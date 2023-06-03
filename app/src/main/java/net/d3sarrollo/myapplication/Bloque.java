package net.d3sarrollo.myapplication;

public class Bloque {


    public Bloque(int ancho, int alto, int radio) {
        // Inicializar los atributos del zombie con valores aleatorio
        // Generar coordenadas aleatorias dentro de la pantalla
        this.x = (int) (Math.random() * ancho);
        this.y = (int) (Math.random() * alto);
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
