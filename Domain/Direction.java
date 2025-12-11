package Domain;

/**
 * Enum que representa las direcciones de movimiento en el juego
 */
public enum Direction {
    UP(0, -1),      // Arriba
    DOWN(0, 1),     // Abajo
    LEFT(-1, 0),    // Izquierda
    RIGHT(1, 0);    // Derecha

    private final int deltaX;
    private final int deltaY;

    /**
     * Constructor del enum
     * @param deltaX Cambio en coordenada X
     * @param deltaY Cambio en coordenada Y
     */
    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    /**
     * Obtiene la dirección opuesta
     */
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }
}
