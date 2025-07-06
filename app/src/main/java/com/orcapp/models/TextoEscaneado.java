package com.orcapp.models;

public class TextoEscaneado {
    private String texto;
    private String fecha;

    public TextoEscaneado(String texto, String fecha) {
        this.texto = texto;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public String getFecha() {
        return fecha;
    }
}
