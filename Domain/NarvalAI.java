package Domain;

import java.util.*;

/**
 * IA para Narval
 * Movimiento aleatorio, pero activa su habilidad si detecta al helado
 * en la misma fila o columna
 */
public class NarvalAI implements AI {
    private Enemy narval;
    private Board board;
    private Direction currentDirection;
    private Random random;
    private int stepsInDirection;
    private static final int MAX_STEPS_SAME_DIRECTION = 4;

    public NarvalAI(Enemy narval, Board board) {
        this.narval = narval;
        this.board = board;
        this.random = new Random();
        this.currentDirection = getRandomDirection();
        this.stepsInDirection = 0;
    }

    @Override
    public Direction getNextMove() {
        Position narvalPos = narval.getPosition();
        IceCream iceCream = board.getIceCream();

        // Si el helado está en la misma fila o columna, activar habilidad
        if (iceCream != null && isAlignedWithIceCream(narvalPos, iceCream.getPosition())) {
            // Activar habilidad (ejecutar el ataque)
            narval.executeAbility();

            // Moverse hacia el helado
            Direction dirToIceCream = getDirectionToIceCream(narvalPos, iceCream.getPosition());
            Position nextPos = calculateNextPosition(narvalPos, dirToIceCream);

            if (canMove(nextPos)) {
                currentDirection = dirToIceCream;
                return dirToIceCream;
            }
        }

        // Movimiento aleatorio normal
        Position nextPos = calculateNextPosition(narvalPos, currentDirection);

        if (canMove(nextPos)) {
            stepsInDirection++;
            if (stepsInDirection >= MAX_STEPS_SAME_DIRECTION) {
                currentDirection = getRandomDirection();
                stepsInDirection = 0;
            }
            return currentDirection;
        }

        // Cambiar dirección si está bloqueado
        currentDirection = getRandomDirection();
        stepsInDirection = 0;

        nextPos = calculateNextPosition(narvalPos, currentDirection);
        if (canMove(nextPos)) {
            return currentDirection;
        }

        // Buscar cualquier dirección válida
        for (Direction dir : Direction.values()) {
            nextPos = calculateNextPosition(narvalPos, dir);
            if (canMove(nextPos)) {
                currentDirection = dir;
                stepsInDirection = 0;
                return dir;
            }
        }

        return currentDirection;
    }

    /**
     * Verificar si el helado está en la misma fila o columna
     */
    private boolean isAlignedWithIceCream(Position narvalPos, Position iceCreamPos) {
        return narvalPos.getX() == iceCreamPos.getX() || // Misma columna (vertical)
                narvalPos.getY() == iceCreamPos.getY(); // Misma fila (horizontal)
    }

    /**
     * Obtener dirección hacia el helado
     */
    private Direction getDirectionToIceCream(Position from, Position to) {
        // Priorizar si está en la misma fila
        if (from.getY() == to.getY()) {
            if (to.getX() > from.getX())
                return Direction.RIGHT;
            if (to.getX() < from.getX())
                return Direction.LEFT;
        }

        // O en la misma columna
        if (from.getX() == to.getX()) {
            if (to.getY() > from.getY())
                return Direction.DOWN;
            if (to.getY() < from.getY())
                return Direction.UP;
        }

        return currentDirection;
    }

    @Override
    public void update() {
        // Actualizar estado de la IA
    }

    @Override
    public void reset() {
        currentDirection = getRandomDirection();
        stepsInDirection = 0;
    }

    private Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[random.nextInt(directions.length)];
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
