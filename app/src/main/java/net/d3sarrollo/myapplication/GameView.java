package net.d3sarrollo.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;


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


    int prevX, prevY;
    int joystickX = 0;
    int joystickY = 0;

    float joystickAngle = 0;

    int sizeJoystick = 150;

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





    public GameView(Context context) {
        super(context);
        paint = new Paint();


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

        anchoPantalla = screenWidth;
        altoPantalla= screenHeight;
        joystickX = 200;
        joystickY = altoPantalla - 200;

        for (int i = 0; i < 200; i++) {
            Zombie z = new Zombie(anchoPantalla,altoPantalla, 25, 2);
            zombies.add(z);
        }

        for (int i = 0; i < 5; i++) {
            Bloque b = new Bloque(anchoPantalla,altoPantalla, 75);
            bloques.add(b);
        }
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


    private void draw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            if (canvas != null) {

                synchronized (getHolder()) {
                    // Limpiar el fondo del lienzo
                    paint.setColor(Color.WHITE);
                    canvas.drawPaint(paint);

                    //Dibujar control
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
                    }


                    //Dibujar jugador
                    paint.setColor(Color.BLUE);
                    int x1 = player1.getCoorX() - player1.getSize()/2;
                    int y1 = player1.getCoorY() - player1.getSize()/2;
                    int x2 = player1.getCoorX() + player1.getSize()/2;
                    int y2 = player1.getCoorY() + player1.getSize()/2;

                    canvas.drawRect(x1,y1,x2,y2, paint);



                    //Dibujar bloques
                    if (bloques.size()>0){
                        paint.setColor(Color.DKGRAY);
                        for (int i = 0; i < bloques.size(); i++) {
                            Bloque b = bloques.get(i);
                            // Realiza las operaciones necesarias con la bala en la posición i
                            canvas.drawCircle(b.getX(),b.getY(),b.getRadio(), paint);
                        }
                    }

                    //Dibujar zombies
                    if (zombies.size()>0){
                        paint.setColor(Color.RED);
                        for (int i = 0; i < zombies.size(); i++) {
                            Zombie z = zombies.get(i);
                            // Realiza las operaciones necesarias con la bala en la posición i
                            canvas.drawCircle(z.getX(),z.getY(),z.getSize(), paint);
                        }
                    }


                    //Dibujar balas
                    if (balas.size()>0){
                        paint.setColor(Color.RED);
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

                    // Salir del bucle interno para evitar verificar más colisiones con la misma bala
                    break;
                }
            }
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


        if (touchX < anchoPantalla/2){
            //Manejar eventos de toque de pantalla
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    prevX = touchX;
                    prevY = touchY;
                    //Calcular distancia del toque desde el centro del joystick

                    difX = prevX - joystickX;
                    difY = prevY - joystickY;
                    float displacement = (float) Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
                    joystickAngle = (float) Math.atan2(difY, difX); // Cálculo del ángulo en radianes


                    if (displacement < sizeJoystick){
                        // Cambiar dirección sin moverse
                        if (Math.abs(difX) > Math.abs(difY)) {
                            // Movimiento horizontal
                            if (difX > 0) {
                                if (difY > 30) {
                                    directionJ1 = "DOWN_RIGHT";
                                } else if (difY < -30) {
                                    directionJ1 = "UP_RIGHT";
                                } else {
                                    directionJ1 = "RIGHT";
                                }
                            } else {
                                if (difY > 30) {
                                    directionJ1 = "DOWN_LEFT";
                                } else if (difY < -30) {
                                    directionJ1 = "UP_LEFT";
                                } else {
                                    directionJ1 = "LEFT";
                                }
                            }
                        } else {
                            // Movimiento vertical
                            if (difY > 0) {
                                directionJ1 = "DOWN";
                            } else {
                                directionJ1 = "UP";
                            }
                        }
                    }
                    break;

                // Acciones segun accion de toque de pantalla

                case MotionEvent.ACTION_POINTER_DOWN:
                    if (pointerCount == 2) {
                        Bala nuevaBala = new Bala(this.regresarCoordenadasJugador1()[0],this.regresarCoordenadasJugador1()[1], this.regresarDireccionJugador1()); // Coordenadas de la bala
                        this.agregarBala(nuevaBala);
                    }
                    break;


                case MotionEvent.ACTION_MOVE:
                    difX = touchX - prevX;
                    difY = touchY - prevY;

                    float nuevoCoorX = player1.getCoorX() + difX;
                    float nuevoCoorY = player1.getCoorY() + difY;

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
        this.balas.add(b);
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
