package com.example.appmaissaude;

public enum StatusRegistro {
    PENDENTE("Pendente", "#FF9800"),      // Laranja
    EM_ANALISE("Em Análise", "#2196F3"),  // Azul
    RESOLVIDO("Resolvido", "#4CAF50");    // Verde

    private final String texto;
    private final String cor;

    StatusRegistro(String texto, String cor) {
        this.texto = texto;
        this.cor = cor;
    }

    public String getTexto() {
        return texto;
    }

    public String getCor() {
        return cor;
    }

    // Método para avançar para próximo status
    public StatusRegistro proximo() {
        switch (this) {
            case PENDENTE:
                return EM_ANALISE;
            case EM_ANALISE:
                return RESOLVIDO;
            case RESOLVIDO:
                return PENDENTE; // Volta ao início se quiser
            default:
                return PENDENTE;
        }
    }

    // Converter de String para Enum (para compatibilidade com dados salvos)
    public static StatusRegistro fromString(String status) {
        if (status == null) return PENDENTE;
        try {
            return StatusRegistro.valueOf(status);
        } catch (IllegalArgumentException e) {
            return PENDENTE;
        }
    }
}
