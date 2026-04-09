package com.example.appmaissaude;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDados {
    private static final String PREF_NAME = "MaisSaudePrefs";
    private static final String KEY_REGISTROS = "lista_registros";
    private static final Gson gson = new Gson(); // Singleton do Gson

    // SALVAR a lista completa
    public static void salvarRegistros(Context context, List<Registro> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = gson.toJson(lista); // Usa singleton
        editor.putString(KEY_REGISTROS, json);
        editor.apply();
    }

    // CARREGAR a lista completa
    public static List<Registro> carregarRegistros(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REGISTROS, null);

        if (json == null) return new ArrayList<>(); // Se estiver vazio, retorna lista nova

        Type type = new TypeToken<List<Registro>>() {}.getType();
        return gson.fromJson(json, type); // Usa singleton
    }

    // CONTAR registros por status
    public static int contarPorStatus(Context context, StatusRegistro status) {
        int count = 0;
        for (Registro r : carregarRegistros(context)) {
            if (r.getStatus() == status) count++;
        }
        return count;
    }

    // ATUALIZAR um registro específico
    public static boolean atualizarRegistro(Context context, Registro registroAtualizado) {
        List<Registro> lista = carregarRegistros(context);
        
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(registroAtualizado.getId())) {
                lista.set(i, registroAtualizado);
                salvarRegistros(context, lista);
                return true;
            }
        }
        return false; // Não encontrou o registro
    }

    // SALVAR IMAGEM no armazenamento interno e retornar o caminho
    public static String salvarImagem(Context context, Uri imagemUri) {
        File diretorio = new File(context.getFilesDir(), "imagens_registros");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        String nomeArquivo = "img_" + System.currentTimeMillis() + ".jpg";
        File arquivoImagem = new File(diretorio, nomeArquivo);

        try (InputStream inputStream = context.getContentResolver().openInputStream(imagemUri)) {
            if (inputStream == null) return null;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return null;
            try (FileOutputStream fos = new FileOutputStream(arquivoImagem)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            }
            return arquivoImagem.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ── Perfil do Usuário ──────────────────────────────────────────────────────

    private static final String KEY_PERFIL_NOME      = "perfil_nome";
    private static final String KEY_PERFIL_CPF       = "perfil_cpf";
    private static final String KEY_PERFIL_TELEFONE  = "perfil_telefone";
    private static final String KEY_PERFIL_EMAIL     = "perfil_email";
    private static final String KEY_PERFIL_SENHA     = "perfil_senha";

    public static class PerfilUsuario {
        public String nome;
        public String cpf;
        public String telefone;
        public String email;
        public String senha;

        public PerfilUsuario(String nome, String cpf, String telefone, String email, String senha) {
            this.nome = nome != null ? nome : "";
            this.cpf = cpf != null ? cpf : "";
            this.telefone = telefone != null ? telefone : "";
            this.email = email != null ? email : "";
            this.senha = senha != null ? senha : "";
        }
    }

    public static void salvarPerfil(Context context, String nome, String cpf,
                                     String telefone, String email, String senha) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_PERFIL_NOME, nome)
                .putString(KEY_PERFIL_CPF, cpf)
                .putString(KEY_PERFIL_TELEFONE, telefone)
                .putString(KEY_PERFIL_EMAIL, email);
        if (senha != null && !senha.isEmpty()) {
            editor.putString(KEY_PERFIL_SENHA, hashSenha(senha));
        }
        editor.apply();
    }

    static String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update("SaudeTransparente_AppSalt".getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 always available on Android
            return senha;
        }
    }

    public static PerfilUsuario carregarPerfil(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new PerfilUsuario(
                prefs.getString(KEY_PERFIL_NOME, ""),
                prefs.getString(KEY_PERFIL_CPF, ""),
                prefs.getString(KEY_PERFIL_TELEFONE, ""),
                prefs.getString(KEY_PERFIL_EMAIL, ""),
                prefs.getString(KEY_PERFIL_SENHA, "")
        );
    }
}