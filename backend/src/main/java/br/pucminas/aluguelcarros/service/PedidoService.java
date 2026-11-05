package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AvaliacaoRequest;
import br.pucminas.aluguelcarros.dto.PedidoRequest;
import br.pucminas.aluguelcarros.dto.PedidoResponse;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Automovel;
import br.pucminas.aluguelcarros.model.Cliente;
import br.pucminas.aluguelcarros.model.Pedido;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.ClienteRepository;
import br.pucminas.aluguelcarros.repository.ContratoRepository;
import br.pucminas.aluguelcarros.repository.PedidoRepository;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private static final Long NENHUM_PEDIDO = -1L;

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;
    private final AutomovelRepository automoveis;
    private final ContratoRepository contratos;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, AgenteRepository agentes, AutomovelRepository automoveis, ContratoRepository contratos) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.agentes = agentes;
        this.automoveis = automoveis;
        this.contratos = contratos;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest requisicao, UsuarioAutenticado usuario) {
        Cliente cliente = clientes.findById(usuario.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
        Automovel automovel = carregarAutomovelElegivel(requisicao.automovelId(), requisicao.dataInicio(), requisicao.dataFim(), NENHUM_PEDIDO);
        Pedido pedido = pedidos.save(new Pedido(cliente, automovel, requisicao.dataInicio(), requisicao.dataFim()));
        return resposta(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(UsuarioAutenticado usuario) {
        List<Pedido> encontrados = usuario.perfil().isAgente()
                ? pedidos.findAllByOrderByCriadoEmDesc()
                : pedidos.findByClienteIdOrderByCriadoEmDesc(usuario.id());
        return encontrados.stream().map(this::resposta).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, UsuarioAutenticado usuario) {
        Pedido pedido = carregar(id);
        exigirAcesso(pedido, usuario);
        return resposta(pedido);
    }

    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest requisicao, UsuarioAutenticado usuario) {
        Pedido pedido = carregar(id);
        exigirAcesso(pedido, usuario);
        Automovel automovel = carregarAutomovelElegivel(requisicao.automovelId(), requisicao.dataInicio(), requisicao.dataFim(), id);
        if (usuario.perfil().isAgente()) {
            pedido.modificarPeloAgente(automovel, requisicao.dataInicio(), requisicao.dataFim());
        } else {
            pedido.modificarPeloCliente(automovel, requisicao.dataInicio(), requisicao.dataFim());
        }
        return resposta(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, UsuarioAutenticado usuario) {
        Pedido pedido = carregar(id);
        exigirAcesso(pedido, usuario);
        pedido.cancelar();
        return resposta(pedido);
    }

    @Transactional
    public PedidoResponse avaliar(Long id, AvaliacaoRequest requisicao, UsuarioAutenticado usuario) {
        Pedido pedido = carregar(id);
        Agente agente = agentes.findById(usuario.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
        pedido.avaliar(agente, requisicao.aprovado(), requisicao.parecer().trim());
        return resposta(pedido);
    }

    private Automovel carregarAutomovelElegivel(Long automovelId, LocalDate dataInicio, LocalDate dataFim, Long pedidoIgnorado) {
        Automovel automovel = automoveis.findById(automovelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado."));
        if (!automovel.isDisponivel()) {
            throw new RegraNegocioException("Este automóvel já pertence a um cliente e não está disponível.");
        }
        if (pedidos.existeContratoNoPeriodo(automovelId, dataInicio, dataFim, pedidoIgnorado)) {
            throw new RegraNegocioException("Este automóvel já tem contrato nesse período.");
        }
        return automovel;
    }

    private void exigirAcesso(Pedido pedido, UsuarioAutenticado usuario) {
        if (!usuario.perfil().isAgente() && !pedido.getCliente().getId().equals(usuario.id())) {
            throw new AcessoNegadoException("Este pedido pertence a outro cliente.");
        }
    }

    private Pedido carregar(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
    }

    private PedidoResponse resposta(Pedido pedido) {
        return PedidoResponse.de(pedido, contratos.findByPedidoId(pedido.getId()).orElse(null));
    }
}
