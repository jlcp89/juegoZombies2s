package net.d3sarrollo.myapplication;

public class Bloque {


    public Bloque(int ancho, int alto, int radio, int jugadorX, int jugadorY) {
        // Generar coordenadas aleatorias dentro de la pantalla
        int margen = 100; // Margen mínimo de 100 píxeles desde los bordes
        int tamañoBloque = 60; // Ancho del bloque
        int tamañoJugador = 50; // Ancho del jugador

        int margenX = margen + tamañoBloque; // Considerar el tamaño del bloque al calcular el margen
        int margenY = margen + tamañoBloque; // Considerar el tamaño del bloque al calcular el margen
        int rangoX = ancho - 2 * margen - tamañoBloque; // Restar el tamaño del bloque al calcular el rango válido
        int rangoY = alto - 2 * margen - tamañoBloque; // Restar el tamaño del bloque al calcular el rango válido

        boolean bloqueSobreJugador;
        do {
            this.x = (int) (Math.random() * rangoX) + margenX;
            this.y = (int) (Math.random() * rangoY) + margenY;

            // Verificar si el bloque se superpone con la posición del jugador
            bloqueSobreJugador = this.x - radio < jugadorX + tamañoJugador &&
                    this.x + radio > jugadorX - tamañoJugador &&
                    this.y - radio < jugadorY + tamañoJugador &&
                    this.y + radio > jugadorY - tamañoJugador;
        } while (bloqueSobreJugador);

        this.radio = radio;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    private int x;
    private int y;
    private int radio;

}
