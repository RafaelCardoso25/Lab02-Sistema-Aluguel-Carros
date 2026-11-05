package br.pucminas.aluguelcarros.controller;

import br.pucminas.aluguelcarros.dto.AgenteCadastroRequest;
import br.pucminas.aluguelcarros.dto.AgenteResponse;
import br.pucminas.aluguelcarros.service.AgenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agentes")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrar(@Valid @RequestBody AgenteCadastroRequest requisicao) {
        return agenteService.cadastrar(requisicao);
    }
}
