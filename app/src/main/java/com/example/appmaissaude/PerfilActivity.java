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
    private TextInputLayout tilNome, tilCpf, tilEmail, tilSenha, tilConfirmarSenha;

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
        tilCpf = findViewById(R.id.tilCpf);
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

        // Validação de CPF
        if (!TextUtils.isEmpty(cpf) && !cpfValido(cpf)) {
            tilNome.setError(null);
            etCpf.setError("CPF inválido");
            etCpf.requestFocus();
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

    /** Valida CPF usando dígitos verificadores. */
    private boolean cpfValido(String cpf) {
        String c = cpf.replaceAll("\\D", "");
        if (c.length() != 11) return false;
        if (c.matches("(\\d)\\1{10}")) return false; // Todos iguais (00000000000)
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (c.charAt(i) - '0') * (10 - i);
        int r1 = (sum * 10) % 11;
        if (r1 == 10 || r1 == 11) r1 = 0;
        if (r1 != (c.charAt(9) - '0')) return false;
        sum = 0;
        for (int i = 0; i < 10; i++) sum += (c.charAt(i) - '0') * (11 - i);
        int r2 = (sum * 10) % 11;
        if (r2 == 10 || r2 == 11) r2 = 0;
        return r2 == (c.charAt(10) - '0');
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarBadgeAlertas();
    }
}
