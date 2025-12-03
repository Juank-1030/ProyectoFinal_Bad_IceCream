package Domain;

import java.util.*;

/**
 * IA para Pot (Maceta)
 * Sin habilidad: movimiento aleatorio hasta bloquearse
 * Con habilidad activa: persigue al helado más cercano
 */
public class PotAI implements AI {
    private Enemy pot;
    private Board board;
    private Direction currentDirection;
    private Random random;
    private int stepsInDirection;
    private static final int MAX_STEPS_SAME_DIRECTION = 5;

    public PotAI(Enemy pot, Board board) {
        this.pot = pot;
        this.board = board;
        this.random = new Random();
        this.currentDirection = getRandomDirection();
        this.stepsInDirection = 0;
    }

    @Override
    public Direction getNextMove() {
        Position currentPos = pot.getPosition();

        // Si tiene habilidad activa, perseguir al helado más cercano
        if (pot.getMovementBehavior() instanceof ChaseMovement) {
            return chaseNearestIceCream(currentPos);
        }

        // Movimiento aleatorio
        Position nextPos = calculateNextPosition(currentPos, currentDirection);

        // Si puede moverse en la dirección actual
        if (canMove(nextPos)) {
            stepsInDirection++;
            // Cambiar dirección aleatoriamente cada cierto tiempo
            if (stepsInDirection >= MAX_STEPS_SAME_DIRECTION) {
                currentDirection = getRandomDirection();
                stepsInDirection = 0;
            }
            return currentDirection;
        }

        // Bloqueado, cambiar a dirección aleatoria
        currentDirection = getRandomDirection();
        stepsInDirection = 0;

        // Intentar nuevamente con la nueva dirección
        nextPos = calculateNextPosition(currentPos, currentDirection);
        if (canMove(nextPos)) {
            return currentDirection;
        }

        // Si no puede, buscar cualquier dirección válida
        for (Direction dir : Direction.values()) {
            nextPos = calculateNextPosition(currentPos, dir);
            if (canMove(nextPos)) {
                currentDirection = dir;
                stepsInDirection = 0;
                return dir;
            }
        }

        return currentDirection;
    }

    /**
     * Perseguir al helado más cercano
     */
    private Direction chaseNearestIceCream(Position currentPos) {
        IceCream iceCream = board.getIceCream();
        if (iceCream == null) {
            return getRandomDirection();
        }

        Position targetPos = iceCream.getPosition();
        return calculateDirectionTowards(currentPos, targetPos);
    }

    /**
     * Calcular la dirección que acerca más al objetivo
     */
    private Direction calculateDirectionTowards(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        // Priorizar movimiento horizontal si está más lejos
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                return Direction.RIGHT;
            if (dx < 0)
                return Direction.LEFT;
        } else {
            if (dy > 0)
                return Direction.DOWN;
            if (dy < 0)
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
