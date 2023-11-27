package net.d3sarrollo.zombie_defense_random_maps.model.object;

public class Nivel {

    public Nivel (int zombies, int velZombie, int bloques) {
        setCantidadZombies(zombies);
        setVelocidadZombies(velZombie);
        setCantidadBloques(bloques);
    }

    public int getCantidadZombies() {
        return cantidadZombies;
    }

    public void setCantidadZombies(int cantidadZombies) {
        this.cantidadZombies = cantidadZombies;
    }

    public int getVelocidadZombies() {
        return velocidadZombies;
    }

    public void setVelocidadZombies(int velocidadZombies) {
        this.velocidadZombies = velocidadZombies;
    }

    public int getCantidadBloques() {
        return cantidadBloques;
    }

    public void setCantidadBloques(int cantidadBloques) {
        this.cantidadBloques = cantidadBloques;
    }

    private int cantidadZombies;
    private int velocidadZombies;
    private int cantidadBloques;


}
