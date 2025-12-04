package Domain;

/**
 * Narval - Enemigo especializado en cargas destructivas
 * - Velocidad normal: 2 bloques por segundo (500ms)
 * - Velocidad en carga: 7 bloques por segundo (~143ms)
 * - HABILIDAD: Carga destructiva que rompe todos los bloques en su camino
 * - Se detiene solo al colisionar con algo que NO sea hielo (pared, helado,
 * límite)
 */
public class Narval extends Enemy {
    private static final long serialVersionUID = 1L;

    // Sistema de carga
    private boolean chargeActive; // Si está en modo de carga
    private long chargeStartTime; // Momento en que inició la carga
    private Direction chargeDirection; // Dirección de la carga
    private long chargeRechargeStartTime; // Momento en que empezó la recarga

    // Tiempos en milisegundos
    private static final long CHARGE_RECHARGE = 7000; // 7 segundos entre cargas
    private static final int NORMAL_SPEED = 500; // 500ms = 2 celdas/seg
    private static final int CHARGE_SPEED = 143; // ~143ms = 7 celdas/seg (1000/7)

    /**
     * Constructor de Narval
     * 
     * @param position      Posición inicial
     * @param stateProvider Proveedor de estado del tablero
     */
    public Narval(Position position, BoardStateProvider stateProvider) {
        super(position, "Narval", NORMAL_SPEED, true); // 500ms = 2 celdas/seg en modo normal

        // Inicializar carga
        this.chargeActive = false;
        this.chargeStartTime = 0;
        this.chargeDirection = Direction.DOWN;
        this.chargeRechargeStartTime = -CHARGE_RECHARGE; // Disponible inmediatamente

        // Establecer comportamiento de persecución
        this.setMovementBehavior(new ChaseMovement(stateProvider));
    }

    @Override
    public void executeAbility() {
        // La lógica de carga se maneja en Board.executeNarvalCharge()
    }

    /**
     * Verifica si el Narval puede activar su carga
     */
    public boolean canCharge() {
        long currentTime = System.currentTimeMillis();
        return !chargeActive && currentTime >= chargeRechargeStartTime + CHARGE_RECHARGE;
    }

    /**
     * Verifica si está en modo carga
     */
    public boolean isCharging() {
        return chargeActive;
    }

    /**
     * Activa el modo carga en una dirección
     */
    public void activateCharge(Direction direction) {
        if (canCharge()) {
            chargeActive = true;
            chargeDirection = direction;
            chargeStartTime = System.currentTimeMillis();
            // Cambiar velocidad a la de carga
            this.speed = CHARGE_SPEED;
            this.lastMovementTime = System.currentTimeMillis();
            System.out.println("🔱 Narval: ¡CARGANDO hacia " + direction + " a 7 bloques/seg!");
        }
    }

    /**
     * Desactiva el modo carga e inicia la recarga
     */
    public void deactivateCharge() {
        if (chargeActive) {
            chargeActive = false;
            chargeRechargeStartTime = System.currentTimeMillis();
            // Restaurar velocidad normal
            this.speed = NORMAL_SPEED;
            this.lastMovementTime = System.currentTimeMillis();
            System.out.println("🔱 Narval: Carga completada, en recarga (velocidad normal 2 bloques/seg)...");
        }
    }

    /**
     * Obtiene la dirección actual de carga
     */
    public Direction getChargeDirection() {
        return chargeDirection;
    }

    /**
     * Obtiene el tiempo restante para poder activar otra carga
     */
    public long getChargeRechargeTimeRemaining() {
        if (chargeActive) {
            return 0;
        }
        long remaining = (chargeRechargeStartTime + CHARGE_RECHARGE) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Indica que el Narval puede romper hielo
     */
    @Override
    public boolean canBreakIce() {
        return true; // El Narval siempre puede romper hielo
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
    public void update() {
        super.update();
        // Ejecutar la lógica de carga en cada actualización
        executeAbility();
    }

    @Override
    public String toString() {
        String chargeStatus = chargeActive ? "CARGANDO hacia " + chargeDirection
                : "Recargando (" + (getChargeRechargeTimeRemaining() / 1000) + "s)";
        return super.toString() + " [" + chargeStatus + "]";
    }
}
