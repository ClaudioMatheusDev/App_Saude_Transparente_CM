package com.example.appmaissaude;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
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
import java.util.Collections;
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

    // CARREGAR a lista completa (mais recentes primeiro)
    public static List<Registro> carregarRegistros(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REGISTROS, null);

        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Registro>>() {}.getType();
        List<Registro> lista = gson.fromJson(json, type);
        // Ordenar: mais recentes primeiro
        Collections.sort(lista, (a, b) -> {
            String da = a.getDataHora() != null ? a.getDataHora() : "";
            String db = b.getDataHora() != null ? b.getDataHora() : "";
            return db.compareTo(da);
        });
        return lista;
    }

    // TAMANHO MÁXIMO de imagem: 10 MB
    private static final long MAX_IMAGE_BYTES = 10 * 1024 * 1024L;

    /** Valida tamanho/tipo da imagem antes de salvar. Retorna mensagem de erro ou null se OK. */
    public static String validarImagem(Context context, Uri imagemUri) {
        try {
            String mime = context.getContentResolver().getType(imagemUri);
            if (mime == null || (!mime.equals("image/jpeg") && !mime.equals("image/png") && !mime.equals("image/webp"))) {
                return "Formato inválido. Use JPEG, PNG ou WebP.";
            }
            try (InputStream is = context.getContentResolver().openInputStream(imagemUri)) {
                if (is == null) return "Não foi possível ler a imagem.";
                long size = is.available();
                if (size > MAX_IMAGE_BYTES) return "Imagem muito grande (máx 10 MB).";
            }
        } catch (Exception e) {
            Log.e("GerenciadorDados", "Erro ao validar imagem", e);
            return "Erro ao validar imagem.";
        }
        return null;
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
            Log.e("GerenciadorDados", "Erro ao salvar imagem", e);
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
            // SHA-256 is always available on Android 4.0+; this branch is unreachable
            throw new RuntimeException("SHA-256 not available", e);
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