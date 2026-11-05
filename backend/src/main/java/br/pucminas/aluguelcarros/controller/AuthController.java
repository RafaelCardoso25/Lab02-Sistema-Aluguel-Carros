package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.dto.LoginRequest;
import br.pucminas.aluguelcarros.dto.SessaoResponse;
import br.pucminas.aluguelcarros.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public SessaoResponse entrar(@Valid @RequestBody LoginRequest requisicao) {
        return authService.entrar(requisicao);
    }

    @GetMapping("/me")
    public SessaoResponse sessaoAtual(@AuthenticationPrincipal Jwt jwt) {
        return authService.sessaoAtual(Long.valueOf(jwt.getSubject()), jwt.getTokenValue());
    }
}
