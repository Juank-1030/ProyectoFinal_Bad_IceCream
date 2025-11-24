package Domain;


/**
 * Comportamiento de movimiento que persigue al jugador (helado)
 * El enemigo siempre intenta acercarse al helado
 * Usado por Maceta y Calamar Naranja
 */
public class ChaseMovement implements MovementBehavior {
    private static final long serialVersionUID = 1L;

    private transient Board board;  // Referencia al tablero (para encontrar al helado)
    private int updateCounter;

    /**
     * Constructor
     * @param board Referencia al tablero del juego
     */
    public ChaseMovement(Board board) {
        this.board = board;
        this.updateCounter = 0;
    }

    /**
     * Establece la referencia al tablero (necesario después de deserialización)
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public Direction getNextMove(Enemy enemy) {
        if (board == null) {
            // Si no hay referencia al tablero, moverse aleatoriamente
            return getRandomDirection();
        }

        // Obtener posición del helado más cercano
        Position iceCreamPosition = board.getIceCreamPosition();
        if (iceCreamPosition == null) {
            return enemy.getCurrentDirection(); // Mantener dirección actual
        }

        Position enemyPos = enemy.getPosition();

        // Calcular la dirección que más acerca al helado
        int deltaX = iceCreamPosition.getX() - enemyPos.getX();
        int deltaY = iceCreamPosition.getY() - enemyPos.getY();

        // Priorizar movimiento horizontal o vertical según la mayor distancia
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Moverse horizontalmente
            return deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (Math.abs(deltaY) > 0) {
            // Moverse verticalmente
            return deltaY > 0 ? Direction.DOWN : Direction.UP;
        }

        // Si está en la misma posición, mantener dirección
        return enemy.getCurrentDirection();
    }

    /**
     * Obtiene una dirección aleatoria
     */
    private Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[(int) (Math.random() * directions.length)];
    }

    @Override
    public void update() {
        updateCounter++;
    }

    @Override
    public void reset() {
        updateCounter = 0;
    }
}
