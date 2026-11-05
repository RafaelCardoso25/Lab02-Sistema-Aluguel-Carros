package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.dto.AvaliacaoRequest;
import br.pucminas.aluguelcarros.dto.PedidoRequest;
import br.pucminas.aluguelcarros.dto.PedidoResponse;
import br.pucminas.aluguelcarros.security.UsuarioAutenticado;
import br.pucminas.aluguelcarros.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.criar(requisicao, UsuarioAutenticado.de(jwt));
    }

    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return pedidoService.listar(UsuarioAutenticado.de(jwt));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.buscar(id, UsuarioAutenticado.de(jwt));
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @Valid @RequestBody PedidoRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.modificar(id, requisicao, UsuarioAutenticado.de(jwt));
    }

    @PostMapping("/{id}/cancelamento")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.cancelar(id, UsuarioAutenticado.de(jwt));
    }

    @PostMapping("/{id}/avaliacao")
    public PedidoResponse avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest requisicao, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.avaliar(id, requisicao, UsuarioAutenticado.de(jwt));
    }
}
