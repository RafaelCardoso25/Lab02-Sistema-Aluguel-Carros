package br.pucminas.aluguelcarros.service;

import java.util.Locale;

final class Normalizador {

    private Normalizador() {
    }

    static String login(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    static String digitos(String valor) {
        return valor.replaceAll("\\D", "");
    }

    static String placa(String valor) {
        return valor.replace("-", "").trim().toUpperCase(Locale.ROOT);
    }
}
