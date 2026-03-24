package com.example.appmaissaude;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDados {
    private static final String PREF_NAME = "MaisSaudePrefs";
    private static final String KEY_REGISTROS = "lista_registros";

    // SALVAR a lista completa
    public static void salvarRegistros(Context context, List<Registro> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = new Gson().toJson(lista); // Transforma a lista em texto
        editor.putString(KEY_REGISTROS, json);
        editor.apply();
    }

    // CARREGAR a lista completa
    public static List<Registro> carregarRegistros(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REGISTROS, null);

        if (json == null) return new ArrayList<>(); // Se estiver vazio, retorna lista nova

        Type type = new TypeToken<List<Registro>>() {}.getType();
        return new Gson().fromJson(json, type); // Transforma o texto de volta em lista
    }
}