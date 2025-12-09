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

        // Primero: verificar si hay peligro inmediato
        Enemy closestEnemy = getClosestEnemy(currentPos, enemies);
        if (closestEnemy != null && getDistance(currentPos, closestEnemy.getPosition()) <= DANGER_DISTANCE) {
            // Hay peligro: huir
            return getDirectionAwayFrom(currentPos, closestEnemy.getPosition(), board);
        }

        // Segundo: si hay frutas cercanas, ir por ellas
        if (!fruits.isEmpty()) {
            Fruit closestFruit = getClosestFruit(currentPos, fruits);
            if (closestFruit != null) {
                double fruitDistance = getDistance(currentPos, closestFruit.getPosition());
                // Si la fruta está cerca y no hay peligro, ir por ella
                if (fruitDistance <= FRUIT_PRIORITY_DISTANCE) {
                    Direction towardFruit = getDirectionTowards(currentPos, closestFruit.getPosition(), board);
                    if (towardFruit != null) {
                        return towardFruit;
                    }
                }
            }
        }

        // Tercero: si hay enemigos cerca, alejarse preventivamente
        if (closestEnemy != null && getDistance(currentPos, closestEnemy.getPosition()) <= DANGER_DISTANCE * 2) {
            return getDirectionAwayFrom(currentPos, closestEnemy.getPosition(), board);
        }

        // Cuarto: explorar buscando frutas
        if (!fruits.isEmpty()) {
            Fruit closestFruit = getClosestFruit(currentPos, fruits);
            if (closestFruit != null) {
                return getDirectionTowards(currentPos, closestFruit.getPosition(), board);
            }
        }

        return null; // Esperar
    }

    private Enemy getClosestEnemy(Position from, List<Enemy> enemies) {
        if (enemies.isEmpty()) return null;
        
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

    private Fruit getClosestFruit(Position from, List<Fruit> fruits) {
        if (fruits.isEmpty()) return null;
        
        Fruit closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Fruit fruit : fruits) {
            double distance = getDistance(from, fruit.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                closest = fruit;
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
            if (board.isValidPosition(nextPos)) {
                return dir;
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
            if (board.isValidPosition(nextPos)) {
                return dir;
            }
        }
        return null;
    }

    private Direction[] getPriorityDirections(int dx, int dy) {
        Direction[] dirs = new Direction[4];
        int idx = 0;

        if (dx > 0) dirs[idx++] = Direction.RIGHT;
        if (dx < 0) dirs[idx++] = Direction.LEFT;
        if (dy > 0) dirs[idx++] = Direction.DOWN;
        if (dy < 0) dirs[idx++] = Direction.UP;

        if (idx < 4) {
            if (dx <= 0 && idx < 4) dirs[idx++] = Direction.LEFT;
            if (dx >= 0 && idx < 4) dirs[idx++] = Direction.RIGHT;
            if (dy <= 0 && idx < 4) dirs[idx++] = Direction.UP;
            if (dy >= 0 && idx < 4) dirs[idx++] = Direction.DOWN;
        }

        return dirs;
    }

    @Override
    public String getName() {
        return "Expert";
    }
}
