package Domain;

import java.util.List;

/**
 * Estrategia IA Hungry: El helado busca recoger frutas
 * 
 * COMPORTAMIENTO:
 * 1. Busca la fruta más cercana sin recoger
 * 2. Se mueve hacia ella
 * 3. Si hay hielo bloqueando el camino, intenta romperlo
 * 4. Si está bloqueado, busca ruta alternativa
 */
public class HungryAIStrategy implements IceCreamAIStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public Direction getNextMove(Board board, IceCream iceCream) {
        Position currentPos = iceCream.getPosition();
        List<Fruit> allFruits = board.getFruits();

        // Filtrar solo las frutas NO recolectadas
        Fruit closest = null;
        double minDist = Double.MAX_VALUE;

        if (allFruits != null) {
            for (Fruit f : allFruits) {
                // IMPORTANTE: Solo considerar frutas no recolectadas
                if (f != null && !f.isCollected()) {
                    double dist = distance(currentPos, f.getPosition());
                    if (dist < minDist) {
                        minDist = dist;
                        closest = f;
                    }
                }
            }
        }

        // Si encontramos una fruta NO recolectada, muévete hacia ella
        if (closest != null) {
            Position target = closest.getPosition();
            int targetX = target.getX();
            int targetY = target.getY();
            int currentX = currentPos.getX();
            int currentY = currentPos.getY();

            // Prioridad: Muévete primero en X, luego en Y
            Direction preferredDir = null;

            if (targetX < currentX) {
                preferredDir = Direction.LEFT;
            } else if (targetX > currentX) {
                preferredDir = Direction.RIGHT;
            } else if (targetY < currentY) {
                preferredDir = Direction.UP;
            } else if (targetY > currentY) {
                preferredDir = Direction.DOWN;
            }

            // Si tenemos una dirección preferida, verificar si es pasable
            if (preferredDir != null) {
                Position nextPos = currentPos.move(preferredDir);

                // ✅ Si es una posición válida (sin hielo, sin muros), ir allí
                if (board.isValidPosition(nextPos)) {
                    return preferredDir;
                }

                // ✅ Si hay hielo, AVISAR que se rompa (devolver la dirección)
                // Game.java se encargará de romper el hielo
                if (board.hasIceBlock(nextPos)) {
                    return preferredDir; // Devolver dirección del hielo
                }
            }

            // Si no puede ir directo, buscar camino alternativo
            return findAlternativeDirection(currentPos, board);
        }

        // Si no hay frutas sin recoger, devuelve DOWN como dirección por defecto
        return Direction.DOWN;
    }

    /**
     * Busca ruta alternativa cuando hay obstáculo
     */
    private Direction findAlternativeDirection(Position current, Board board) {
        // Intentar direcciones en orden: UP, DOWN, LEFT, RIGHT
        Direction[] directions = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };

        // Primero, intentar las direcciones válidas (sin obstáculos)
        for (Direction d : directions) {
            Position testPos = current.move(d);
            if (board.isValidPosition(testPos)) {
                return d;
            }
        }

        // Si todas están bloqueadas, devolver cualquier dirección con hielo para romper
        for (Direction d : directions) {
            Position testPos = current.move(d);
            if (board.isInBounds(testPos) && board.hasIceBlock(testPos)) {
                return d; // Devolver dirección del hielo para que Game lo rompa
            }
        }

        return Direction.DOWN; // Fallback
    }

    private double distance(Position a, Position b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt((double) (dx * dx + dy * dy));
    }

    @Override
    public String getName() {
        return "Hungry";
    }
}