package com.joaofontes.mosaic.model;


public enum DirecaoMesclagem {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical");

    private final String displayName;

    DirecaoMesclagem(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static DirecaoMesclagem fromString(String text) {
        if (text != null) {
            for (DirecaoMesclagem b : DirecaoMesclagem.values()) {
                if (text.equalsIgnoreCase(b.name()) || text.equalsIgnoreCase(b.displayName)) {
                    return b;
                }
            }
        }
        return null; // Ou lançar exceção, ou retornar um padrão
    }
}
