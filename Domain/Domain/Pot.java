package Domain;


/**
 * Pot (Maceta) - Enemigo del nivel 2
 * - Persigue al helado
 * - NO puede romper bloques
 */
public class Pot extends Enemy {
    private static final long serialVersionUID = 1L;

    public Pot(Position position, Board board) {
        super(position, "Maceta", 1, false);
        // Establecer comportamiento de persecución
        this.setMovementBehavior(new ChaseMovement(board));
    }

    @Override
    public void executeAbility() {
        // La Maceta solo persigue, no tiene habilidades especiales adicionales
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
