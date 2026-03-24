package com.example.appmaissaude;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class NovoRegistroActivity extends AppCompatActivity {

    private String categoriaSelecionada = "";
    private Uri imagemSelecionadaUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_registro);

        // Referências dos componentes
        ImageView btnVoltarNovo = findViewById(R.id.btnVoltarNovo);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText editDescricao = findViewById(R.id.editDescricao);
        Spinner spinnerCentrosSaude = findViewById(R.id.spinnerCentrosSaude);
        ImageView btnAddFoto = findViewById(R.id.btnAddFoto);
        ImageView imgPreview = findViewById(R.id.imgPreview);

        // 1. Configurar o Spinner (Lista de Locais)
        String[] locais = {"UBS Central", "Hospital Regional", "Posto de Saúde Vila Nova", "UPA 24h Centro", "Centro de Saúde da Família"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locais);
        spinnerCentrosSaude.setAdapter(adapter);

        // 2. Lógica da Galeria de Fotos
        ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagemSelecionadaUri = result.getData().getData();
                        imgPreview.setImageURI(imagemSelecionadaUri);
                        imgPreview.setPadding(0, 0, 0, 0);
                        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
        );

        btnAddFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeriaLauncher.launch(intent);
        });

        // 3. Lógica das Categorias (Cores e Seleção)
        LinearLayout[] listaCategorias = {
                findViewById(R.id.catInfra), findViewById(R.id.catMedicamentos),
                findViewById(R.id.catAtendimento), findViewById(R.id.catAgendamento),
                findViewById(R.id.catVacinacao), findViewById(R.id.catTransporte),
                findViewById(R.id.catLimpeza), findViewById(R.id.catFila),
                findViewById(R.id.catAcessibilidade)
        };

        View.OnClickListener clickCategoria = v -> {
            for (LinearLayout cat : listaCategorias) {
                cat.setBackgroundColor(Color.TRANSPARENT);
            }
            v.setBackgroundColor(Color.parseColor("#D0EFEA"));
            int id = v.getId();
            if (id == R.id.catInfra) categoriaSelecionada = "Infraestrutura";
            else if (id == R.id.catMedicamentos) categoriaSelecionada = "Medicamentos";
            else if (id == R.id.catAtendimento) categoriaSelecionada = "Atendimento";
            else if (id == R.id.catAgendamento) categoriaSelecionada = "Agendamento";
            else if (id == R.id.catVacinacao) categoriaSelecionada = "Vacinação";
            else if (id == R.id.catTransporte) categoriaSelecionada = "Transporte";
            else if (id == R.id.catLimpeza) categoriaSelecionada = "Limpeza";
            else if (id == R.id.catFila) categoriaSelecionada = "Gestão de Fila";
            else if (id == R.id.catAcessibilidade) categoriaSelecionada = "Acessibilidade";
        };

        for (LinearLayout cat : listaCategorias) {
            cat.setOnClickListener(clickCategoria);
        }

        // 4. BOTÃO ENVIAR (Persistência Real com Validação)
        btnEnviar.setOnClickListener(v -> {
            // VALIDAÇÃO COMPLETA
            if (categoriaSelecionada.isEmpty()) {
                Toast.makeText(this, "❌ Por favor, selecione uma categoria!", Toast.LENGTH_SHORT).show();
                return;
            }

            String desc = editDescricao.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(this, "❌ Por favor, descreva o problema!", Toast.LENGTH_SHORT).show();
                editDescricao.requestFocus();
                return;
            }

            if (desc.length() < 10) {
                Toast.makeText(this, "❌ Descrição muito curta. Mínimo 10 caracteres.", Toast.LENGTH_SHORT).show();
                editDescricao.requestFocus();
                return;
            }

            // Salvar imagem se foi selecionada
            String caminhoImagem = null;
            if (imagemSelecionadaUri != null) {
                caminhoImagem = GerenciadorDados.salvarImagem(this, imagemSelecionadaUri);
                if (caminhoImagem == null) {
                    Toast.makeText(this, "⚠️ Erro ao salvar imagem. Continuando sem foto...", Toast.LENGTH_SHORT).show();
                }
            }

            // Criar o objeto com os dados da tela
            String local = spinnerCentrosSaude.getSelectedItem().toString();
            Registro novoRegistro = new Registro(categoriaSelecionada, local, desc, caminhoImagem);

            // Guardar permanentemente
            List<Registro> listaAtual = GerenciadorDados.carregarRegistros(this);
            listaAtual.add(novoRegistro);
            GerenciadorDados.salvarRegistros(this, listaAtual);

            Toast.makeText(this, "✅ Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnVoltarNovo.setOnClickListener(v -> finish());
    }
}