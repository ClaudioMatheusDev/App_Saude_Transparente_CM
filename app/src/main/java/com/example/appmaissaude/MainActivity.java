package com.example.appmaissaude; // Confirme se o seu pacote é este

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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
        carregarNomePerfil();

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
                startActivity(new Intent(MainActivity.this, AlertasActivity.class));
                return true;
            } else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return true;
            }
            
            return false;
        });
    }

    private void carregarNumeroRegistros() {
        TextView txtNumeroRegistros = findViewById(R.id.txtNumeroRegistros);
        TextView txtPendentes = findViewById(R.id.txtRegistrosPendentes);
        List<Registro> registros = GerenciadorDados.carregarRegistros(this);
        txtNumeroRegistros.setText(String.valueOf(registros.size()));
        int pendentes = 0;
        for (Registro r : registros) {
            if (r.getStatus() == StatusRegistro.PENDENTE) pendentes++;
        }
        if (pendentes > 0) {
            txtPendentes.setText(getString(R.string.pendentes_label, pendentes));
        } else {
            txtPendentes.setText(registros.isEmpty() ? "" : getString(R.string.nenhum_pendente));
        }
    }

    private void carregarNomePerfil() {
        TextView txtNome = findViewById(R.id.txtNomeUsuario);
        TextView txtId = findViewById(R.id.txtIdUsuario);
        GerenciadorDados.PerfilUsuario perfil = GerenciadorDados.carregarPerfil(this);
        if (!perfil.nome.isEmpty()) {
            txtNome.setText(perfil.nome);
            String subtitulo = !perfil.email.isEmpty() ? perfil.email : (!perfil.telefone.isEmpty() ? perfil.telefone : "");
            txtId.setText(subtitulo.isEmpty() ? getString(R.string.placeholder_id_usuario) : subtitulo);
        } else {
            txtNome.setText(getString(R.string.placeholder_nome_usuario));
            txtId.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza o contador e o nome quando o usuário volta para a tela
        carregarNumeroRegistros();
        carregarNomePerfil();
    }
}