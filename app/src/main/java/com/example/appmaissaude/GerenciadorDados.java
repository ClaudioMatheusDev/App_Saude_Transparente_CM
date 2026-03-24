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
        try {
            // Cria diretório para imagens se não existir
            File diretorio = new File(context.getFilesDir(), "imagens_registros");
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            // Nome único para a imagem
            String nomeArquivo = "img_" + System.currentTimeMillis() + ".jpg";
            File arquivoImagem = new File(diretorio, nomeArquivo);

            // Lê a imagem da URI e salva comprimida
            InputStream inputStream = context.getContentResolver().openInputStream(imagemUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Salva comprimido (qualidade 80) para economizar espaço
            FileOutputStream fos = new FileOutputStream(arquivoImagem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            return arquivoImagem.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}