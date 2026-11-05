package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.dto.AutomovelRequest;
import br.pucminas.aluguelcarros.dto.AutomovelResponse;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import br.pucminas.aluguelcarros.service.AutomovelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResponse> listar() {
        return automovelService.listar();
    }

    @GetMapping("/{id}")
    public AutomovelResponse buscar(@PathVariable Long id) {
        return automovelService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelResponse cadastrar(@Valid @RequestBody AutomovelRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return automovelService.cadastrar(requisicao, UsuarioAutenticado.de(jwt));
    }

    @PutMapping("/{id}")
    public AutomovelResponse atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return automovelService.atualizar(id, requisicao, UsuarioAutenticado.de(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        automovelService.excluir(id, UsuarioAutenticado.de(jwt));
    }
}
