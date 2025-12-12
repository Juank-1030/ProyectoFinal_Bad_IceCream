package Domain;

/**
 * Pot (Maceta) - Enemigo especializado en velocidad
 * - Velocidad normal: 2 bloques por segundo (500ms)
 * - Persigue al helado
 * - NO puede romper bloques
 * - HABILIDAD ESPECIAL: Modo turbo
 * - Modo turbo: corre a 4 bloques/seg (velocidad helado) durante 6 segundos
 * - Recarga: 6 segundos después de terminar el turbo
 */
public class Pot extends Enemy {
    private static final long serialVersionUID = 1L;

    // Sistema de turbo
    private boolean turboActive; // Si el turbo está activo
    private long turboStartTime; // Momento en que inició el turbo
    private long turboRechargeStartTime; // Momento en que empezó la recarga

    // Tiempos en milisegundos
    private static final long TURBO_DURATION = 6000; // 6 segundos activo
    private static final long TURBO_RECHARGE = 6000; // 6 segundos recarga
    private static final int NORMAL_SPEED = 500; // 500ms = 2 celdas/seg
    private static final int TURBO_SPEED = 250; // 250ms = 4 celdas/seg (igual al helado)

    public Pot(Position position, BoardStateProvider stateProvider) {
        super(position, "Maceta", NORMAL_SPEED, false); // 500ms = 2 celdas/seg
        this.turboActive = false;
        this.turboStartTime = 0;
        this.turboRechargeStartTime = -TURBO_RECHARGE; // Disponible inmediatamente

        // Establecer comportamiento de persecución
        this.setMovementBehavior(new ChaseMovement(stateProvider));
    }

    @Override
    public void executeAbility() {
        long currentTime = System.currentTimeMillis();

        // Verificar si el turbo está activo
        if (turboActive && currentTime >= turboStartTime + TURBO_DURATION) {
            // El turbo terminó, iniciar recarga
            turboActive = false;
            turboRechargeStartTime = currentTime;
            this.speed = NORMAL_SPEED; // Volver a velocidad normal
            System.out.println("⏳ Pot: Turbo finalizado, en recarga...");
        }

        // Verificar si la recarga terminó y puede activar turbo
        if (!turboActive && currentTime >= turboRechargeStartTime + TURBO_RECHARGE) {
            turboActive = true;
            turboStartTime = currentTime;
            this.speed = TURBO_SPEED; // Cambiar a velocidad turbo
            System.out.println("[INFO] Pot: TURBO ACTIVADO!");
        }
    }

    /**
     * Verifica si el turbo está activo
     */
    public boolean isTurboActive() {
        return turboActive;
    }

    /**
     * Obtiene el tiempo restante del turbo actual
     */
    public long getTurboTimeRemaining() {
        if (!turboActive) {
            return 0;
        }
        long remaining = turboStartTime + TURBO_DURATION - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Obtiene el tiempo restante de recarga
     */
    public long getTurboRechargeTimeRemaining() {
        if (turboActive) {
            return 0;
        }
        long remaining = (turboRechargeStartTime + TURBO_RECHARGE) - System.currentTimeMillis();
        return Math.max(0, remaining);
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
        // Ejecutar la lógica de turbo en cada actualización
        executeAbility();
    }

    @Override
    public String toString() {
        String turboStatus = turboActive ? "TURBO ACTIVO (" + (getTurboTimeRemaining() / 1000) + "s)"
                : "Recargando (" + (getTurboRechargeTimeRemaining() / 1000) + "s)";
        return super.toString() + " [" + turboStatus + "]";
    }
}
