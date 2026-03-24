package com.example.appmaissaude;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AcompanhamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acompanhamento);

        // 1. Configurar botão voltar
        ImageView btnVoltar = findViewById(R.id.btnVoltarAcomp);
        btnVoltar.setOnClickListener(v -> finish());

        // 2. Configurar a RecyclerView
        RecyclerView rv = findViewById(R.id.rvRegistros);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 3. CARREGAR DADOS REAIS (Do SharedPreferences via GerenciadorDados)
        List<Registro> registrosSalvos = GerenciadorDados.carregarRegistros(this);

        // 4. Definir o Adapter com os dados reais
        RegistroAdapter adapter = new RegistroAdapter(registrosSalvos);
        rv.setAdapter(adapter);
    }

    // Opcional: Atualizar a lista se o utilizador voltar a esta tela
    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView rv = findViewById(R.id.rvRegistros);
        List<Registro> registrosSalvos = GerenciadorDados.carregarRegistros(this);
        rv.setAdapter(new RegistroAdapter(registrosSalvos));
    }
}