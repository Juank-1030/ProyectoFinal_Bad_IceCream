package Domain;

/**
 * OrangeSquid (Calamar Naranja) - Enemigo del nivel 3
 * - Persigue al helado
 * - PUEDE romper bloques de hielo (de a uno)
 */
public class OrangeSquid extends Enemy {
    private static final long serialVersionUID = 1L;

    public OrangeSquid(Position position, BoardStateProvider stateProvider) {
        super(position, "Calamar Naranja", 1, true);
        // Establecer comportamiento de persecución (desacoplado con interfaz)
        this.setMovementBehavior(new ChaseMovement(stateProvider));
    }

    @Override
    public void executeAbility() {
        // Habilidad: Romper un bloque de hielo en la dirección actual
        // Esta habilidad se ejecuta automáticamente cuando encuentra un bloque
        breakIceBlock();
    }

    /**
     * Actualiza el proveedor de estado (necesario para persistencia)
     */
    public void updateStateProvider(BoardStateProvider stateProvider) {
        if (this.movementBehavior instanceof ChaseMovement) {
            ((ChaseMovement) this.movementBehavior).setStateProvider(stateProvider);
        }
    }
}
