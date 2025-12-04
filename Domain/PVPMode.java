package Domain;

/**
 * Enum que representa los diferentes tipos de modo PVP disponibles
 */
public enum PVPMode {
    ICE_CREAM_VS_MONSTER("Helado vs Monstruo"), // Un helado controla el jugador 1, el monstruo el jugador 2
    ICE_CREAM_COOPERATIVE("Helado Cooperativo"); // Dos helados cooperando contra la IA

    private final String description;

    PVPMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
