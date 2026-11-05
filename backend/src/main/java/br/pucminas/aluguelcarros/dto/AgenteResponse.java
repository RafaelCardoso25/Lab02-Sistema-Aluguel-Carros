package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Perfil;

public record AgenteResponse(Long id, String login, String nome, String cnpj, Perfil tipo) {

    public static AgenteResponse de(Agente agente) {
        return new AgenteResponse(agente.getId(), agente.getLogin(), agente.getNome(), agente.getCnpj(), agente.getPerfil());
    }
}
