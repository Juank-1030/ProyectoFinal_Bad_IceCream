package Domain;

/**
 * Comportamiento de movimiento que persigue al jugador (helado)
 * El enemigo siempre intenta acercarse al helado
 * Usado por Maceta y Calamar Naranja
 * 
 * Mejora MVC:
 * - Usa BoardStateProvider en lugar de Board directo
 * - Desacoplamiento: No depende de la clase Board concreta
 * - Fácil de testear: Se puede inyectar un mock de BoardStateProvider
 */
public class ChaseMovement implements MovementBehavior {
    private static final long serialVersionUID = 1L;

    private transient BoardStateProvider stateProvider; // Interfaz desacoplada

    /**
     * Constructor
     * 
     * @param stateProvider Proveedor del estado del tablero (abstracción)
     */
    public ChaseMovement(BoardStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }

    /**
     * Establece el proveedor de estado (necesario después de deserialización)
     */
    public void setStateProvider(BoardStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }

    @Override
    public Direction getNextMove(Enemy enemy) {
        if (stateProvider == null) {
            // Si no hay referencia al estado, moverse aleatoriamente
            return getRandomDirection();
        }

        // Obtener posición del helado más cercano
        Position iceCreamPosition = stateProvider.getIceCreamPosition();
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
        // Comportamiento de seguimiento se ejecuta en getNextMove()
    }

    @Override
    public void reset() {
        // Reiniciar estado si es necesario
    }
}