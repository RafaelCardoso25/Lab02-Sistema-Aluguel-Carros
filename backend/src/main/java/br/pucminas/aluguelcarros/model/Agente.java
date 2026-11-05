package br.pucminas.aluguelcarros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "agentes")
@PrimaryKeyJoinColumn(name = "id")
public abstract class Agente extends Usuario {

    @Column(nullable = false, unique = true)
    private String cnpj;

    protected Agente() {
    }

    protected Agente(String login, String senhaHash, String nome, String cnpj) {
        super(login, senhaHash, nome);
        this.cnpj = cnpj;
    }

    public String getCnpj() {
        return cnpj;
    }
}
