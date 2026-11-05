package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Contrato;
import br.pucminas.aluguelcarros.model.Pedido;
import br.pucminas.aluguelcarros.model.StatusPedido;
import java.time.Instant;
import java.time.LocalDate;

public record PedidoResponse(
        Long id,
        ClienteResponse cliente,
        AutomovelResponse automovel,
        LocalDate dataInicio,
        LocalDate dataFim,
        StatusPedido status,
        String parecer,
        String avaliadoPorNome,
        Instant criadoEm,
        Instant atualizadoEm,
        ContratoResponse contrato) {

    public static PedidoResponse de(Pedido pedido, Contrato contrato) {
        return new PedidoResponse(
                pedido.getId(),
                ClienteResponse.de(pedido.getCliente()),
                AutomovelResponse.de(pedido.getAutomovel()),
                pedido.getDataInicio(),
                pedido.getDataFim(),
                pedido.getStatus(),
                pedido.getParecer(),
                pedido.getAvaliadoPor() == null ? null : pedido.getAvaliadoPor().getNome(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm(),
                contrato == null ? null : ContratoResponse.de(contrato));
    }
}
