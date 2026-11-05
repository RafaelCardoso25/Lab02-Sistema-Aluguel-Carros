package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.TipoContrato;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ContratoRequest(
        @NotNull(message = "Informe o pedido.")
        Long pedidoId,
        @NotNull(message = "Escolha o tipo de contrato.")
        TipoContrato tipo,
        @NotNull(message = "Informe o valor do contrato.")
        @DecimalMin(value = "0.01", message = "O valor precisa ser maior que zero.")
        @Digits(integer = 10, fraction = 2, message = "Use até 2 casas decimais.")
        BigDecimal valor,
        @Valid
        ContratoCreditoDto credito) {
}
