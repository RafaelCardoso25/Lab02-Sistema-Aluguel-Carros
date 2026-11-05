package br.pucminas.aluguelcarros.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PedidoRequest(
        @NotNull(message = "Escolha um automóvel.")
        Long automovelId,
        @NotNull(message = "Informe a data de retirada.")
        @FutureOrPresent(message = "A retirada não pode ser no passado.")
        LocalDate dataInicio,
        @NotNull(message = "Informe a data de devolução.")
        LocalDate dataFim) {
}
