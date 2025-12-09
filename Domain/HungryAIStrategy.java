package Domain;

import java.util.List;

/**
 * Estrategia IA Hungry: El helado busca recoger frutas
 * Prioriza acercarse a la fruta más cercana
 */
public class HungryAIStrategy implements IceCreamAIStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public Direction getNextMove(Board board, IceCream iceCream) {
        Position currentPos = iceCream.getPosition();
        List<Fruit> fruits = board.getFruits();

        if (fruits.isEmpty()) {
            return null; // No hay frutas, no hacer nada
        }

        // Encontrar la fruta más cercana
        Fruit closestFruit = null;
        double minDistance = Double.MAX_VALUE;

        for (Fruit fruit : fruits) {
            double distance = getDistance(currentPos, fruit.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                closestFruit = fruit;
            }
        }

        if (closestFruit == null) {
            return null;
        }

        // Calcular dirección hacia la fruta más cercana
        return getDirectionTowards(currentPos, closestFruit.getPosition(), board);
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
     * Obtiene la dirección preferida hacia el objetivo
     */
    private Direction getDirectionTowards(Position from, Position to, Board board) {
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());

        // Intentar primero moverse en la dirección con mayor diferencia
        Direction[] attempts = getPriorityDirections(dx, dy);

        for (Direction dir : attempts) {
            Position nextPos = from.move(dir);
            if (board.isValidPosition(nextPos)) {
                return dir;
            }
        }

        return null; // No hay movimiento válido
    }

    /**
     * Obtiene las direcciones ordenadas por prioridad
     */
    private Direction[] getPriorityDirections(int dx, int dy) {
        Direction[] dirs = new Direction[4];
        int idx = 0;

        // Priorizar eje X
        if (dx > 0) dirs[idx++] = Direction.RIGHT;
        if (dx < 0) dirs[idx++] = Direction.LEFT;
        if (dy > 0) dirs[idx++] = Direction.DOWN;
        if (dy < 0) dirs[idx++] = Direction.UP;

        // Llenar con direcciones restantes
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
        return "Hungry";
    }
}
