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

        // SIEMPRE perseguir al helado (ampliar rango de búsqueda)
        IceCream iceCream = board.getIceCream();
        if (iceCream != null) {
            Position targetPos = iceCream.getPosition();

            // Obtener todas las direcciones ordenadas por proximidad al objetivo
            Direction[] directionsByProximity = getDirectionsByProximity(currentPos, targetPos);

            // Intentar moverse en cada dirección, priorizando las más cercanas al objetivo
            for (Direction dir : directionsByProximity) {
                Position nextPos = calculateNextPosition(currentPos, dir);
                if (canMove(nextPos)) {
                    currentDirection = dir;
                    stepsInDirection = 0;
                    return dir;
                }
            }
        }

        // Fallback: movimiento aleatorio si no puede perseguir
        Position nextPos = calculateNextPosition(currentPos, currentDirection);
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

        nextPos = calculateNextPosition(currentPos, currentDirection);
        if (canMove(nextPos)) {
            return currentDirection;
        }

        // Buscar cualquier dirección válida
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
     * Obtener todas las direcciones ordenadas por proximidad al objetivo
     */
    private Direction[] getDirectionsByProximity(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        Direction[] directions = new Direction[4];
        int index = 0;

        // Agregar direcciones por distancia
        if (Math.abs(dx) > Math.abs(dy)) {
            // Prioridad horizontal
            if (dx > 0) {
                directions[index++] = Direction.RIGHT;
            } else if (dx < 0) {
                directions[index++] = Direction.LEFT;
            }

            if (dy > 0) {
                directions[index++] = Direction.DOWN;
            } else if (dy < 0) {
                directions[index++] = Direction.UP;
            }
        } else {
            // Prioridad vertical
            if (dy > 0) {
                directions[index++] = Direction.DOWN;
            } else if (dy < 0) {
                directions[index++] = Direction.UP;
            }

            if (dx > 0) {
                directions[index++] = Direction.RIGHT;
            } else if (dx < 0) {
                directions[index++] = Direction.LEFT;
            }
        }

        // Completar con direcciones restantes
        for (Direction d : Direction.values()) {
            boolean found = false;
            for (int i = 0; i < index; i++) {
                if (directions[i] == d) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                directions[index++] = d;
            }
        }

        return directions;
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

        // Verificar que no sea un muro
        if (board.isWall(pos)) {
            return false;
        }

        // Verificar que no hay hielo en esa posición
        if (board.getIceBlockAt(pos) != null) {
            return false;
        }

        // Verificar que no hay enemigo en esa posición
        Enemy enemyAtPos = board.getEnemyAt(pos);
        return enemyAtPos == null;
    }
}
