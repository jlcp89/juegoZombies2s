package net.d3sarrollo.zombie_defense_random_maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;


import androidx.core.content.ContextCompat;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private Paint paint;
    private int anchoPantalla, altoPantalla;
    private Thread gameThread;
    private boolean running;
    private String directionJ1 = "UP";

    private Bitmap currentImage;

    private Jugador player1;

    private List<Bala> balas;

    private List<Zombie> zombies;

    private List<Bloque> bloques;
    private List<Nivel> niveles;



    private int prevX, prevY;
    private int joystickX = 0;
    private int joystickY = 0;

    private float joystickAngle = 0;

    private int sizeJoystick = 150;

    protected byte[] getBalasByte (){
        return this.balas.toString().getBytes();
    }

    protected byte[] getZobiesByte (){
        return this.zombies.toString().getBytes();
    }

    public boolean isFinDelJuego() {
        return finDelJuego;
    }

    public void setFinDelJuego(boolean finDelJuego) {
        this.finDelJuego = finDelJuego;
    }

    private boolean finDelJuego = false;
    private int contadorNivel = 0;

    private int initialCoorX;
    private int initialCoorY;
    private int contadorZombiesEliminados = 0;

    public int getContadorZombiesEliminados() {
        return contadorZombiesEliminados;
    }


    public void setContadorZombiesEliminados(int contadorZombiesEliminados) {
        this.contadorZombiesEliminados = contadorZombiesEliminados;
    }

    public int getNivelAcutal() {
        return this.contadorNivel;
    }
    private Context context;
    private int contadorZombiesNivel = 0;
    private boolean rapido = false;





    private void generarNiveles(){
        niveles = new ArrayList<>();
        Nivel nivelN = new Nivel( 30,2,15);
        niveles.add(nivelN);
    }

    protected void generarZombies(){
        Nivel n1 = this.niveles.get(contadorNivel);
        int cantZombies = n1.getCantidadZombies();
        int velZombie = n1.getVelocidadZombies();
        zombies.clear();
        for (int i = 0; i < cantZombies; i++) {
            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, velZombie);
            zombies.add(z);
        }
    }

    protected void generarBloques(){
        Nivel n = niveles.get(contadorNivel);
        int cantBloques = n.getCantidadBloques();
        bloques.clear();
        for (int i = 0; i < cantBloques; i++) {
            Bloque b = new Bloque(anchoPantalla,altoPantalla, 25, player1.getCoorX(), player1.getCoorY());
            bloques.add(b);
        }
    }

    protected void generarNivel(){
        if (contadorNivel <= niveles.size()){
            this.generarZombies();
            this.generarBloques();
            this.contadorNivel = this.contadorNivel + 1;
        }
    }


    public GameView(Context context) {
        super(context);
        this.context = context;
        paint = new Paint();
        generarNiveles();

        setOnTouchListener(this);
        getHolder().addCallback(this);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        player1 = new Jugador(screenWidth / 2, screenHeight / 2, 50, 20);
        balas = new ArrayList<>();
        zombies = new ArrayList<>();
        bloques = new ArrayList<>();

        initialCoorX = player1.getCoorX();
        initialCoorY = player1.getCoorY();

        anchoPantalla = screenWidth;
        altoPantalla= screenHeight;
        joystickX = 200;
        joystickY = altoPantalla - 200;
    }



    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    updateBullets();
                    updateZombies();
                    draw();
                    if (zombies.size()<1){
                        generarNivel();
                    }
                }
            }
        });
        gameThread.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // No se necesita implementar aquí
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void draw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            if (canvas != null) {

                synchronized (getHolder()) {
                    // Limpiar el fondo del lienzo
                    paint.setColor(Color.WHITE);
                    canvas.drawPaint(paint);

                    /*//Dibujar control
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(joystickX,joystickY,sizeJoystick, paint);
                    //Dibujar control
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(joystickX,joystickY,10, paint);

                    // Dibujar las líneas blancas
                    paint.setColor(Color.WHITE);

                    int NUM_DIVISIONS = 8;
                    int CIRCLE_RADIUS = 200;
                    int LINE_WIDTH = 5;
                    int LINE_LENGTH = CIRCLE_RADIUS * 2;

                    for (int i = 0; i < NUM_DIVISIONS; i++) {
                        float angle = (float) (2 * Math.PI * i / NUM_DIVISIONS);
                        float startX = joystickX ;
                        float startY = joystickY ;
                        float endX = joystickX + (float) ((CIRCLE_RADIUS + LINE_LENGTH) * Math.cos(angle));
                        float endY = joystickY + (float) ((CIRCLE_RADIUS + LINE_LENGTH) * Math.sin(angle));
                        canvas.drawLine(startX, startY, endX, endY, paint);
                    }*/

                    paint.setColor(Color.BLACK);
                    int cellSize = 40; // Tamaño de la celda en píxeles

                    int width = anchoPantalla;
                    int height = altoPantalla;

                    // Dibujar líneas verticales
                    for (int x = 0; x <= width; x += cellSize) {
                        canvas.drawLine(x, 0, x, height, paint);
                    }

                    // Dibujar líneas horizontales
                    for (int y = 0; y <= height; y += cellSize) {
                        canvas.drawLine(0, y, width, y, paint);
                    }


                    //Dibujar jugador
                    // Obtener el drawable "soldado_arriba" desde los recursos
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_arriba);

                    switch (directionJ1) {
                        case "UP":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_arriba);
                            break;
                        case "DOWN":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_abajo);
                            break;
                        case "LEFT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_izquierda);
                            break;
                        case "RIGHT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_derecha);
                            break;
                        case "UP_LEFT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_arriba_izquierda);
                            break;
                        case "UP_RIGHT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_arriba_derecha);
                            break;
                        case "DOWN_LEFT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_abajo_izquierda);
                            break;
                        case "DOWN_RIGHT":
                            soldadoArribaDrawable = getResources().getDrawable(R.drawable.soldado_abajo_derecha);
                            break;
                    }


                    // Obtener las dimensiones del drawable
                    int drawableWidth = soldadoArribaDrawable.getIntrinsicWidth();
                    int drawableHeight = soldadoArribaDrawable.getIntrinsicHeight();

                    // Calcular las coordenadas de dibujo
                    int left = player1.getCoorX() - drawableWidth / 2;
                    int top = player1.getCoorY() - drawableHeight / 2;
                    int right = player1.getCoorX() + drawableWidth / 2;
                    int bottom = player1.getCoorY() + drawableHeight / 2;

                    // Dibujar el drawable en el lienzo
                    soldadoArribaDrawable.setBounds(left, top, right, bottom);
                    soldadoArribaDrawable.draw(canvas);

                    // Dibujar bloques
                    if (bloques.size() > 0) {
                        paint.setColor(Color.DKGRAY);
                        for (int i = 0; i < bloques.size(); i++) {
                            Bloque b = bloques.get(i);
                            // Realiza las operaciones necesarias con el bloque en la posición i
                            float lefts = b.getX() - b.getRadio(); // Coordenada x de la esquina superior izquierda del cuadrado
                            float tops = b.getY() - b.getRadio(); // Coordenada y de la esquina superior izquierda del cuadrado
                            float rights = b.getX() + b.getRadio(); // Coordenada x de la esquina inferior derecha del cuadrado
                            float bottoms = b.getY() + b.getRadio(); // Coordenada y de la esquina inferior derecha del cuadrado
                            canvas.drawRect(lefts, tops, rights, bottoms, paint);
                        }
                    }

                    // Obtener el drawable "zombie_arriba" desde los recursos
                    Drawable zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_arriba);

                    for (int i = 0; i < zombies.size(); i++) {
                        Zombie z = zombies.get(i);

                        if (z.getDireccion() == null){
                            z.setDireccion("UP");
                        }

                        switch (z.getDireccion()) {
                            case "UP":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_arriba);
                                break;
                            case "DOWN":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_abajo);
                                break;
                            case "LEFT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_izquierda);
                                break;
                            case "RIGHT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_derecha);
                                break;
                            /*case "UP_LEFT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_arriba_izquierda);
                                break;
                            case "UP_RIGHT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_arriba_derecha);
                                break;
                            case "DOWN_LEFT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_abajo_izquierda);
                                break;
                            case "DOWN_RIGHT":
                                zombieArribaDrawable = getResources().getDrawable(R.drawable.zombie_abajo_derecha);
                                break;*/
                        }

                        // Calcular las coordenadas de dibujo
                        int leftz = z.getX() - zombieArribaDrawable.getIntrinsicWidth() / 2;
                        int topz = z.getY() - zombieArribaDrawable.getIntrinsicHeight() / 2;
                        int rightz = z.getX() + zombieArribaDrawable.getIntrinsicWidth() / 2;
                        int bottomz = z.getY() + zombieArribaDrawable.getIntrinsicHeight() / 2;

                        // Dibujar el drawable en el lienzo
                        zombieArribaDrawable.setBounds(leftz, topz, rightz, bottomz);
                        zombieArribaDrawable.draw(canvas);
                    }


                    //Dibujar balas
                    if (balas.size()>0){
                        int colorBala = ContextCompat.getColor(this.context, R.color.green);

                        paint.setColor(colorBala);
                        for (int i = 0; i < balas.size(); i++) {
                            Bala bala = balas.get(i);
                            // Realiza las operaciones necesarias con la bala en la posición i
                            canvas.drawCircle(bala.getX(),bala.getY(),5, paint);
                        }
                    }



                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public void updateBullets() {
        for (int i = 0; i < balas.size(); i++) {
            Bala bala = balas.get(i);
            // Actualizar la posición de la bala
            bala.actualizar(); // Reemplaza 'actualizar()' por el método correspondiente para actualizar la posición de la bala

            // Verificar si la bala está fuera de la pantalla y eliminarla si es necesario
            if (bala.getX() < 0 || bala.getX() > anchoPantalla || bala.getY() < 0 || bala.getY() > altoPantalla) {
                balas.remove(i);
                i--; // Actualizar el índice para evitar omitir balas después de eliminar una
            }
        }
    }

    public void updateZombies() {
        // Iterar sobre el array de zombies
        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);

            // Actualizar la posición del zombie hacia el jugador
            int playerX = player1.getCoorX();
            int playerY = player1.getCoorY();
            int zombieX = zombie.getX();
            int zombieY = zombie.getY();

            // Calcular la dirección hacia el jugador
            double angle = Math.atan2(playerY - zombieY, playerX - zombieX);
            int deltaX = (int) (Math.cos(angle) * zombie.getSpeed());
            int deltaY = (int) (Math.sin(angle) * zombie.getSpeed());
            int mueveX = (int) (Math.cos(angle) * zombie.getSize()) * -1;
            int mueveY = (int) (Math.sin(angle) * zombie.getSize()) * -1;
            int mX = 0;
            int mY = 0;


            // Verificar colisión con los zombies anteriores
            // Verificar colisión con los zombies anteriores
            for (int j = 0; j < i; j++) {
                Zombie otherZombie = zombies.get(j);
                if (zombie.checkCollision(otherZombie)) {
                    // Calcular la nueva posición del zombie para evitar colisión
                    mX = mueveX;
                    mY = mueveY;
                }
            }

            float siguienteX = zombieX + deltaX + mX;
            float siguienteY = zombieY + deltaY + mY;
            boolean permitirMovimiento = true;

            // Verificar colisión con bloques circulares
            for (Bloque bloque : bloques) {
                float distancia = (float) Math.sqrt(Math.pow(siguienteX - bloque.getX(), 2) + Math.pow(siguienteY - bloque.getY(), 2)) -20;
                if (distancia < bloque.getRadio()) {
                    // El siguiente movimiento del zombie chocaría con un bloque circular
                    permitirMovimiento = false;
                    break; // No es necesario seguir verificando con los demás bloques
                }
            }

            if (permitirMovimiento) {
                // Permitir el movimiento del zombie en esa dirección
                zombie.setX(zombieX + deltaX + mX);
                zombie.setY(zombieY + deltaY + mY);
            }

            // Distancia entre el zombie actual y el jugador
            float distanciaZombieJugador = (float) Math.sqrt(Math.pow(zombie.getX() - player1.getCoorX(), 2) + Math.pow(zombie.getY() - player1.getCoorY(), 2)) -20;
            if (distanciaZombieJugador < 35) {
                // Si la distancia es menor a 35 pixeles, la variable de fin del juego se activa
                setFinDelJuego(true);
                break;
            }


            // Verificar colisiones con las balas
            for (int j = 0; j < balas.size(); j++) {
                Bala bullet = balas.get(j);

                if (zombie.isHitBy(bullet)) {
                    // Eliminar el zombie y la bala del array
                    zombies.remove(i);
                    balas.remove(j);

                    // Decrementar los índices para evitar errores de índice fuera de rango
                    i--;
                    j--;
                    contadorZombiesEliminados += 1;

                    if (contadorZombiesEliminados<=250){
                        Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 2);
                        zombies.add(z);
                    } else if (contadorZombiesEliminados > 250 && contadorZombiesEliminados<500){
                        if (rapido){
                            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 4);
                            zombies.add(z);
                            rapido = false;
                        } else {
                            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 2);
                            zombies.add(z);
                            rapido = true;
                        }
                    } else if (contadorZombiesEliminados >= 500){
                        if (rapido){
                            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 6);
                            zombies.add(z);
                            rapido = false;
                        } else {
                            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 3);
                            zombies.add(z);
                            rapido = true;
                        }
                    }
                    break;
                }
            }

             //Calcular distancia del toque desde el centro del joystick
            int difX = deltaX + mX;
            int difY = deltaY + mY;

            String directionZ = "UP";

            if (Math.abs(difX) > Math.abs(difY)) {
                // Movimiento horizontal
                if (difX > 0) {
                    if (difY > 30) {
                        directionZ = "DOWN_RIGHT";
                    } else if (difY < -30) {
                        directionZ = "UP_RIGHT";
                    } else {
                        directionZ = "RIGHT";
                    }
                } else {
                    if (difY > 30) {
                        directionZ = "DOWN_LEFT";
                    } else if (difY < -30) {
                        directionZ = "UP_LEFT";
                    } else {
                        directionZ = "LEFT";
                    }
                }
            } else {
                // Movimiento vertical
                if (difY > 0) {
                    directionZ = "DOWN";
                } else {
                    directionZ = "UP";
                }
            }
            zombie.setDireccion(directionZ);
        }
    }

    public float calcularDistancia(float x1, float y1, float x2, float y2) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //obtenjer el valor de las coordenadas del toque del usuario
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        int difX = 0;
        int difY = 0;
        int pointerCount = event.getPointerCount();

        if (touchX < anchoPantalla){
            //Manejar eventos de toque de pantalla
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialCoorX = player1.getCoorX();
                    initialCoorY = player1.getCoorY();
                    prevX = touchX;
                    prevY = touchY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    difX = touchX - prevX;
                    difY = touchY - prevY;
                    float anguloMovimiento = (float) Math.atan2(difY, difX); // Cálculo del ángulo en radianes
                    int velocidadJugador = 8;
                    float nuevoCoorX = (float) (player1.getCoorX() + (velocidadJugador*Math.cos(anguloMovimiento)));
                    float nuevoCoorY = (float) (player1.getCoorY() + (velocidadJugador*Math.sin(anguloMovimiento)));
                    // Verificar colisión con bloques circulares
                    boolean colisionBloque = false;
                    for (Bloque bloque : bloques) {
                        float distancia = calcularDistancia(nuevoCoorX, nuevoCoorY, bloque.getX(), bloque.getY());
                        if (distancia <  bloque.getRadio()+25 ) {
                            // Hay colisión entre el jugador y el bloque
                            // Marcar que hubo colisión con un bloque
                            colisionBloque = true;
                            break;
                        }
                    }
                    // Actualizar la posición del jugador solo si no hay colisión con bloques
                    if (!colisionBloque) {
                        player1.setCoorX((int) Math.max(0, Math.min(nuevoCoorX, anchoPantalla - player1.getSize())));
                        player1.setCoorY((int) Math.max(0, Math.min(nuevoCoorY, altoPantalla - player1.getSize())));
                    }
                    // Guardar la coordenada actual para la siguiente actualización de la posición del jugador
                    prevX = touchX;
                    prevY = touchY;
                    break;
                case MotionEvent.ACTION_UP:
                    // Liberación del toque
                    break;
            }
        }
        return true; // Indicar que se ha procesado el evento de toque
    }

    protected void agregarBala (Bala b){

        switch (b.getDireccion()) {
            case "UP":
                b.setX(b.getX()+8);
                break;
            case "DOWN":
                b.setX(b.getX()-35);
                break;
            case "LEFT":
                b.setY(b.getY()-20);
                break;
            case "RIGHT":
                b.setY(b.getY()+15);
                break;
            case "UP_LEFT":
                b.setY(b.getY()-8);
                b.setX(b.getX());
                break;
            case "UP_RIGHT":
                b.setY(b.getY()-8);
                b.setX(b.getX()+15);
                break;
            case "DOWN_LEFT":
                b.setY(b.getY()-8);
                b.setX(b.getX()-32);
                break;
            case "DOWN_RIGHT":
                b.setY(b.getY()+8);
                b.setX(b.getX()-35);
                break;
        }

        b.setX(b.getX()+16);
        this.balas.add(b);
        this.directionJ1 = b.getDireccion();
    }

    protected void eliminarBala (Bala b){
        this.balas.remove(b);
    }

    protected int[] regresarCoordenadasJugador1(){
        int[] coord = new int[2];
        coord[0] = this.player1.getCoorX();
        coord[1] = this.player1.getCoorY();
        return coord;
    }

    protected String regresarDireccionJugador1 (){
        return this.directionJ1;
    }

    protected byte[] getPlayerData() {
        // Obtén los datos del jugador desde tu juego
        int cX = regresarCoordenadasJugador1()[0];
        int cY = regresarCoordenadasJugador1()[1];
        String di = regresarDireccionJugador1();

        // Crea un objeto JSON para almacenar los datos del jugador
        JSONObject playerDataJson = new JSONObject();
        try {
            playerDataJson.put("coordenadaX", cX);
            playerDataJson.put("coordenadaY", cY);
            playerDataJson.put("direccion", di);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Convierte el objeto JSON a un array de bytes
        return playerDataJson.toString().getBytes();
    }
}
