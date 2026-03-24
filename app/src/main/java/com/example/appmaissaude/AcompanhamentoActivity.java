package com.example.appmaissaude;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class AcompanhamentoActivity extends AppCompatActivity {

    private RegistroAdapter adapter;
    private RecyclerView rv;
    private TextView txtListaVazia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acompanhamento);

        // 1. Configurar botão voltar
        ImageView btnVoltar = findViewById(R.id.btnVoltarAcomp);
        btnVoltar.setOnClickListener(v -> finish());

        // 2. Configurar a RecyclerView
        rv = findViewById(R.id.rvRegistros);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Mensagem de lista vazia (se existir no layout)
        txtListaVazia = findViewById(R.id.txtListaVazia); // Você pode adicionar isso ao layout

        // 3. CARREGAR DADOS REAIS (Do SharedPreferences via GerenciadorDados)
        carregarDados();

        // 4. Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_historico); // Marca "Histórico" como selecionado
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                finish(); // Volta para MainActivity
                return true;
            } else if (itemId == R.id.nav_novo) {
                Intent intent = new Intent(AcompanhamentoActivity.this, NovoRegistroActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_historico) {
                // Já está no histórico
                return true;
            } else if (itemId == R.id.nav_notificacoes) {
                Toast.makeText(this, "Alertas em desenvolvimento", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                Toast.makeText(this, "Perfil em desenvolvimento", Toast.LENGTH_SHORT).show();
                return true;
            }
            
            return false;
        });
    }

    // Método para carregar e atualizar dados
    private void carregarDados() {
        List<Registro> registrosSalvos = GerenciadorDados.carregarRegistros(this);

        // Mostrar/esconder mensagem de lista vazia
        if (txtListaVazia != null) {
            if (registrosSalvos.isEmpty()) {
                txtListaVazia.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                txtListaVazia.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        }

        // Criar adapter apenas uma vez ou atualizar dados
        if (adapter == null) {
            adapter = new RegistroAdapter(registrosSalvos);
            rv.setAdapter(adapter);
        } else {
            adapter.atualizarDados(registrosSalvos); // Método que criaremos no adapter
        }
    }

    // Atualizar a lista quando o usuário voltar a esta tela
    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }
}