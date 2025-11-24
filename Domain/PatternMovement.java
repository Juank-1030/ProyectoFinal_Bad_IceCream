package Domain;


/**
 * Comportamiento de movimiento en patrón fijo
 * El enemigo se mueve siguiendo un patrón predefinido (ej: arriba, derecha, abajo, izquierda)
 * Usado por el Troll
 */
public class PatternMovement implements MovementBehavior {
    private static final long serialVersionUID = 1L;

    private Direction[] pattern;  // Patrón de movimiento
    private int currentStep;      // Paso actual en el patrón
    private int stepsPerDirection; // Pasos a dar en cada dirección

    /**
     * Constructor con patrón personalizado
     * @param pattern Array de direcciones del patrón
     * @param stepsPerDirection Número de pasos en cada dirección antes de cambiar
     */
    public PatternMovement(Direction[] pattern, int stepsPerDirection) {
        this.pattern = pattern;
        this.stepsPerDirection = stepsPerDirection;
        this.currentStep = 0;
    }

    /**
     * Constructor con patrón por defecto (cuadrado: UP, RIGHT, DOWN, LEFT)
     */
    public PatternMovement() {
        this(new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT}, 3);
    }

    @Override
    public Direction getNextMove(Enemy enemy) {
        // Obtener la dirección actual del patrón
        int patternIndex = (currentStep / stepsPerDirection) % pattern.length;
        return pattern[patternIndex];
    }

    @Override
    public void update() {
        currentStep++;
    }

    @Override
    public void reset() {
        currentStep = 0;
    }

    // Getters y Setters
    public Direction[] getPattern() {
        return pattern;
    }

    public void setPattern(Direction[] pattern) {
        this.pattern = pattern;
        reset();
    }

    public int getStepsPerDirection() {
        return stepsPerDirection;
    }

    public void setStepsPerDirection(int stepsPerDirection) {
        this.stepsPerDirection = stepsPerDirection;
    }
}
