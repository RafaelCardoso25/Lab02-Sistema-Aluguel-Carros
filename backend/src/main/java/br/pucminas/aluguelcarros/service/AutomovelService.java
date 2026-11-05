package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AutomovelRequest;
import br.pucminas.aluguelcarros.dto.AutomovelResponse;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Automovel;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.PedidoRepository;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomovelService {

    private final AutomovelRepository automoveis;
    private final AgenteRepository agentes;
    private final PedidoRepository pedidos;

    public AutomovelService(AutomovelRepository automoveis, AgenteRepository agentes, PedidoRepository pedidos) {
        this.automoveis = automoveis;
        this.agentes = agentes;
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar() {
        return automoveis.findAllByOrderByMarcaAscModeloAsc().stream().map(AutomovelResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AutomovelResponse buscar(Long id) {
        return AutomovelResponse.de(carregar(id));
    }

    @Transactional
    public AutomovelResponse cadastrar(AutomovelRequest requisicao, UsuarioAutenticado usuario) {
        Agente agente = agentes.findById(usuario.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
        String placa = Normalizador.placa(requisicao.placa());
        String matricula = requisicao.matricula().trim();
        if (automoveis.existsByPlaca(placa)) {
            throw new RegraNegocioException("Já existe um automóvel com esta placa.");
        }
        if (automoveis.existsByMatricula(matricula)) {
            throw new RegraNegocioException("Já existe um automóvel com esta matrícula.");
        }
        Automovel automovel = new Automovel(matricula, requisicao.ano(), requisicao.marca().trim(), requisicao.modelo().trim(), placa, agente);
        return AutomovelResponse.de(automoveis.save(automovel));
    }

    @Transactional
    public AutomovelResponse atualizar(Long id, AutomovelRequest requisicao, UsuarioAutenticado usuario) {
        Automovel automovel = carregar(id);
        exigirProprietario(automovel, usuario);
        String placa = Normalizador.placa(requisicao.placa());
        String matricula = requisicao.matricula().trim();
        if (automoveis.existsByPlacaAndIdNot(placa, id)) {
            throw new RegraNegocioException("Já existe um automóvel com esta placa.");
        }
        if (automoveis.existsByMatriculaAndIdNot(matricula, id)) {
            throw new RegraNegocioException("Já existe um automóvel com esta matrícula.");
        }
        automovel.atualizar(matricula, requisicao.ano(), requisicao.marca().trim(), requisicao.modelo().trim(), placa);
        return AutomovelResponse.de(automovel);
    }

    @Transactional
    public void excluir(Long id, UsuarioAutenticado usuario) {
        Automovel automovel = carregar(id);
        exigirProprietario(automovel, usuario);
        if (pedidos.existsByAutomovelId(id)) {
            throw new RegraNegocioException("Automóveis com pedidos registrados não podem ser excluídos.");
        }
        automoveis.delete(automovel);
    }

    private void exigirProprietario(Automovel automovel, UsuarioAutenticado usuario) {
        if (!automovel.getProprietario().getId().equals(usuario.id())) {
            throw new AcessoNegadoException("Somente o proprietário pode alterar este automóvel.");
        }
    }

    private Automovel carregar(Long id) {
        return automoveis.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado."));
    }
}
