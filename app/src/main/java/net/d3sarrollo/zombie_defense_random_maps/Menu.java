package net.d3sarrollo.zombie_defense_random_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.d3sarrollo.myapplication.R;

public class Menu extends AppCompatActivity {

    private Button botonJugar;
    private Button botonSalir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        botonJugar = findViewById(R.id.button_jugar);
        botonSalir = findViewById(R.id.button_salir);

        botonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
    }
}