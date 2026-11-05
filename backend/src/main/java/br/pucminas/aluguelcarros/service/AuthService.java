package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.LoginRequest;
import br.pucminas.aluguelcarros.dto.SessaoResponse;
import br.pucminas.aluguelcarros.exception.CredenciaisInvalidasException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import br.pucminas.aluguelcarros.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, TokenService tokens) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
    }

    @Transactional(readOnly = true)
    public SessaoResponse entrar(LoginRequest requisicao) {
        Usuario usuario = usuarios.findByLogin(Normalizador.login(requisicao.login()))
                .filter(encontrado -> passwordEncoder.matches(requisicao.senha(), encontrado.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return new SessaoResponse(tokens.gerar(usuario), usuario.getId(), usuario.getNome(), usuario.getPerfil());
    }

    @Transactional(readOnly = true)
    public SessaoResponse sessaoAtual(Long usuarioId, String token) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return new SessaoResponse(token, usuario.getId(), usuario.getNome(), usuario.getPerfil());
    }
}
