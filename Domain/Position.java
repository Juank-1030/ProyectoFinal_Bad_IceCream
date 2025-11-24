package Domain;

import java.io.Serializable;

/**
 * Representa una posición (x, y) en el tablero del juego
 */
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x;
    private int y;

    /**
     * Constructor de Position
     * @param x Coordenada X (columna)
     * @param y Coordenada Y (fila)
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor de copia
     */
    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Mueve la posición en una dirección
     * @param direction Dirección del movimiento
     * @return Nueva posición después del movimiento
     */
    public Position move(Direction direction) {
        return new Position(
            this.x + direction.getDeltaX(),
            this.y + direction.getDeltaY()
        );
    }

    /**
     * Calcula la distancia Manhattan entre dos posiciones
     */
    public int distanceTo(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Verifica si dos posiciones son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
