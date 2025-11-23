package Domain;

/**
 * Enum que representa los diferentes modos de juego
 */
public enum GameMode {
    PVP("Player vs Player"),      // Jugador controla helado, otro jugador controla enemigo
    PVM("Player vs Machine"),     // Jugador controla helado, IA controla enemigo
    MVM("Machine vs Machine");    // IA controla tanto helado como enemigo

    private final String description;

    GameMode(String description) {
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
