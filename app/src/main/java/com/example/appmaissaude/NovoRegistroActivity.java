package com.example.appmaissaude;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.List;

public class NovoRegistroActivity extends AppCompatActivity {

    private String categoriaSelecionada = "";
    private Uri imagemSelecionadaUri = null;
    private Registro registroEmEdicao = null; // Null = modo criar, preenchido = modo editar
    private boolean modoEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_registro);

        // VERIFICAR SE É MODO DE EDIÇÃO
        if (getIntent().hasExtra("registro_para_editar")) {
            registroEmEdicao = getIntent().getParcelableExtra("registro_para_editar");
            modoEdicao = true;
        }

        // Referências dos componentes
        ImageView btnVoltarNovo = findViewById(R.id.btnVoltarNovo);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText editDescricao = findViewById(R.id.editDescricao);
        Spinner spinnerCentrosSaude = findViewById(R.id.spinnerCentrosSaude);
        ImageView btnAddFoto = findViewById(R.id.btnAddFoto);
        ImageView imgPreview = findViewById(R.id.imgPreview);
        TextView txtTitulo = findViewById(R.id.txtTituloNovoRegistro); // Você pode adicionar isso ao layout

        // Atualizar UI se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            if (txtTitulo != null) {
                txtTitulo.setText("EDITAR REGISTRO");
            }
            btnEnviar.setText("ATUALIZAR");
        }

        // 1. Configurar o Spinner (Lista de Locais)
        String[] locais = {"UBS Central", "Hospital Regional", "Posto de Saúde Vila Nova", "UPA 24h Centro", "Centro de Saúde da Família"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locais);
        spinnerCentrosSaude.setAdapter(adapter);

        // Popular campos se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            editDescricao.setText(registroEmEdicao.getDescricao());
            categoriaSelecionada = registroEmEdicao.getCategoria();
            
            // Selecionar local no spinner
            for (int i = 0; i < locais.length; i++) {
                if (locais[i].equals(registroEmEdicao.getLocal())) {
                    spinnerCentrosSaude.setSelection(i);
                    break;
                }
            }
            
            // Carregar imagem se existir
            if (registroEmEdicao.getCaminhoImagem() != null) {
                File imgFile = new File(registroEmEdicao.getCaminhoImagem());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgPreview.setImageBitmap(bitmap);
                    imgPreview.setPadding(0, 0, 0, 0);
                    imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }

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

        // Destacar categoria se for modo de edição
        if (modoEdicao && registroEmEdicao != null) {
            for (LinearLayout cat : listaCategorias) {
                int id = cat.getId();
                String catNome = "";
                if (id == R.id.catInfra) catNome = "Infraestrutura";
                else if (id == R.id.catMedicamentos) catNome = "Medicamentos";
                else if (id == R.id.catAtendimento) catNome = "Atendimento";
                else if (id == R.id.catAgendamento) catNome = "Agendamento";
                else if (id == R.id.catVacinacao) catNome = "Vacinação";
                else if (id == R.id.catTransporte) catNome = "Transporte";
                else if (id == R.id.catLimpeza) catNome = "Limpeza";
                else if (id == R.id.catFila) catNome = "Gestão de Fila";
                else if (id == R.id.catAcessibilidade) catNome = "Acessibilidade";
                
                if (catNome.equals(registroEmEdicao.getCategoria())) {
                    cat.setBackgroundColor(Color.parseColor("#D0EFEA"));
                }
            }
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
            } else if (modoEdicao && registroEmEdicao != null) {
                // Manter imagem antiga se não selecionou nova
                caminhoImagem = registroEmEdicao.getCaminhoImagem();
            }

            String local = spinnerCentrosSaude.getSelectedItem().toString();

            if (modoEdicao && registroEmEdicao != null) {
                // MODO EDIÇÃO: Atualizar registro existente
                registroEmEdicao.setCategoria(categoriaSelecionada);
                registroEmEdicao.setLocal(local);
                registroEmEdicao.setDescricao(desc);
                registroEmEdicao.setCaminhoImagem(caminhoImagem);
                
                boolean sucesso = GerenciadorDados.atualizarRegistro(this, registroEmEdicao);
                if (sucesso) {
                    Toast.makeText(this, "✅ Registro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "❌ Erro ao atualizar registro!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // MODO CRIAÇÃO: Criar novo registro
                Registro novoRegistro = new Registro(categoriaSelecionada, local, desc, caminhoImagem);

                // Guardar permanentemente
                List<Registro> listaAtual = GerenciadorDados.carregarRegistros(this);
                listaAtual.add(novoRegistro);
                GerenciadorDados.salvarRegistros(this, listaAtual);

                Toast.makeText(this, "✅ Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }

            finish();
        });

        btnVoltarNovo.setOnClickListener(v -> finish());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_novo); // Marca "Novo" como selecionado
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Volta para MainActivity
                Intent intent = new Intent(NovoRegistroActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpa a pilha e volta para MainActivity
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_novo) {
                // Já está em novo registro
                return true;
            } else if (itemId == R.id.nav_historico) {
                Intent intent = new Intent(NovoRegistroActivity.this, AcompanhamentoActivity.class);
                startActivity(intent);
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
}