package Domain;


/**
 * OrangeSquid (Calamar Naranja) - Enemigo del nivel 3
 * - Persigue al helado
 * - PUEDE romper bloques de hielo (de a uno)
 */
public class OrangeSquid extends Enemy {
    private static final long serialVersionUID = 1L;

    public OrangeSquid(Position position, Board board) {
        super(position, "Calamar Naranja", 1, true);
        // Establecer comportamiento de persecución
        this.setMovementBehavior(new ChaseMovement(board));
    }

    @Override
    public void executeAbility() {
        // Habilidad: Romper un bloque de hielo en la dirección actual
        // Esta habilidad se ejecuta automáticamente cuando encuentra un bloque
        breakIceBlock();
    }

    /**
     * Actualiza la referencia al tablero (necesario para persistencia)
     */
    public void updateBoardReference(Board board) {
        if (this.movementBehavior instanceof ChaseMovement) {
            ((ChaseMovement) this.movementBehavior).setBoard(board);
        }
    }
}
