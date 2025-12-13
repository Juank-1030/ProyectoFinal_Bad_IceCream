package Domain;

import java.util.List;

/**
 * Estrategia IA Expert: Estrategia balanceada y experta
 * Balancea recoger frutas y evitar enemigos
 * Realiza movimientos más inteligentes considerando múltiples factores
 */
public class ExpertAIStrategy implements IceCreamAIStrategy {
    private static final long serialVersionUID = 1L;
    private static final int DANGER_DISTANCE = 4; // Distancia mínima a enemigos
    private static final int FRUIT_PRIORITY_DISTANCE = 6; // Distancia para priorizar frutas

    @Override
    public Direction getNextMove(Board board, IceCream iceCream) {
        Position currentPos = iceCream.getPosition();
        List<Enemy> enemies = board.getEnemies();
        List<Fruit> fruits = board.getFruits();

        // PRIORIDAD 1: Si hay peligro INMEDIATO, HUIR primero
        Enemy closestEnemy = getClosestEnemy(currentPos, enemies);
        if (closestEnemy != null && getDistance(currentPos, closestEnemy.getPosition()) <= DANGER_DISTANCE) {
            Direction fleeDir = getDirectionAwayFrom(currentPos, closestEnemy.getPosition(), board);
            if (fleeDir != null) {
                return fleeDir;
            }
            // Si no puede huir, buscar cualquier escape
            Direction escapeDir = findAnyValidDirection(board, currentPos);
            if (escapeDir != null) {
                return escapeDir;
            }
        }

        // PRIORIDAD 2: Si hay frutas cercanas Y REACHABLE, ir por ellas
        if (fruits != null && !fruits.isEmpty()) {
            Fruit reachableFruit = getClosestReachableFruit(currentPos, fruits, board);
            if (reachableFruit != null) {
                double fruitDistance = getDistance(currentPos, reachableFruit.getPosition());
                if (fruitDistance <= FRUIT_PRIORITY_DISTANCE) {
                    Direction towardFruit = getDirectionTowards(currentPos, reachableFruit.getPosition(), board);
                    if (towardFruit != null) {
                        return towardFruit;
                    }
                }
            }
        }

        // PRIORIDAD 3: Si hay enemigos moderadamente cerca, alejarse
        if (closestEnemy != null && getDistance(currentPos, closestEnemy.getPosition()) <= DANGER_DISTANCE * 2.5) {
            Direction awayDir = getDirectionAwayFrom(currentPos, closestEnemy.getPosition(), board);
            if (awayDir != null) {
                return awayDir;
            }
        }

        // PRIORIDAD 4: Ir hacia frutas lejanas REACHABLE para completar el nivel
        if (fruits != null && !fruits.isEmpty()) {
            Fruit reachableFruit = getClosestReachableFruit(currentPos, fruits, board);
            if (reachableFruit != null) {
                Direction towardFruit = getDirectionTowards(currentPos, reachableFruit.getPosition(), board);
                if (towardFruit != null) {
                    return towardFruit;
                }
            }
        }

        // PRIORIDAD 5: Explorar
        Direction exploreDir = explorarActivamente(board, currentPos);
        if (exploreDir != null) {
            return exploreDir;
        }

        // FALLBACK: Cualquier dirección válida o con hielo
        Direction[] allDirs = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
        for (Direction dir : allDirs) {
            Position nextPos = currentPos.move(dir);
            if (board.isInBounds(nextPos) && (board.isValidPosition(nextPos) || board.hasIceBlock(nextPos))) {
                return dir;
            }
        }

        // ULTIMO FALLBACK: si todo está bloqueado, devolver primera dirección en rango
        for (Direction dir : allDirs) {
            Position nextPos = currentPos.move(dir);
            if (board.isInBounds(nextPos)) {
                return dir;
            }
        }

        return Direction.DOWN; // Nunca devolver null
    }

    private Enemy getClosestEnemy(Position from, List<Enemy> enemies) {
        if (enemies.isEmpty())
            return null;

        Enemy closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = getDistance(from, enemy.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }

    private Fruit getClosestReachableFruit(Position from, List<Fruit> fruits, Board board) {
        if (fruits.isEmpty())
            return null;

        Fruit closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Fruit fruit : fruits) {
            double distance = getDistance(from, fruit.getPosition());
            if (distance < minDistance) {
                // Validar que la fruta es alcanzable
                Direction testDir = getDirectionTowards(from, fruit.getPosition(), board);
                if (testDir != null) { // Solo considerar si es reachable
                    minDistance = distance;
                    closest = fruit;
                }
            }
        }
        return closest;
    }

    private double getDistance(Position a, Position b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Direction getDirectionTowards(Position from, Position to, Board board) {
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());

        Direction[] attempts = getPriorityDirections(dx, dy);

        for (Direction dir : attempts) {
            Position nextPos = from.move(dir);
            if (board.isInBounds(nextPos)) {
                if (board.isValidPosition(nextPos) || board.hasIceBlock(nextPos)) {
                    return dir;
                }
            }
        }
        return null;
    }

    private Direction getDirectionAwayFrom(Position from, Position threat, Board board) {
        int dx = Integer.compare(from.getX(), threat.getX());
        int dy = Integer.compare(from.getY(), threat.getY());

        Direction[] attempts = getPriorityDirections(dx, dy);

        for (Direction dir : attempts) {
            Position nextPos = from.move(dir);
            if (board.isInBounds(nextPos)) {
                if (board.isValidPosition(nextPos) || board.hasIceBlock(nextPos)) {
                    return dir;
                }
            }
        }
        return null;
    }

    private Direction[] getPriorityDirections(int dx, int dy) {
        Direction[] dirs = new Direction[4];
        int idx = 0;

        if (dx > 0)
            dirs[idx++] = Direction.RIGHT;
        if (dx < 0)
            dirs[idx++] = Direction.LEFT;
        if (dy > 0)
            dirs[idx++] = Direction.DOWN;
        if (dy < 0)
            dirs[idx++] = Direction.UP;

        if (idx < 4) {
            if (dx <= 0 && idx < 4)
                dirs[idx++] = Direction.LEFT;
            if (dx >= 0 && idx < 4)
                dirs[idx++] = Direction.RIGHT;
            if (dy <= 0 && idx < 4)
                dirs[idx++] = Direction.UP;
            if (dy >= 0 && idx < 4)
                dirs[idx++] = Direction.DOWN;
        }

        return dirs;
    }

    /**
     * Explora en todas direcciones cuando no hay movimiento óptimo
     */
    private Direction explorarActivamente(Board board, Position from) {
        Direction[] dirs = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
        for (Direction dir : dirs) {
            Position nextPos = from.move(dir);
            if (board.isValidPosition(nextPos)) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Encuentra cualquier dirección válida o con hielo rompible
     */
    private Direction findAnyValidDirection(Board board, Position from) {
        Direction[] dirs = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };

        // Primero intentar direcciones completamente válidas
        for (Direction dir : dirs) {
            Position nextPos = from.move(dir);
            if (board.isInBounds(nextPos) && board.isValidPosition(nextPos)) {
                return dir;
            }
        }

        // Si no hay válidas, intentar con hielo rompible
        for (Direction dir : dirs) {
            Position nextPos = from.move(dir);
            if (board.isInBounds(nextPos) && board.hasIceBlock(nextPos)) {
                return dir;
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return "Expert";
    }
}
