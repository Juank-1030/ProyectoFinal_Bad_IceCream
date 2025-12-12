package Domain;

import java.util.List;

/**
 * Estrategia IA Fearful: El helado se protege de los enemigos
 * Huye del enemigo más cercano o se aleja de todos los enemigos
 */
public class FearfulAIStrategy implements IceCreamAIStrategy {
    private static final long serialVersionUID = 1L;
    private static final int DANGER_DISTANCE = 5; // Distancia a considerar como peligro

    @Override
    public Direction getNextMove(Board board, IceCream iceCream) {
        Position currentPos = iceCream.getPosition();
        List<Enemy> enemies = board.getEnemies();

        // PRIORIDAD 1: Si hay enemigos cercanos, HUIR
        if (enemies != null && !enemies.isEmpty()) {
            Enemy closestEnemy = null;
            double minDistance = Double.MAX_VALUE;

            for (Enemy enemy : enemies) {
                double distance = getDistance(currentPos, enemy.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = enemy;
                }
            }

            // Si hay peligro inmediato (dentro de 5 celdas), huir
            if (closestEnemy != null && minDistance <= DANGER_DISTANCE) {
                Direction fleeDir = getDirectionAwayFrom(currentPos, closestEnemy.getPosition(), board);
                if (fleeDir != null) {
                    return fleeDir;
                }
                // Si no puede huir en la dirección preferida, buscar cualquier dirección válida
                Direction escapeDir = findAnyValidDirection(board, currentPos);
                if (escapeDir != null) {
                    return escapeDir;
                }
            }
        }

        // PRIORIDAD 2: No hay peligro, explorar activamente
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

    /**
     * Calcula la distancia euclidiana entre dos posiciones
     */
    private double getDistance(Position a, Position b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Obtiene la dirección para alejarse del objetivo
     */
    private Direction getDirectionAwayFrom(Position from, Position threat, Board board) {
        int dx = Integer.compare(from.getX(), threat.getX()); // Invertido para alejarse
        int dy = Integer.compare(from.getY(), threat.getY());

        // Intentar primero moverse en la dirección opuesta al enemigo
        Direction[] attempts = getPriorityDirections(dx, dy);

        for (Direction dir : attempts) {
            Position nextPos = from.move(dir);
            if (board.isInBounds(nextPos)) {
                if (board.isValidPosition(nextPos)) {
                    return dir;
                }
                // Si hay hielo, también devolver (Game lo romperá)
                if (board.hasIceBlock(nextPos)) {
                    return dir;
                }
            }
        }

        return null; // No hay escape posible
    }

    /**
     * Obtiene las direcciones ordenadas por prioridad (alejamiento)
     */
    private Direction[] getPriorityDirections(int dx, int dy) {
        Direction[] dirs = new Direction[4];
        int idx = 0;

        // Priorizar alejamiento en eje X
        if (dx > 0)
            dirs[idx++] = Direction.RIGHT;
        if (dx < 0)
            dirs[idx++] = Direction.LEFT;
        if (dy > 0)
            dirs[idx++] = Direction.DOWN;
        if (dy < 0)
            dirs[idx++] = Direction.UP;

        // Llenar con direcciones restantes
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
     * Explora en todas direcciones cuando no hay peligro
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
        return "Fearful";
    }
}
