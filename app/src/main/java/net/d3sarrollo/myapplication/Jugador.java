package net.d3sarrollo.myapplication;

import android.content.Context;
import android.util.AttributeSet;

public class Jugador {

    private int coorX, coorY;
    private int size;

    private int vel;

    public boolean isColisionConBloque() {
        return colisionConBloque;
    }

    public void setColisionConBloque(boolean colisionConBloque) {
        this.colisionConBloque = colisionConBloque;
    }

    private boolean colisionConBloque = false;

    public int getCoorX() {
        return coorX;
    }

    public void setCoorX(int coorX) {
        this.coorX = coorX;
    }

    public int getCoorY() {
        return coorY;
    }

    public void setCoorY(int coorY) {
        this.coorY = coorY;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }




    public Jugador (int cX, int cY, int t, int v){
        setCoorX(cX);
        setCoorY(cY);
        setSize(t);
        setVel(v);
    }

    public Jugador (){
    }


    public int getVel() {
        return vel;
    }

    public void setVel(int vel) {
        this.vel = vel;
    }

    public Boolean verificarColisionBloque(Bloque bloque) {
        float distancia = calcularDistancia(getCoorX(), getCoorY(), bloque.getX(), bloque.getY());
        if (distancia < getSize() + bloque.getRadio()) {
            return true;
        } else {
            return false;
        }
    }

    public float calcularDistancia(float x1, float y1, float x2, float y2) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
