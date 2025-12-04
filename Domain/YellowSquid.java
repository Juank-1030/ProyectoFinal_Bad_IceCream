package Domain;

/**
 * YellowSquid (Calamar Amarillo) - Enemigo especializado en romper hielo
 * - Velocidad: 1 bloque por segundo (1000ms)
 * - PUEDE romper bloques de hielo pero requiere 3 interacciones ESPACIO
 * - Persigue al helado usando ChaseMovement
 */
public class YellowSquid extends Enemy {
    private static final long serialVersionUID = 1L;

    // Sistema de conteo para la habilidad de romper hielo
    private int breakInteractionCounter; // Cuenta las veces que se presiona ESPACIO
    private static final int BREAK_INTERACTIONS_NEEDED = 3; // 3 pulsaciones ESPACIO para romper

    /**
     * Constructor de YellowSquid
     * Velocidad: 1000ms por celda = 1 celda por segundo
     * 
     * @param position      Posición inicial
     * @param stateProvider Proveedor de estado del tablero
     */
    public YellowSquid(Position position, BoardStateProvider stateProvider) {
        super(position, "Calamar Amarillo", 1000, true); // 1000ms = 1 celda/seg
        this.breakInteractionCounter = 0;

        // Establecer comportamiento de persecución (desacoplado con interfaz)
        this.setMovementBehavior(new ChaseMovement(stateProvider));
    }

    @Override
    public void executeAbility() {
        // Esta función se llama cuando se presiona ESPACIO en PVP
        // Incrementar contador de interacción
        breakInteractionCounter++;

        if (breakInteractionCounter >= BREAK_INTERACTIONS_NEEDED) {
            // Romper un bloque
            breakIceBlock();
            System.out.println("🟡 Calamar: Bloque de hielo roto!");
            // Resetear el contador
            breakInteractionCounter = 0;
        } else {
            System.out.println("🟡 Calamar: " + breakInteractionCounter + "/" + BREAK_INTERACTIONS_NEEDED);
        }
    }

    public int getIceBreakCounter() {
        return breakInteractionCounter;
    }

    public void resetIceBreakCounter() {
        breakInteractionCounter = 0;
    }

    /**
     * Actualiza el proveedor de estado (necesario para persistencia)
     */
    public void updateStateProvider(BoardStateProvider stateProvider) {
        if (this.movementBehavior instanceof ChaseMovement) {
            ((ChaseMovement) this.movementBehavior).setStateProvider(stateProvider);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [BreakInteractions: " + breakInteractionCounter + "/" + BREAK_INTERACTIONS_NEEDED
                + "]";
    }
}
