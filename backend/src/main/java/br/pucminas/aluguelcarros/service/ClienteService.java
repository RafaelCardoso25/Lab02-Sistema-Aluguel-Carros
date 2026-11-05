package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.ClienteAtualizacaoRequest;
import br.pucminas.aluguelcarros.dto.ClienteCadastroRequest;
import br.pucminas.aluguelcarros.dto.ClienteResponse;
import br.pucminas.aluguelcarros.dto.RendimentoDto;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Cliente;
import br.pucminas.aluguelcarros.repository.ClienteRepository;
import br.pucminas.aluguelcarros.repository.PedidoRepository;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clientes, UsuarioRepository usuarios, PedidoRepository pedidos, PasswordEncoder passwordEncoder) {
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteCadastroRequest requisicao) {
        String login = Normalizador.login(requisicao.login());
        String cpf = Normalizador.digitos(requisicao.cpf());
        String rg = requisicao.rg().trim();
        if (usuarios.existsByLogin(login)) {
            throw new RegraNegocioException("Este usuário já está em uso.");
        }
        if (clientes.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um cliente com este CPF.");
        }
        if (clientes.existsByRg(rg)) {
            throw new RegraNegocioException("Já existe um cliente com este RG.");
        }
        Cliente cliente = new Cliente(
                login,
                passwordEncoder.encode(requisicao.senha()),
                requisicao.nome().trim(),
                rg,
                cpf,
                requisicao.endereco().trim(),
                requisicao.profissao().trim());
        cliente.definirRendimentos(requisicao.rendimentos().stream().map(RendimentoDto::paraEntidade).toList());
        return ClienteResponse.de(clientes.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAllByOrderByNomeAsc().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, UsuarioAutenticado usuario) {
        if (!usuario.perfil().isAgente() && !usuario.id().equals(id)) {
            throw new AcessoNegadoException("Você só pode consultar o próprio cadastro.");
        }
        return ClienteResponse.de(carregar(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteAtualizacaoRequest requisicao, UsuarioAutenticado usuario) {
        exigirTitular(id, usuario);
        Cliente cliente = carregar(id);
        String cpf = Normalizador.digitos(requisicao.cpf());
        String rg = requisicao.rg().trim();
        if (clientes.existsByCpfAndIdNot(cpf, id)) {
            throw new RegraNegocioException("Já existe um cliente com este CPF.");
        }
        if (clientes.existsByRgAndIdNot(rg, id)) {
            throw new RegraNegocioException("Já existe um cliente com este RG.");
        }
        cliente.atualizarDados(requisicao.nome().trim(), rg, cpf, requisicao.endereco().trim(), requisicao.profissao().trim());
        cliente.definirRendimentos(requisicao.rendimentos().stream().map(RendimentoDto::paraEntidade).toList());
        if (requisicao.novaSenha() != null) {
            cliente.alterarSenha(passwordEncoder.encode(requisicao.novaSenha()));
        }
        return ClienteResponse.de(cliente);
    }

    @Transactional
    public void excluir(Long id, UsuarioAutenticado usuario) {
        exigirTitular(id, usuario);
        Cliente cliente = carregar(id);
        if (pedidos.existsByClienteId(id)) {
            throw new RegraNegocioException("Clientes com pedidos registrados não podem ser excluídos.");
        }
        clientes.delete(cliente);
    }

    private void exigirTitular(Long id, UsuarioAutenticado usuario) {
        if (!usuario.id().equals(id)) {
            throw new AcessoNegadoException("Você só pode alterar o próprio cadastro.");
        }
    }

    private Cliente carregar(Long id) {
        return clientes.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }
}
