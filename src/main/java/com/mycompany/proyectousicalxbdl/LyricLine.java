package com.mycompany.proyectousicalxbdl;

import javafx.util.Duration;

public class LyricLine {
    private final Duration tiempo;
    private final String texto;

    public LyricLine(Duration tiempo, String texto) {
        this.tiempo = tiempo;
        this.texto = texto;
    }

    public Duration getTiempo() {
        return tiempo;
    }

    public String getTexto() {
        return texto;
    }
}
