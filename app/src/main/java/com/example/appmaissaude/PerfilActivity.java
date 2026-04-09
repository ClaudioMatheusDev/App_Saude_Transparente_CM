package com.example.appmaissaude;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PerfilActivity extends BaseActivity {

    private TextInputEditText etNome, etCpf, etTelefone, etEmail, etSenha, etConfirmarSenha;
    private TextInputLayout tilNome, tilEmail, tilSenha, tilConfirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Vincular campos
        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);

        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        tilConfirmarSenha = findViewById(R.id.tilConfirmarSenha);

        // Carregar dados já salvos
        carregarPerfil();

        // Botão salvar
        Button btnSalvar = findViewById(R.id.btnSalvarPerfil);
        btnSalvar.setOnClickListener(v -> salvarPerfil());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_perfil);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_perfil) {
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_novo) {
                startActivity(new Intent(PerfilActivity.this, NovoRegistroActivity.class));
                return true;
            } else if (itemId == R.id.nav_historico) {
                startActivity(new Intent(PerfilActivity.this, AcompanhamentoActivity.class));
                return true;
            } else if (itemId == R.id.nav_notificacoes) {
                startActivity(new Intent(PerfilActivity.this, AlertasActivity.class));
                return true;
            }

            return false;
        });
    }

    private void carregarPerfil() {
        GerenciadorDados.PerfilUsuario perfil = GerenciadorDados.carregarPerfil(this);
        etNome.setText(perfil.nome);
        etCpf.setText(perfil.cpf);
        etTelefone.setText(perfil.telefone);
        etEmail.setText(perfil.email);
        // Não preenche a senha por segurança
    }

    private void salvarPerfil() {
        // Limpar erros anteriores
        tilNome.setError(null);
        tilEmail.setError(null);
        tilSenha.setError(null);
        tilConfirmarSenha.setError(null);

        String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
        String cpf = etCpf.getText() != null ? etCpf.getText().toString().trim() : "";
        String telefone = etTelefone.getText() != null ? etTelefone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String senha = etSenha.getText() != null ? etSenha.getText().toString() : "";
        String confirmar = etConfirmarSenha.getText() != null ? etConfirmarSenha.getText().toString() : "";

        // Validações
        if (TextUtils.isEmpty(nome)) {
            tilNome.setError("Informe seu nome");
            etNome.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            etEmail.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(senha)) {
            if (senha.length() < 6) {
                tilSenha.setError("A senha deve ter ao menos 6 caracteres");
                etSenha.requestFocus();
                return;
            }
            if (!senha.equals(confirmar)) {
                tilConfirmarSenha.setError("As senhas não coincidem");
                etConfirmarSenha.requestFocus();
                return;
            }
        }

        // Buscar senha já salva se campo estiver vazio (manter a anterior)
        // GerenciadorDados.salvarPerfil não altera a senha se o campo for vazio
        GerenciadorDados.salvarPerfil(this, nome, cpf, telefone, email, senha);
        Toast.makeText(this, getString(R.string.perfil_salvo), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarBadgeAlertas();
    }
}
