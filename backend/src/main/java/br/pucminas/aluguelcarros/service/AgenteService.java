package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AgenteCadastroRequest;
import br.pucminas.aluguelcarros.dto.AgenteResponse;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Banco;
import br.pucminas.aluguelcarros.model.Empresa;
import br.pucminas.aluguelcarros.model.Perfil;
import br.pucminas.aluguelcarros.repository.AgenteRepository;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenteService {

    private final AgenteRepository agentes;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(AgenteRepository agentes, UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
        this.agentes = agentes;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AgenteResponse cadastrar(AgenteCadastroRequest requisicao) {
        if (!requisicao.tipo().isAgente()) {
            throw new RegraNegocioException("O agente precisa ser uma empresa ou um banco.");
        }
        String login = Normalizador.login(requisicao.login());
        String cnpj = Normalizador.digitos(requisicao.cnpj());
        if (usuarios.existsByLogin(login)) {
            throw new RegraNegocioException("Este usuário já está em uso.");
        }
        if (agentes.existsByCnpj(cnpj)) {
            throw new RegraNegocioException("Já existe um agente com este CNPJ.");
        }
        String hash = passwordEncoder.encode(requisicao.senha());
        String nome = requisicao.nome().trim();
        Agente agente = requisicao.tipo() == Perfil.BANCO
                ? new Banco(login, hash, nome, cnpj)
                : new Empresa(login, hash, nome, cnpj);
        return AgenteResponse.de(agentes.save(agente));
    }
}
