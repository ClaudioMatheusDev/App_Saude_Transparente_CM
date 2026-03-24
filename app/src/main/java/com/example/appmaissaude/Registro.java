package com.example.appmaissaude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Registro {
    private String categoria;
    private String local;
    private String descricao;
    private String dataHora;
    private String caminhoImagem; // Caminho da imagem salva

    public Registro(String categoria, String local, String descricao, String caminhoImagem) {
        this.categoria = categoria;
        this.local = local;
        this.descricao = descricao;
        this.caminhoImagem = caminhoImagem;
        // Gera timestamp automaticamente
        this.dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    // Getters (para o Adapter conseguir ler os dados)
    public String getCategoria() { return categoria; }
    public String getLocal() { return local; }
    public String getDescricao() { return descricao; }
    public String getDataHora() { return dataHora; }
    public String getCaminhoImagem() { return caminhoImagem; }
}