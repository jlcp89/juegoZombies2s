package net.d3sarrollo.zombie_defense_random_maps.model.multiplayer;

import android.content.Context;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyEndpointDiscoveryCallback extends EndpointDiscoveryCallback {

    private HashMap<String, DiscoveredEndpointInfo> deviceList = new HashMap<>();
    private Context context;
    private String selectedEndpointIdEnviar;
    private List<String> deviceNames;

    public void setContexto (Context c){
        this.context = c;
    }


    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
        // Almacenar el endpointId y la información del dispositivo encontrado
        // Puedes crear una lista o un mapa para almacenar los dispositivos encontrados
        // Por ejemplo:
        deviceList.put(endpointId, discoveredEndpointInfo);

        // Mostrar una lista de dispositivos disponibles al usuario
        // Puedes utilizar un RecyclerView o cualquier otro componente de interfaz de usuario para mostrar la lista
        // Por ejemplo, puedes utilizar un AlertDialog con una lista de opciones
        this.deviceNames = new ArrayList<>();
        for (DiscoveredEndpointInfo info : this.deviceList.values()) {
            this.deviceNames.add(info.getEndpointName());
        }
    }

    @Override
    public void onEndpointLost(String endpointId) {
        // Manejar la pérdida del dispositivo
        deviceList.remove(endpointId);
        deviceNames.remove(endpointId);
    }


    protected List<String>  getListaNombres(){
        return this.deviceNames;
    }

    protected  HashMap<String, DiscoveredEndpointInfo>  getListaDevices(){
        return this.deviceList;
    }

}

