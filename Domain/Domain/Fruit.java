package Domain;

import java.io.Serializable;

/**
 * Clase abstracta que representa una fruta en el juego
 * Las frutas tienen diferentes comportamientos (estáticas, móviles, teletransporte)
 */
public abstract class Fruit implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Position position;
    protected String fruitType;
    protected boolean collected;
    protected FruitBehavior behavior;

    /**
     * Constructor de Fruit
     * @param position Posición inicial
     * @param fruitType Tipo de fruta
     */
    public Fruit(Position position, String fruitType) {
        this.position = new Position(position);
        this.fruitType = fruitType;
        this.collected = false;
    }

    /**
     * Establece el comportamiento de la fruta
     */
    public void setBehavior(FruitBehavior behavior) {
        this.behavior = behavior;
    }

    /**
     * Actualiza el estado de la fruta (movimiento, teletransporte, etc.)
     */
    public void update() {
        if (behavior != null && !collected) {
            Position newPosition = behavior.updatePosition(position);
            if (newPosition != null) {
                this.position = newPosition;
            }
        }
    }

    /**
     * Marca la fruta como recolectada
     */
    public void collect() {
        this.collected = true;
    }

    // Getters y Setters
    public Position getPosition() {
        return new Position(position);
    }

    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    public String getFruitType() {
        return fruitType;
    }

    public boolean isCollected() {
        return collected;
    }

    public FruitBehavior getBehavior() {
        return behavior;
    }

    /**
     * Obtiene el valor en puntos de la fruta
     */
    public abstract int getPoints();

    @Override
    public String toString() {
        return fruitType + " at " + position + (collected ? " (collected)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fruit fruit = (Fruit) obj;
        return position.equals(fruit.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
