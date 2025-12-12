package Domain;

import java.io.Serializable;

/**
 * IA para controlar enemigos en modos PVM y MVM
 * Utiliza el comportamiento de movimiento ya definido en cada enemigo
 */
public class EnemyAI implements AI, Serializable {

    private Enemy enemy;
    private Board board;
    private int updateCounter;

    /**
     * Constructor de EnemyAI
     * 
     * @param enemy El enemigo a controlar
     * @param board El tablero del juego
     */
    public EnemyAI(Enemy enemy, Board board) {
        this.enemy = enemy;
        this.board = board;
        this.updateCounter = 0;
    }

    @Override
    public Direction getNextMove() {
        if (enemy == null || !enemy.isAlive()) {
            return null;
        }

        // Delegar al comportamiento de movimiento del enemigo
        // Cada enemigo ya tiene su estrategia (Pattern, Chase, etc.)
        Direction nextDirection = enemy.getNextMove();

        // Verificar si el movimiento es válido
        if (nextDirection != null) {
            var nextPos = enemy.getPosition().move(nextDirection);

            // Si hay un bloque y el enemigo puede romperlo, intentar romperlo
            if (board.hasIceBlock(nextPos) && enemy.canBreakIce()) {
                return nextDirection; // Moverá y romperá el bloque
            }

            // Si la posición no es válida, buscar alternativa
            if (!board.isValidPosition(nextPos)) {
                return findAlternativeDirection();
            }
        }

        return nextDirection;
    }

    /**
     * Busca una dirección alternativa válida
     */
    private Direction findAlternativeDirection() {
        Direction[] directions = Direction.values();

        for (Direction dir : directions) {
            var testPos = enemy.getPosition().move(dir);
            if (board.isValidPosition(testPos)) {
                return dir;
            }
        }

        return enemy.getCurrentDirection(); // Mantener dirección actual
    }

    @Override
    public void update() {
        updateCounter++;
        // La actualización específica se maneja en el comportamiento del enemigo
    }

    @Override
    public void reset() {
        updateCounter = 0;
    }

    // Getters
    public Enemy getEnemy() {
        return enemy;
    }

    public Board getBoard() {
        return board;
    }
}
