package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Automovel;
import br.pucminas.aluguelcarros.model.Perfil;

public record AutomovelResponse(
        Long id,
        String matricula,
        Integer ano,
        String marca,
        String modelo,
        String placa,
        Long proprietarioId,
        String proprietarioNome,
        Perfil proprietarioPerfil,
        boolean disponivel) {

    public static AutomovelResponse de(Automovel automovel) {
        return new AutomovelResponse(
                automovel.getId(),
                automovel.getMatricula(),
                automovel.getAno(),
                automovel.getMarca(),
                automovel.getModelo(),
                automovel.getPlaca(),
                automovel.getProprietario().getId(),
                automovel.getProprietario().getNome(),
                automovel.getProprietario().getPerfil(),
                automovel.isDisponivel());
    }
}
