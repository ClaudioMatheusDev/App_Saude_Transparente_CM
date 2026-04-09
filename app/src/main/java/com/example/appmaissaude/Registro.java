package com.example.appmaissaude;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Registro implements Parcelable {
    private String id; // ID único para identificar o registro
    private String categoria;
    private String local;
    private String descricao;
    private String dataHora;
    private String caminhoImagem; // Caminho da imagem salva
    private String status; // Status como String para compatibilidade com Gson
    private double latitude;
    private double longitude;

    public Registro(String categoria, String local, String descricao, String caminhoImagem) {
        this.id = String.valueOf(System.currentTimeMillis()); // ID único baseado em timestamp
        this.categoria = categoria;
        this.local = local;
        this.descricao = descricao;
        this.caminhoImagem = caminhoImagem;
        this.status = StatusRegistro.PENDENTE.name(); // Começa como PENDENTE
        // Gera timestamp automaticamente
        this.dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    // Construtor para Parcelable
    protected Registro(Parcel in) {
        id = in.readString();
        categoria = in.readString();
        local = in.readString();
        descricao = in.readString();
        dataHora = in.readString();
        caminhoImagem = in.readString();
        status = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Registro> CREATOR = new Creator<Registro>() {
        @Override
        public Registro createFromParcel(Parcel in) {
            return new Registro(in);
        }

        @Override
        public Registro[] newArray(int size) {
            return new Registro[size];
        }
    };

    // Getters (para o Adapter conseguir ler os dados)
    public String getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getLocal() { return local; }
    public String getDescricao() { return descricao; }
    public String getDataHora() { return dataHora; }
    public String getCaminhoImagem() { return caminhoImagem; }
    public StatusRegistro getStatus() { 
        return StatusRegistro.fromString(status); 
    }

    // Setters (para edição)
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setLocal(String local) { this.local = local; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }
    public void setStatus(StatusRegistro status) { 
        this.status = status.name(); 
    }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean temLocalizacao() { return latitude != 0.0 || longitude != 0.0; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(categoria);
        dest.writeString(local);
        dest.writeString(descricao);
        dest.writeString(dataHora);
        dest.writeString(caminhoImagem);
        dest.writeString(status);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}