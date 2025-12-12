package Domain;

/**
 * Comportamiento para frutas que se mueven constantemente
 * Usado por Piña
 */
public class MovingFruitBehavior implements FruitBehavior {
    private static final long serialVersionUID = 1L;

    private transient Board board; // Referencia al tablero para validar movimientos
    private Direction currentDirection;
    private int moveCounter;
    private int movesPerUpdate; // Cada cuántos updates se mueve

    /**
     * Constructor con valores por defecto (ÚNICO USADO)
     */
    public MovingFruitBehavior(Board board) {
        this.board = board;
        this.currentDirection = getRandomDirection();
        this.movesPerUpdate = 2;
        this.moveCounter = 0;
    }

    /**
     * Establece la referencia al tablero (necesario después de deserialización)
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public Position updatePosition(Position currentPosition) {
        moveCounter++;

        if (moveCounter < movesPerUpdate) {
            return null; // No se mueve en este update
        }

        moveCounter = 0;

        // Intentar moverse en la dirección actual
        Position newPosition = currentPosition.move(currentDirection);

        // Verificar si la nueva posición es válida
        if (board != null && !board.isValidPosition(newPosition)) {
            // Cambiar a una dirección aleatoria válida
            currentDirection = findValidDirection(currentPosition);
            if (currentDirection != null) {
                newPosition = currentPosition.move(currentDirection);
            } else {
                return null; // No hay movimientos válidos
            }
        }

        return newPosition;
    }

    /**
     * Encuentra una dirección válida desde la posición actual
     */
    private Direction findValidDirection(Position position) {
        Direction[] directions = Direction.values();

        // Mezclar direcciones aleatoriamente
        for (int i = 0; i < directions.length; i++) {
            int randomIndex = (int) (Math.random() * directions.length);
            Direction temp = directions[i];
            directions[i] = directions[randomIndex];
            directions[randomIndex] = temp;
        }

        // Buscar la primera dirección válida
        for (Direction dir : directions) {
            Position testPos = position.move(dir);
            if (board != null && board.isValidPosition(testPos)) {
                return dir;
            }
        }

        return null;
    }

    /**
     * Obtiene una dirección aleatoria
     */
    private static Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[(int) (Math.random() * directions.length)];
    }

    @Override
    public void reset() {
        moveCounter = 0;
        currentDirection = getRandomDirection();
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
    }
}
