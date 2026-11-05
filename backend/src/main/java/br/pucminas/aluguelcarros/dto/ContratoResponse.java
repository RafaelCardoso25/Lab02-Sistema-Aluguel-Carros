package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Contrato;
import br.pucminas.aluguelcarros.model.ContratoCredito;
import br.pucminas.aluguelcarros.model.Perfil;
import br.pucminas.aluguelcarros.model.TipoContrato;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoResponse(
        Long id,
        Long pedidoId,
        String clienteNome,
        AutomovelResponse automovel,
        String agenteNome,
        Perfil agentePerfil,
        TipoContrato tipo,
        BigDecimal valor,
        LocalDate dataAssinatura,
        Credito credito) {

    public record Credito(String bancoNome, BigDecimal valor, BigDecimal taxaJurosMensal, Integer parcelas) {

        static Credito de(ContratoCredito credito) {
            return credito == null ? null : new Credito(
                    credito.getBanco().getNome(),
                    credito.getValor(),
                    credito.getTaxaJurosMensal(),
                    credito.getParcelas());
        }
    }

    public static ContratoResponse de(Contrato contrato) {
        return new ContratoResponse(
                contrato.getId(),
                contrato.getPedido().getId(),
                contrato.getPedido().getCliente().getNome(),
                AutomovelResponse.de(contrato.getPedido().getAutomovel()),
                contrato.getAgente().getNome(),
                contrato.getAgente().getPerfil(),
                contrato.getTipo(),
                contrato.getValor(),
                contrato.getDataAssinatura(),
                Credito.de(contrato.getContratoCredito()));
    }
}
