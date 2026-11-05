package br.pucminas.aluguelcarros.model;

public enum Perfil {
    CLIENTE,
    EMPRESA,
    BANCO;

    public boolean isAgente() {
        return this != CLIENTE;
    }
}
