package net.d3sarrollo.myapplication;

public class Bala {
    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private String direccion;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelocidadX() {
        return velocidadX;
    }

    public void setVelocidadX(float velocidadX) {
        this.velocidadX = velocidadX;
    }

    public float getVelocidadY() {
        return velocidadY;
    }

    public void setVelocidadY(float velocidadY) {
        this.velocidadY = velocidadY;
    }

    public Bala(float x, float y, String d) {
        this.setX(x);
        this.setY(y);
        this.setDireccion(d);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String d) {
        this.direccion = d;
        if (direccion.equals("UP")){
            this.velocidadX = 0;
            this.velocidadY = -12;
        } else if (direccion.equals("DOWN")){
            this.velocidadX = 0;
            this.velocidadY = 12;
        } else if (direccion.equals("DOWN_RIGHT")){
            this.velocidadX = 6;
            this.velocidadY = 6;
        } else if (direccion.equals("UP_RIGHT")){
            this.velocidadX = 6;
            this.velocidadY = -6;
        } else if (direccion.equals("RIGHT")){
            this.velocidadX = 12;
            this.velocidadY = 0;
        } else if (direccion.equals("DOWN_LEFT")){
            this.velocidadX = -6;
            this.velocidadY = 6;
        } else if (direccion.equals("UP_LEFT")){
            this.velocidadX = -6;
            this.velocidadY = -6;
        } else if (direccion.equals("LEFT")){
            this.velocidadX = -12;
            this.velocidadY = 0;
        }
    }

    public void actualizar() {
        // Actualizar la posición de la bala en función de su velocidad y dirección
        x += velocidadX;
        y += velocidadY;
    }
}
