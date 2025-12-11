package Domain;

import java.io.Serializable;

/**
 * Clase abstracta que representa una fruta en el juego
 * Las frutas tienen diferentes comportamientos (estáticas, móviles,
 * teletransporte)
 */
public abstract class Fruit implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Position position;
    protected String fruitType;
    protected boolean collected;
    protected FruitBehavior behavior;

    // NUEVO: Variables para animación de aparición
    protected transient boolean appearing; // Si está en animación de aparecer
    protected transient long appearStartTime; // Momento en que empezó a aparecer
    protected static final long APPEAR_DURATION = 600; // 600ms de animación (0.6 segundos)

    /**
     * Constructor de Fruit
     * 
     * @param position  Posición inicial
     * @param fruitType Tipo de fruta
     */
    public Fruit(Position position, String fruitType) {
        this.position = new Position(position);
        this.fruitType = fruitType;
        this.collected = false;

        // NUEVO: Iniciar animación de aparición
        this.appearing = true;
        this.appearStartTime = System.currentTimeMillis();
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
        // NUEVO: Actualizar estado de aparición
        if (appearing) {
            checkAppearanceFinished();
        }

        // Comportamiento normal (movimiento, etc.)
        if (behavior != null && !collected) {
            Position newPosition = behavior.updatePosition(position);
            if (newPosition != null) {
                this.position = newPosition;
            }
        }
    }

    /**
     * NUEVO: Verifica si terminó la animación de aparición
     */
    private void checkAppearanceFinished() {
        long elapsed = System.currentTimeMillis() - appearStartTime;
        if (elapsed >= APPEAR_DURATION) {
            appearing = false;
        }
    }

    /**
     * NUEVO: Verifica si está en animación de aparición
     */
    public boolean isAppearing() {
        if (!appearing) {
            return false;
        }

        checkAppearanceFinished();
        return appearing;
    }

    /**
     * NUEVO: Obtiene el estado visual actual de la fruta
     * Retorna: "appear", "normal", o "collected"
     */
    public String getVisualState() {
        if (collected) {
            return "collected";
        }
        if (isAppearing()) {
            return "appear";
        }
        return "normal";
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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Fruit fruit = (Fruit) obj;
        return position.equals(fruit.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}