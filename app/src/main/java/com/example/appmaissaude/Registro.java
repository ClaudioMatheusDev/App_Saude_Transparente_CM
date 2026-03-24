package com.example.appmaissaude;
public class Registro {
    private String categoria;
    private String local;
    private String descricao;

    public Registro(String categoria, String local, String descricao) {
        this.categoria = categoria;
        this.local = local;
        this.descricao = descricao;
    }

    // Getters (para o Adapter conseguir ler os dados)
    public String getCategoria() { return categoria; }
    public String getLocal() { return local; }
    public String getDescricao() { return descricao; }
}