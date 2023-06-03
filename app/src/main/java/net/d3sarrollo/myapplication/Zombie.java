package net.d3sarrollo.myapplication;

import android.graphics.Rect;

public class Zombie {
    private int x; // Posición en el eje X
    private int y; // Posición en el eje Y
    private int size; // Tamaño del zombie
    private int speed; // Velocidad de movimiento del zombie

    private double distanceToPlayer;


    public Zombie(int ancho, int alto, int tamano, int vel) {
        // Inicializar los atributos del zombie con valores aleatorios
        int screenWidth = ancho;
        int screenHeight = alto;

        // Generar coordenadas aleatorias fuera de la pantalla
        int side = (int) (Math.random() * 4); // 0: arriba, 1: derecha, 2: abajo, 3: izquierda
        int x, y;

        size = tamano;
        speed = vel;

        switch (side) {
            case 0: // Arriba
                x = (int) (Math.random() * screenWidth);
                y = -size-50;
                break;
            case 1: // Derecha
                x = screenWidth + size+50;
                y = (int) (Math.random() * screenHeight);
                break;
            case 2: // Abajo
                x = (int) (Math.random() * screenWidth);
                y = screenHeight + size+50;
                break;
            case 3: // Izquierda
                x = -size-50;
                y = (int) (Math.random() * screenHeight);
                break;
            default: // Por defecto, usar coordenadas aleatorias en la pantalla
                x = (int) (Math.random() * (screenWidth - 200)) + 500;
                y = (int) (Math.random() * (screenHeight - 200)) + 500;
                break;
        }

        this.x = x;
        this.y = y;

    }

    public void updatePosition() {
        // Mover el zombie hacia abajo
        y += speed;
    }

    protected int getSize(){
        return this.size;
    }

    protected void setSize(int s){
        this.size = s;
    }

    protected int getSpeed(){
        return this.speed;
    }

    protected int getX(){
        return this.x;
    }

    protected int getY(){
        return this.y;
    }

    protected void setX(int cX){
        this.x = cX;
    }

    protected void setY(int cY){
        this.y = cY;
    }

    public boolean checkCollision(Zombie otherZombie) {
        // Verificar si hay colisión entre el zombie actual y otro zombie
        float distance = (float) Math.sqrt(Math.pow(otherZombie.getX() - getX(), 2) + Math.pow(otherZombie.getY() - getY(), 2));
        boolean choque = false;
        if (distance < (2*getSize())) {
            choque = true;
        }
        return choque;
    }

    public boolean isHitBy(Bala bala) {
        // Calcular la distancia entre el centro del zombie y la posición de la bala
        double distance = Math.sqrt(Math.pow(bala.getX() - (x + size/2), 2) + Math.pow(bala.getY() - (y + size/2), 2));

        // Verificar si la distancia es menor o igual al radio del círculo del zombie
        return distance <= size;
    }


    public double getDistanceToPlayer() {
        return distanceToPlayer;
    }

    public void setDistanceToPlayer(double distanceToPlayer) {
        this.distanceToPlayer = distanceToPlayer;
    }
}