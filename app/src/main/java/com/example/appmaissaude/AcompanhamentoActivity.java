package com.example.appmaissaude;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class AcompanhamentoActivity extends BaseActivity {

    private RegistroAdapter adapter;
    private RecyclerView rv;
    private TextView txtListaVazia;
    private List<Registro> todosRegistros = new ArrayList<>();
    private StatusRegistro filtroAtual = null; // null = todos

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
        txtListaVazia = findViewById(R.id.txtListaVazia);

        // 3. CARREGAR DADOS REAIS (Do SharedPreferences via GerenciadorDados)
        carregarDados();

        // 4. Chips de filtro
        ChipGroup chipGroup = findViewById(R.id.chipGroupFiltro);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTodos)       filtroAtual = null;
            else if (id == R.id.chipPendente)  filtroAtual = StatusRegistro.PENDENTE;
            else if (id == R.id.chipEmAnalise) filtroAtual = StatusRegistro.EM_ANALISE;
            else if (id == R.id.chipResolvido) filtroAtual = StatusRegistro.RESOLVIDO;
            aplicarFiltro();
        });

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
                startActivity(new Intent(AcompanhamentoActivity.this, AlertasActivity.class));
                return true;
            } else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(AcompanhamentoActivity.this, PerfilActivity.class));
                return true;
            }
            
            return false;
        });
    }

    // Método para carregar e atualizar dados
    private void carregarDados() {
        todosRegistros = GerenciadorDados.carregarRegistros(this);
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        List<Registro> filtrados = new ArrayList<>();
        for (Registro r : todosRegistros) {
            if (filtroAtual == null || r.getStatus() == filtroAtual) {
                filtrados.add(r);
            }
        }

        // Mostrar/esconder mensagem de lista vazia
        if (txtListaVazia != null) {
            if (filtrados.isEmpty()) {
                txtListaVazia.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                txtListaVazia.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        }

        if (adapter == null) {
            adapter = new RegistroAdapter(filtrados);
            rv.setAdapter(adapter);
        } else {
            adapter.atualizarDados(filtrados);
        }
    }

    // Atualizar a lista quando o usuário voltar a esta tela
    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
        atualizarBadgeAlertas();
    }
}