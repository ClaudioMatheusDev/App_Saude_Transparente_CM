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
import java.util.ArrayList;
import java.util.List;

public class AlertasActivity extends AppCompatActivity {

    private RegistroAdapter adapter;
    private RecyclerView rv;
    private TextView txtListaVazia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);

        ImageView btnVoltar = findViewById(R.id.btnVoltarAlertas);
        btnVoltar.setOnClickListener(v -> finish());

        rv = findViewById(R.id.rvAlertas);
        rv.setLayoutManager(new LinearLayoutManager(this));

        txtListaVazia = findViewById(R.id.txtAlertasVazia);

        carregarDados();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_notificacoes);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_notificacoes) {
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(AlertasActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_novo) {
                startActivity(new Intent(AlertasActivity.this, NovoRegistroActivity.class));
                return true;
            } else if (itemId == R.id.nav_historico) {
                startActivity(new Intent(AlertasActivity.this, AcompanhamentoActivity.class));
                return true;
            } else if (itemId == R.id.nav_perfil) {
                startActivity(new Intent(AlertasActivity.this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    private void carregarDados() {
        List<Registro> todos = GerenciadorDados.carregarRegistros(this);
        List<Registro> pendentes = new ArrayList<>();
        for (Registro r : todos) {
            if (r.getStatus() == StatusRegistro.PENDENTE) {
                pendentes.add(r);
            }
        }

        if (pendentes.isEmpty()) {
            txtListaVazia.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            txtListaVazia.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new RegistroAdapter(pendentes);
            rv.setAdapter(adapter);
        } else {
            adapter.atualizarDados(pendentes);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }
}
