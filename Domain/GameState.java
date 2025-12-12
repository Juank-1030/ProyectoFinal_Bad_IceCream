package Domain;

/**
 * Enum que representa los estados posibles del juego
 */
public enum GameState {
    MENU("En Menú"),
    PLAYING("Jugando"),
    PAUSED("Pausado"),
    WON("Victoria"),
    LOST("Derrota");

    private final String description;

    GameState(String description) {
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
