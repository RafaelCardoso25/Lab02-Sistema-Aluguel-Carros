package br.pucminas.aluguelcarros.dto;

import br.pucminas.aluguelcarros.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record AgenteCadastroRequest(
        @NotBlank(message = "Informe o usuário.")
        @Size(min = 3, max = 60, message = "Use de 3 a 60 caracteres.")
        String login,
        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, max = 72, message = "Use de 8 a 72 caracteres.")
        String senha,
        @NotBlank(message = "Informe o nome.")
        @Size(max = 120, message = "Use até 120 caracteres.")
        String nome,
        @NotBlank(message = "Informe o CNPJ.")
        @CNPJ(message = "Informe um CNPJ válido.")
        String cnpj,
        @NotNull(message = "Informe o tipo de agente.")
        Perfil tipo) {
}
