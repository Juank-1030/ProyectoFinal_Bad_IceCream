package Domain;

import java.io.Serializable;

/**
 * Clase abstracta base para todos los objetos del juego que se mueven
 * (Helados y Enemigos)
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Position position;
    protected Direction currentDirection;
    protected Direction lastDirection;
    protected String currentAction;
    protected int speed;
    protected boolean alive;
    protected long lastMovementTime;

    // NUEVO: Variables para movimiento suave
    protected transient float visualX;
    protected transient float visualY;
    protected static final float INTERPOLATION_SPEED = 0.35F;

    /**
     * Constructor de GameObject
     */
    public GameObject(Position position, int speed) {
        this.position = new Position(position);
        this.currentDirection = Direction.DOWN;
        this.lastDirection = Direction.DOWN;
        this.currentAction = "stand";
        this.speed = speed;
        this.alive = true;
        this.lastMovementTime = System.currentTimeMillis();

        // Inicializar posición visual
        this.visualX = (float) position.getX();
        this.visualY = (float) position.getY();
    }

    // Getters y Setters
    public Position getPosition() {
        return new Position(position);
    }

    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
        this.lastDirection = direction;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(String action) {
        this.currentAction = action;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public float getVisualX() {
        return visualX;
    }

    public float getVisualY() {
        return visualY;
    }

    /**
     * Mueve el objeto en una dirección específica
     */
    public Position move(Direction direction) {
        this.currentDirection = direction;
        this.lastDirection = direction;
        this.currentAction = "walk";
        Position newPosition = position.move(direction);
        return newPosition;
    }

    /**
     * Calcula la siguiente posición sin mover el objeto
     */
    public Position getNextPosition(Direction direction) {
        return position.move(direction);
    }

    /**
     * Actualiza la posición del objeto
     */
    public void updatePosition(Position newPosition) {
        this.position = new Position(newPosition);
    }

    /**
     * Actualiza la posición visual con interpolación rápida y fluida
     */
    public void updateVisualPosition() {
        float targetPosX = (float) position.getX();
        float targetPosY = (float) position.getY();

        float deltaX = targetPosX - visualX;
        float deltaY = targetPosY - visualY;

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 0.01F) {
            // Snap directo a la posición final
            visualX = targetPosX;
            visualY = targetPosY;

            if (currentAction.equals("walk")) {
                currentAction = "stand";
            }
        } else {
            // ✅ MOVIMIENTO RÁPIDO Y FLUIDO
            // Velocidad adaptativa: más rápido cuando está lejos, más lento cerca
            float speed = Math.min(0.3F, distance * 0.2F); // Entre 0 y 0.3 (reducido de 0.8 a 0.3)

            visualX += deltaX * speed;
            visualY += deltaY * speed;

            if (!currentAction.equals("shoot") && !currentAction.equals("break")) {
                currentAction = "walk";
            }
        }
    }

    /**
     * Sincroniza la posición visual con la lógica
     */
    public void syncVisualPosition() {
        this.visualX = (float) position.getX();
        this.visualY = (float) position.getY();
    }

    /**
     * Verifica si es tiempo de moverse basado en la velocidad
     */
    public boolean canMoveNow() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMovementTime >= speed) {
            this.lastMovementTime = currentTime;
            return true;
        }
        return false;
    }

    /**
     * Resetea el tiempo de movimiento
     */
    public void resetMovementTimer() {
        this.lastMovementTime = System.currentTimeMillis();
    }

    public abstract boolean canMoveTo(Position position);

    public abstract void update();

    public abstract String getType();

    @Override
    public String toString() {
        return getType() + " at " + position;
    }
}