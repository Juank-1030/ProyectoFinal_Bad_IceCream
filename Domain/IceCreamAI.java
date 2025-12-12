package Domain;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * IA para controlar el helado en modo MVM (Machine vs Machine)
 * Busca recolectar frutas evitando enemigos
 */
public class IceCreamAI implements AI, Serializable {

    private IceCream iceCream;
    private Board board;
    private Position targetFruit; // Fruta objetivo actual
    private int updateCounter;
    private static final int DECISION_INTERVAL = 5; // Tomar decisiones cada 5 frames

    /**
     * Constructor de IceCreamAI
     * 
     * @param iceCream El helado a controlar
     * @param board    El tablero del juego
     */
    public IceCreamAI(IceCream iceCream, Board board) {
        this.iceCream = iceCream;
        this.board = board;
        this.updateCounter = 0;
    }

    @Override
    public Direction getNextMove() {
        if (iceCream == null || !iceCream.isAlive()) {
            return null;
        }

        // Tomar decisiones solo cada cierto intervalo
        if (updateCounter % DECISION_INTERVAL != 0) {
            return iceCream.getCurrentDirection();
        }

        // Si no hay fruta objetivo o ya fue recolectada, buscar una nueva
        if (targetFruit == null || isFruitCollected(targetFruit)) {
            targetFruit = findNearestFruit();
        }

        // Si no hay frutas, no moverse
        if (targetFruit == null) {
            return null;
        }

        // Calcular dirección hacia la fruta
        Direction directionToFruit = getDirectionToTarget(targetFruit);

        // Verificar si hay peligro (enemigo cercano)
        if (isEnemyNearby()) {
            // Intentar evadir
            Direction safeDirection = findSafeDirection();
            if (safeDirection != null) {
                return safeDirection;
            }
        }

        // Verificar si necesita crear/romper hielo
        Position nextPos = iceCream.getPosition().move(directionToFruit);
        if (board.hasIceBlock(nextPos)) {
            // Romper hielo si está bloqueando el camino
            iceCream.setCurrentDirection(directionToFruit);
            // La lógica de romper hielo se ejecuta en Game
            return null; // No moverse, solo romper
        }

        return directionToFruit;
    }

    /**
     * Encuentra la fruta más cercana no recolectada
     */
    private Position findNearestFruit() {
        List<Fruit> fruits = board.getFruits();
        Position iceCreamPos = iceCream.getPosition();
        Position nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Fruit fruit : fruits) {
            if (!fruit.isCollected()) {
                int distance = iceCreamPos.distanceTo(fruit.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = fruit.getPosition();
                }
            }
        }

        return nearest;
    }

    /**
     * Calcula la dirección hacia un objetivo
     */
    private Direction getDirectionToTarget(Position target) {
        Position currentPos = iceCream.getPosition();
        int deltaX = target.getX() - currentPos.getX();
        int deltaY = target.getY() - currentPos.getY();

        // Priorizar el eje con mayor distancia
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            return deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (Math.abs(deltaY) > 0) {
            return deltaY > 0 ? Direction.DOWN : Direction.UP;
        }

        return iceCream.getCurrentDirection();
    }

    /**
     * Verifica si hay un enemigo cercano (distancia <= 3)
     */
    private boolean isEnemyNearby() {
        Position iceCreamPos = iceCream.getPosition();
        var enemies = board.getEnemies();

        for (var enemy : enemies) {
            if (enemy.isAlive()) {
                int distance = iceCreamPos.distanceTo(enemy.getPosition());
                if (distance <= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Encuentra una dirección segura (alejándose de enemigos)
     */
    private Direction findSafeDirection() {
        Position iceCreamPos = iceCream.getPosition();
        Direction[] directions = Direction.values();
        Direction safest = null;
        int maxDistance = -1;

        for (Direction dir : directions) {
            Position testPos = iceCreamPos.move(dir);

            if (!board.isValidPosition(testPos)) {
                continue;
            }

            // Calcular distancia mínima a enemigos desde esta posición
            int minEnemyDistance = getMinEnemyDistance(testPos);

            if (minEnemyDistance > maxDistance) {
                maxDistance = minEnemyDistance;
                safest = dir;
            }
        }

        return safest;
    }

    /**
     * Obtiene la distancia mínima a cualquier enemigo desde una posición
     */
    private int getMinEnemyDistance(Position pos) {
        int minDistance = Integer.MAX_VALUE;
        var enemies = board.getEnemies();

        for (var enemy : enemies) {
            if (enemy.isAlive()) {
                int distance = pos.distanceTo(enemy.getPosition());
                minDistance = Math.min(minDistance, distance);
            }
        }

        return minDistance;
    }

    /**
     * Verifica si una fruta fue recolectada
     */
    private boolean isFruitCollected(Position fruitPos) {
        Fruit fruit = board.getFruitAt(fruitPos);
        return fruit == null || fruit.isCollected();
    }

    @Override
    public void update() {
        updateCounter++;
    }

    @Override
    public void reset() {
        updateCounter = 0;
        targetFruit = null;
    }

    // Getters
    public IceCream getIceCream() {
        return iceCream;
    }

    public Board getBoard() {
        return board;
    }
}
