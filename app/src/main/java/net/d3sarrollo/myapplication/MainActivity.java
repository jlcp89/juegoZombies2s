package net.d3sarrollo.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;

import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;




public class MainActivity extends AppCompatActivity {

    private Paint paint;
    private int playerX, playerY, playerSize;
    private GameView gameView;
    public static double screenWidth;
    private FloatingActionButton botonBuscar, botonDispararArriba, botonDispararAbajo, botonDispararIzquierda, botonDispararDerecha, botonDispararArribaI, botonDispararArribaD, botonDispararAbajoI, botonDispararAbajoD;
    private static final String TAG = "MainActivity";
    private ConnectionsClient connectionsClient;
    private MyEndpointDiscoveryCallback endpointDiscoveryCallback;
    private MyConnectionLifecycleCallback connectionLifecycleCallback;
    private static final int PERMISSION_REQUEST_CODE = 123;
    String selectedEndpointId;
    private boolean servidor = false;
    private boolean finJuego = false;
    private Timer gameTimer;
    private boolean isToastShown = false;
    private TextView textoNivel, textoPuntos;

    private InterstitialAd mInterstitialAd;

    public int getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(int nivelActual) {
        this.nivelActual = nivelActual;
    }

    private int nivelActual = 1;






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Verificar si se concedieron los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos fueron concedidos, puedes continuar con la lógica de la aplicación
            } else {
                // Los permisos fueron denegados, muestra un mensaje o realiza alguna acción correspondiente
            }
        }
    }

    private void requestPermissionsIfNeeded() {
        String[] permissions = new String[0];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.NEARBY_WIFI_DEVICES,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN
            };
        }

        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionsIfNeeded();

        setContentView(R.layout.activity_main);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });




        // Crear una instancia del lienzo personalizado
        gameView = new GameView(this);

        // Crear una instancia de tu ConnectionLifecycleCallback
        connectionLifecycleCallback = new MyConnectionLifecycleCallback();

        // Crear una instancia de tu MyEndpointDiscoveryCallback
        endpointDiscoveryCallback = new MyEndpointDiscoveryCallback();

        // Obtener una instancia de ConnectionsClient
        connectionsClient = Nearby.getConnectionsClient(this);

        endpointDiscoveryCallback.setContexto(getApplicationContext());

        // Agregar el lienzo personalizado al layout de la actividad principal
        LinearLayout layout = findViewById(R.id.game_layout);
        botonDispararArriba = findViewById(R.id.btn_up);
        botonDispararAbajo = findViewById(R.id.btn_down);
        botonDispararDerecha = findViewById(R.id.btn_right);
        botonDispararIzquierda = findViewById(R.id.btn_left);
        botonDispararArribaI = findViewById(R.id.btn_ari);
        botonDispararArribaD = findViewById(R.id.btn_ard);
        botonDispararAbajoI = findViewById(R.id.btn_abi);
        botonDispararAbajoD = findViewById(R.id.btn_abd);
        textoNivel = findViewById(R.id.textNivel);
        textoPuntos = findViewById(R.id.textPuntos);

        //botonBuscar = findViewById(R.id.botonBuscar);

        layout.addView(gameView);

        /*botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> deviceNames = endpointDiscoveryCallback.getListaNombres();
                HashMap<String, DiscoveredEndpointInfo> deviceList = endpointDiscoveryCallback.getListaDevices();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                if (deviceNames == null || deviceNames.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No hay compañeros de batalla disponibles", Toast.LENGTH_LONG).show();
                } else {
                    builder.setTitle("Dispositivos disponibles")
                            .setItems(deviceNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Obtener el endpointId del dispositivo seleccionado
                                    String selectedEndpointId = (String) deviceList.keySet().toArray()[which];
                                    // Iniciar la conexión con el dispositivo seleccionado
                                    connectionsClient.requestConnection(
                                            getPackageName(),
                                            selectedEndpointId,
                                            connectionLifecycleCallback);
                                }
                            }).show();
                }

                if (connectionLifecycleCallback.isConexionEstablecida() && (connectionLifecycleCallback.getEndpointConectado()!= null)){
                    byte[] playerData = gameView.getPlayerData();
                    sendGameData(connectionLifecycleCallback.getEndpointConectado(), playerData, gameView.getBalasByte(), gameView.getZobiesByte());
                }

            }
        });*/

        botonDispararArriba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "UP"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararAbajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "DOWN"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararDerecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "RIGHT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararIzquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "LEFT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararArribaI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "UP_LEFT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararArribaD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "UP_RIGHT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararAbajoI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "DOWN_LEFT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });

        botonDispararAbajoD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bala nuevaBala = new Bala(gameView.regresarCoordenadasJugador1()[0],gameView.regresarCoordenadasJugador1()[1], "DOWN_RIGHT"); // Coordenadas de la bala
                gameView.agregarBala(nuevaBala);
            }
        });


        // Actualizar el lienzo personalizado cada 16ms (60fps)
        gameTimer = new Timer();

        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (gameView.isFinDelJuego() && !isToastShown) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Game Over", Toast.LENGTH_LONG).show();
                        isToastShown = true;
                        gameTimer.cancel();
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(MainActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                    }

                    String textoN = "Level: " + gameView.getNivelAcutal();
                    textoNivel.setText(textoN);
                    String textoP = "Score: " + gameView.getContadorZombiesEliminados();
                    textoPuntos.setText(textoP);
                    gameView.invalidate();

                });
            }
        }, 0, 16);



    }

    private void sendGameData(String endpointId, byte[] playerData, byte[] balasArray, byte[] zombiesArray) {
        // Combina los datos del jugador, balas y zombies en un solo array
        byte[] mergedData;
        if (servidor){
            mergedData = mergeArrays(playerData, balasArray, zombiesArray);
        } else {
            mergedData = mergeArrays(playerData, balasArray);
        }

        // Crea un Payload con los datos del juego
        Payload payload = Payload.fromBytes(mergedData);

        // Envia el Payload al dispositivo remoto
        connectionsClient.sendPayload(endpointId, payload);
    }

    private byte[] mergeArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] mergedArray = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, mergedArray, currentIndex, array.length);
            currentIndex += array.length;
        }

        return mergedArray;
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Detener la detección de dispositivos cercanos
        connectionsClient.stopDiscovery();
        if (servidor){
            connectionsClient.stopAdvertising();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Elige una opción")
                .setMessage("¿Deseas ser el servidor o el cliente?")
                .setPositiveButton("Servidor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // El usuario eligió ser el servidor
                        // Iniciar la publicación de anuncio y establecer el ConnectionLifecycleCallback
                        servidor = true;
                        connectionsClient.startAdvertising(
                                        deviceName, getPackageName(), connectionLifecycleCallback,
                                        new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // La publicación de anuncio se inició con éxito
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al iniciar la publicación de anuncio
                                    }
                                });                    }
                })
                .setNegativeButton("Cliente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // El usuario eligió ser el cliente
                        connectionsClient.startDiscovery(
                                        getPackageName(), endpointDiscoveryCallback,
                                        new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // La detección de dispositivos cercanos se inició con éxito
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al iniciar la detección de dispositivos cercanos
                                    }
                                });
                    }
                })
                .setCancelable(false)
                .show();*/
    }



}