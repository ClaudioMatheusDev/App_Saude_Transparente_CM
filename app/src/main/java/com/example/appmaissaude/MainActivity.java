package com.example.appmaissaude; // Confirme se o seu pacote é este

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNovoRegistro = findViewById(R.id.btnNovoRegistro);
        Button btnVerRegistros = findViewById(R.id.btnVerRegistros);
        TextView txtNumeroRegistros = findViewById(R.id.txtNumeroRegistros);

        // Carregar e exibir número de registros
        carregarNumeroRegistros();

        // NAVEGAÇÃO USANDO INTENTS
        btnNovoRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovoRegistroActivity.class);
            startActivity(intent);
        });

        btnVerRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcompanhamentoActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // Marca "Início" como selecionado
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Já está na home, não faz nada
                return true;
            } else if (itemId == R.id.nav_novo) {
                // Abre tela de novo registro
                Intent intent = new Intent(MainActivity.this, NovoRegistroActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_historico) {
                // Abre tela de histórico
                Intent intent = new Intent(MainActivity.this, AcompanhamentoActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_notificacoes) {
                // Funcionalidade futura
                Toast.makeText(this, "Alertas em desenvolvimento", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                // Funcionalidade futura
                Toast.makeText(this, "Perfil em desenvolvimento", Toast.LENGTH_SHORT).show();
                return true;
            }
            
            return false;
        });
    }

    private void carregarNumeroRegistros() {
        TextView txtNumeroRegistros = findViewById(R.id.txtNumeroRegistros);
        List<Registro> registros = GerenciadorDados.carregarRegistros(this);
        txtNumeroRegistros.setText(String.valueOf(registros.size()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza o contador quando o usuário volta para a tela
        carregarNumeroRegistros();
    }
}