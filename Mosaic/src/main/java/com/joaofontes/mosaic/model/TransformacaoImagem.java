package com.joaofontes.mosaic.model;

/**
 * Enum para representar as transformações aplicáveis a uma imagem.
 */
public enum TransformacaoImagem {
    NENHUMA("Nenhuma"),
    ESPELHAR_HORIZONTAL("Espelhar Horizontalmente"),
    ESPELHAR_VERTICAL("Espelhar Verticalmente"),
    ROTACIONAR_90("Rotacionar 90°"),    // Sentido horário
    ROTACIONAR_180("Rotacionar 180°"),
    ROTACIONAR_270("Rotacionar 270°");  // Sentido horário (equivalente a -90°)

    private final String displayName;

    TransformacaoImagem(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
    public String toPersistentString() {
        return this.name(); // Salva o nome da constante enum (e.g., "ESPELHAR_HORIZONTAL")
    }

    public static TransformacaoImagem fromString(String text) {
        if (text != null) {
            for (TransformacaoImagem t : TransformacaoImagem.values()) {
                // Tenta corresponder pelo nome da constante enum (mais robusto)
                if (text.equalsIgnoreCase(t.name())) {
                    return t;
                }
                // Fallback para displayName se necessário, mas prefira o nome da constante para persistência
                if (text.equalsIgnoreCase(t.displayName)) {
                    return t;
                }
            }
        }
        return NENHUMA; // Retorna um padrão seguro se não encontrar
    }
}
