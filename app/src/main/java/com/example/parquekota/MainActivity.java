package com.example.parquekota;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button registrarDueno = findViewById(R.id.btnRegistroDueno);
        Button registrarMascota = findViewById(R.id.btnRegistroMascota);
        registrarDueno.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroDuenoActivity.class)));
        registrarMascota.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroMascotaActivity.class)));
    }
}
