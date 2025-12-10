package Domain;

/**
 * IA para Calamar Amarillo (YellowSquid)
 * Persigue al helado y destruye bloques de hielo en su camino
 */
public class YellowSquidAI implements AI {
    private Enemy yellowSquid;
    private Board board;
    private Direction currentDirection;
    private int abilityActivationCount;
    private static final int MAX_ABILITY_ACTIVATIONS = 3;

    public YellowSquidAI(Enemy yellowSquid, Board board) {
        this.yellowSquid = yellowSquid;
        this.board = board;
        this.currentDirection = Direction.DOWN;
        this.abilityActivationCount = 0;
    }

    @Override
    public Direction getNextMove() {
        Position squidPos = yellowSquid.getPosition();
        IceCream iceCream = board.getIceCream();

        if (iceCream == null) {
            return currentDirection;
        }

        Position targetPos = iceCream.getPosition();

        // Obtener todas las direcciones ordenadas por proximidad al objetivo
        Direction[] directionsByProximity = getDirectionsByProximity(squidPos, targetPos);

        // Intentar moverse en cada dirección, priorizando las más cercanas al objetivo
        for (Direction dir : directionsByProximity) {
            Position nextPos = calculateNextPosition(squidPos, dir);

            // Verificar si hay hielo bloqueando
            if (board.getIceBlockAt(nextPos) != null) {
                // Hay hielo, destruirlo con la habilidad
                destroyIceBlock(nextPos);
                currentDirection = dir;
                return dir;
            }

            // Si puede moverse
            if (canMove(nextPos)) {
                currentDirection = dir;
                return dir;
            }
        }

        return currentDirection;
    }

    /**
     * Destruir bloque de hielo activando la habilidad
     */
    private void destroyIceBlock(Position icePos) {
        if (abilityActivationCount < MAX_ABILITY_ACTIVATIONS) {
            yellowSquid.executeAbility();
            abilityActivationCount++;

            // Resetear contador si llegó al máximo
            if (abilityActivationCount >= MAX_ABILITY_ACTIVATIONS) {
                abilityActivationCount = 0;
            }
        }
    }

    /**
     * Calcular dirección que acerca al objetivo
     */
    private Direction calculateDirectionTowards(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

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

    /**
     * Verificar si una posición está más cerca del objetivo
     */
    private boolean isCloserToTarget(Position pos1, Position target, Position pos2) {
        int dist1 = Math.abs(pos1.getX() - target.getX()) + Math.abs(pos1.getY() - target.getY());
        int dist2 = Math.abs(pos2.getX() - target.getX()) + Math.abs(pos2.getY() - target.getY());
        return dist1 < dist2;
    }

    @Override
    public void update() {
        // Actualizar estado de la IA
    }

    @Override
    public void reset() {
        currentDirection = Direction.DOWN;
        abilityActivationCount = 0;
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
}
