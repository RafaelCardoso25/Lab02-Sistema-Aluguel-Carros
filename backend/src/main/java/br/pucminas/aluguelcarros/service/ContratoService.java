package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.ContratoCreditoDto;
import br.pucminas.aluguelcarros.dto.ContratoRequest;
import br.pucminas.aluguelcarros.dto.ContratoResponse;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Banco;
import br.pucminas.aluguelcarros.model.Contrato;
import br.pucminas.aluguelcarros.model.ContratoCredito;
import br.pucminas.aluguelcarros.model.Pedido;
import br.pucminas.aluguelcarros.model.Perfil;
import br.pucminas.aluguelcarros.model.TipoContrato;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.ContratoRepository;
import br.pucminas.aluguelcarros.repository.PedidoRepository;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratoService {

    private final ContratoRepository contratos;
    private final PedidoRepository pedidos;
    private final AgenteRepository agentes;

    public ContratoService(ContratoRepository contratos, PedidoRepository pedidos, AgenteRepository agentes) {
        this.contratos = contratos;
        this.pedidos = pedidos;
        this.agentes = agentes;
    }

    @Transactional
    public ContratoResponse executar(ContratoRequest requisicao, UsuarioAutenticado usuario) {
        Pedido pedido = pedidos.findById(requisicao.pedidoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
        Agente agente = agentes.findById(usuario.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
        validarTipo(requisicao, agente);
        if (!pedido.getAutomovel().isDisponivel()) {
            throw new RegraNegocioException("Este automóvel já pertence a um cliente e não está disponível.");
        }
        if (pedidos.existeContratoNoPeriodo(pedido.getAutomovel().getId(), pedido.getDataInicio(), pedido.getDataFim(), pedido.getId())) {
            throw new RegraNegocioException("Este automóvel já tem contrato nesse período.");
        }
        pedido.registrarContrato();
        pedido.getAutomovel().registrarProprietario(definirProprietario(requisicao.tipo(), pedido, agente));
        Contrato contrato = new Contrato(pedido, agente, requisicao.tipo(), requisicao.valor(), montarCredito(requisicao.credito(), agente));
        return ContratoResponse.de(contratos.save(contrato));
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listar(UsuarioAutenticado usuario) {
        List<Contrato> encontrados = usuario.perfil().isAgente()
                ? contratos.findAllByOrderByIdDesc()
                : contratos.findByPedidoClienteIdOrderByIdDesc(usuario.id());
        return encontrados.stream().map(ContratoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ContratoResponse buscar(Long id, UsuarioAutenticado usuario) {
        Contrato contrato = contratos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contrato não encontrado."));
        if (!usuario.perfil().isAgente() && !contrato.getPedido().getCliente().getId().equals(usuario.id())) {
            throw new AcessoNegadoException("Este contrato pertence a outro cliente.");
        }
        return ContratoResponse.de(contrato);
    }

    private void validarTipo(ContratoRequest requisicao, Agente agente) {
        boolean comCredito = requisicao.tipo() == TipoContrato.ALUGUEL_COM_CREDITO;
        if (requisicao.tipo() == TipoContrato.ALUGUEL_SIMPLES && agente.getPerfil() != Perfil.EMPRESA) {
            throw new RegraNegocioException("Somente empresas executam aluguel simples.");
        }
        if (comCredito && agente.getPerfil() != Perfil.BANCO) {
            throw new RegraNegocioException("Somente bancos concedem contrato de crédito.");
        }
        if (comCredito && requisicao.credito() == null) {
            throw new RegraNegocioException("Informe os dados do contrato de crédito.");
        }
        if (!comCredito && requisicao.credito() != null) {
            throw new RegraNegocioException("Este tipo de contrato não aceita crédito.");
        }
    }

    private Usuario definirProprietario(TipoContrato tipo, Pedido pedido, Agente agente) {
        return tipo == TipoContrato.ALUGUEL_COM_COMPRA ? pedido.getCliente() : agente;
    }

    private ContratoCredito montarCredito(ContratoCreditoDto credito, Agente agente) {
        if (credito == null) {
            return null;
        }
        Banco banco = (Banco) Hibernate.unproxy(agente);
        return new ContratoCredito(banco, credito.valor(), credito.taxaJurosMensal(), credito.parcelas());
    }
}
