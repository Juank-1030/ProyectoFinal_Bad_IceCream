package Domain;

import java.util.List;

/**
 * Comportamiento para frutas que se teletransportan aleatoriamente
 * Usado por Cereza
 */
public class TeleportFruitBehavior implements FruitBehavior {
    private static final long serialVersionUID = 1L;

    private transient Board board; // Referencia al tablero
    private int teleportCounter;
    private int teleportInterval; // Cada cuántos updates se teletransporta

    /**
     * Constructor con valor por defecto (ÚNICO USADO)
     */
    public TeleportFruitBehavior(Board board) {
        this.board = board;
        this.teleportInterval = 30; // Se teletransporta cada 30 updates
        this.teleportCounter = 0;
    }

    /**
     * Establece la referencia al tablero (necesario después de deserialización)
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public Position updatePosition(Position currentPosition) {
        teleportCounter++;

        if (teleportCounter < teleportInterval) {
            return null; // No se teletransporta aún
        }

        teleportCounter = 0;

        // Obtener una posición aleatoria válida del tablero
        if (board != null) {
            List<Position> validPositions = board.getEmptyPositions();
            if (!validPositions.isEmpty()) {
                int randomIndex = (int) (Math.random() * validPositions.size());
                return validPositions.get(randomIndex);
            }
        }

        return null; // No hay posiciones disponibles
    }

    @Override
    public void reset() {
        teleportCounter = 0;
    }

    public int getTeleportInterval() {
        return teleportInterval;
    }

    public void setTeleportInterval(int teleportInterval) {
        this.teleportInterval = teleportInterval;
    }
}
