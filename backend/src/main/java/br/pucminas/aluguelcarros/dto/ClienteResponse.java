package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Cliente;
import java.math.BigDecimal;
import java.util.List;

public record ClienteResponse(
        Long id,
        String login,
        String nome,
        String rg,
        String cpf,
        String endereco,
        String profissao,
        List<RendimentoDto> rendimentos,
        BigDecimal rendaTotal) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getLogin(),
                cliente.getNome(),
                cliente.getRg(),
                cliente.getCpf(),
                cliente.getEndereco(),
                cliente.getProfissao(),
                cliente.getRendimentos().stream().map(RendimentoDto::de).toList(),
                cliente.getRendaTotal());
    }
}
