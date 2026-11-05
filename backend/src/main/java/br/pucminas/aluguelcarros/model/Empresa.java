package br.pucminas.aluguelcarros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
@PrimaryKeyJoinColumn(name = "id")
public class Empresa extends Agente {

    protected Empresa() {
    }

    public Empresa(String login, String senhaHash, String nome, String cnpj) {
        super(login, senhaHash, nome, cnpj);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.EMPRESA;
    }
}
