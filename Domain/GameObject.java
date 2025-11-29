package Domain;

import java.io.Serializable;

/**
 * Clase abstracta base para todos los objetos del juego que se mueven
 * (Helados y Enemigos)
 * Define los movimientos generales y comportamientos comunes
 */
public abstract class GameObject implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Position position;
    protected Direction currentDirection;
    protected int speed; // Velocidad de movimiento (milisegundos entre movimientos)
    protected boolean alive;
    protected long lastMovementTime; // Último tiempo que se movió (para throttling)

    /**
     * Constructor de GameObject
     * 
     * @param position Posición inicial
     * @param speed    Velocidad del objeto (ms entre movimientos: 250ms = 4 cps,
     *                 1000ms = 1 cps)
     */
    public GameObject(Position position, int speed) {
        this.position = new Position(position);
        this.currentDirection = Direction.DOWN;
        this.speed = speed;
        this.alive = true;
        this.lastMovementTime = System.currentTimeMillis();
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

    /**
     * Mueve el objeto en una dirección específica
     * 
     * @param direction Dirección del movimiento
     * @return La nueva posición después del movimiento
     */
    public Position move(Direction direction) {
        this.currentDirection = direction;
        Position newPosition = position.move(direction);
        return newPosition;
    }

    /**
     * Calcula la siguiente posición sin mover el objeto
     * 
     * @param direction Dirección a calcular
     * @return La posición resultante
     */
    public Position getNextPosition(Direction direction) {
        return position.move(direction);
    }

    /**
     * Actualiza la posición del objeto
     * 
     * @param newPosition Nueva posición
     */
    public void updatePosition(Position newPosition) {
        this.position = new Position(newPosition);
    }

    /**
     * Verifica si es tiempo de moverse basado en la velocidad
     * 
     * @return true si es tiempo de moverse, false si debe esperar
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
     * Resetea el tiempo de movimiento (útil para cambios de dirección)
     */
    public void resetMovementTimer() {
        this.lastMovementTime = System.currentTimeMillis();
    }

    /**
     * Verifica si puede moverse a una posición específica
     * Este método debe ser implementado por las subclases según sus reglas
     */
    public abstract boolean canMoveTo(Position position);

    /**
     * Actualiza el estado del objeto (para IA, animaciones, etc.)
     */
    public abstract void update();

    /**
     * Obtiene el tipo de objeto (para identificación)
     */
    public abstract String getType();

    @Override
    public String toString() {
        return getType() + " at " + position;
    }
}
