package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.dto.ContratoRequest;
import br.pucminas.aluguelcarros.dto.ContratoResponse;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import br.pucminas.aluguelcarros.service.ContratoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratoResponse executar(@Valid @RequestBody ContratoRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return contratoService.executar(requisicao, UsuarioAutenticado.de(jwt));
    }

    @GetMapping
    public List<ContratoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return contratoService.listar(UsuarioAutenticado.de(jwt));
    }

    @GetMapping("/{id}")
    public ContratoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return contratoService.buscar(id, UsuarioAutenticado.de(jwt));
    }
}
