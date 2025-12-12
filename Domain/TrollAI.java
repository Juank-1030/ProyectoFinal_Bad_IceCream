package Domain;

import java.io.Serializable;

/**
 * IA para el Troll
 * Se mueve en círculos siguiendo el sentido del reloj por los bordes del mapa
 * Busca siempre el camino más largo disponible
 */
public class TrollAI implements AI, Serializable {
    private Enemy troll;
    private Board board;
    private Direction currentDirection;
    private int moveCount;

    // Dirección del reloj: UP, RIGHT, DOWN, LEFT (sentido horario)
    private static final Direction[] CLOCKWISE = { Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT };
    private int clockwiseIndex = 0;

    public TrollAI(Enemy troll, Board board) {
        this.troll = troll;
        this.board = board;
        this.currentDirection = Direction.RIGHT;
        this.moveCount = 0;
    }

    @Override
    public Direction getNextMove() {
        Position currentPos = troll.getPosition();

        // Intentar moverse en dirección del reloj
        Direction nextDir = CLOCKWISE[clockwiseIndex];
        Position nextPos = calculateNextPosition(currentPos, nextDir);

        // Si puede moverse en esa dirección, mantenerla
        if (canMove(nextPos)) {
            currentDirection = nextDir;
            moveCount++;
            return nextDir;
        }

        // Si no puede, girar en sentido del reloj (cambiar dirección)
        clockwiseIndex = (clockwiseIndex + 1) % 4;
        nextDir = CLOCKWISE[clockwiseIndex];
        nextPos = calculateNextPosition(currentPos, nextDir);

        if (canMove(nextPos)) {
            currentDirection = nextDir;
            moveCount++;
            return nextDir;
        }

        // Si aún no puede, buscar cualquier dirección válida
        for (Direction dir : Direction.values()) {
            nextPos = calculateNextPosition(currentPos, dir);
            if (canMove(nextPos)) {
                currentDirection = dir;
                moveCount++;
                return dir;
            }
        }

        // No hay movimiento disponible
        return currentDirection;
    }

    @Override
    public void update() {
        // Actualizar cada cierto tiempo
    }

    @Override
    public void reset() {
        currentDirection = Direction.RIGHT;
        moveCount = 0;
        clockwiseIndex = 0;
    }

    private Position calculateNextPosition(Position current, Direction direction) {
        int x = current.getX();
        int y = current.getY();

        switch (direction) {
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        return new Position(x, y);
    }

    private boolean canMove(Position pos) {
        // Verificar que no sea null
        if (pos == null)
            return false;

        // Verificar que no hay enemigo en esa posición
        Enemy enemyAtPos = board.getEnemyAt(pos);
        return enemyAtPos == null;
    }
}
