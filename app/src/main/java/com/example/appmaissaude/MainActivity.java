package com.example.appmaissaude; // Confirme se o seu pacote é este

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNovoRegistro = findViewById(R.id.btnNovoRegistro);
        Button btnVerRegistros = findViewById(R.id.btnVerRegistros);

        // NAVEGAÇÃO USANDO INTENTS
        btnNovoRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovoRegistroActivity.class);
            startActivity(intent);
        });

        btnVerRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcompanhamentoActivity.class);
            startActivity(intent);
        });
    }
}