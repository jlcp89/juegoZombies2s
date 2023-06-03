package net.d3sarrollo.myapplication;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;

public class MyConnectionLifecycleCallback extends ConnectionLifecycleCallback {
    private boolean conexionEstablecida = false;
    private String endpointConectado = null;


    @Override
    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
        // Procesar la solicitud de conexión entrante
        // Puede aceptar o rechazar la conexión
    }

    @Override
    public void onConnectionResult(String endpointId, ConnectionResolution result) {

        if (result.getStatus().isSuccess()) {
            // La conexión se estableció con éxito, puedes realizar las acciones necesarias
            // como enviar los datos del juego a través de la conexión establecida
            setConexionEstablecida(true);
            setEndpointConectado(endpointId);

        } else {
            // No se pudo establecer la conexión, maneja el error
        }

    }

    @Override
    public void onDisconnected(String endpointId) {
        // Manejar la desconexión del dispositivo remoto
    }



    public boolean isConexionEstablecida() {
        return conexionEstablecida;
    }

    public void setConexionEstablecida(boolean conexionEstablecida) {
        this.conexionEstablecida = conexionEstablecida;
    }

    public String getEndpointConectado() {
        return endpointConectado;
    }

    public void setEndpointConectado(String endpointConectado) {
        this.endpointConectado = endpointConectado;
    }
}







