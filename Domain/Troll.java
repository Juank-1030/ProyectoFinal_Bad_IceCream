package Domain;

/**
 * Troll - Enemigo del nivel 1
 * - Se mueve en un patrón específico
 * - NO persigue al helado
 * - NO puede romper bloques
 * - Velocidad: 1 celda por segundo (1000ms)
 */
public class Troll extends Enemy {
    private static final long serialVersionUID = 1L;

    public Troll(Position position) {
        super(position, "Troll", 1000, false); // 1000ms = 1 celda/seg
        // Establecer patrón de movimiento por defecto
        this.setMovementBehavior(new PatternMovement());
    }

    /**
     * Constructor con patrón personalizado
     */
    public Troll(Position position, Direction[] pattern, int stepsPerDirection) {
        super(position, "Troll", 1000, false); // 1000ms = 1 celda/seg
        this.setMovementBehavior(new PatternMovement(pattern, stepsPerDirection));
    }

    @Override
    public void executeAbility() {
        // El Troll no tiene habilidades especiales
        // Solo sigue su patrón de movimiento
    }
}
