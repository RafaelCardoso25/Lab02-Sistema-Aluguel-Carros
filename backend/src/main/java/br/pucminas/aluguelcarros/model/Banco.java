package br.pucminas.aluguelcarros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "bancos")
@PrimaryKeyJoinColumn(name = "id")
public class Banco extends Agente {

    protected Banco() {
    }

    public Banco(String login, String senhaHash, String nome, String cnpj) {
        super(login, senhaHash, nome, cnpj);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.BANCO;
    }
}
