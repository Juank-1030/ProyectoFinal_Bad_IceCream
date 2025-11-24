package Domain;


/**
 * Clase abstracta que representa un enemigo
 * Los enemigos se mueven según diferentes patrones y pueden tener habilidades especiales
 */
public abstract class Enemy extends GameObject {
    private static final long serialVersionUID = 1L;

    protected String enemyType;  // Tipo de enemigo (Troll, Maceta, Calamar)
    protected MovementBehavior movementBehavior;  // Patrón de movimiento
    protected boolean canBreakIce;  // Si puede romper bloques de hielo

    /**
     * Constructor de Enemy
     * @param position Posición inicial
     * @param enemyType Tipo de enemigo
     * @param speed Velocidad del enemigo
     * @param canBreakIce Si puede romper bloques
     */
    public Enemy(Position position, String enemyType, int speed, boolean canBreakIce) {
        super(position, speed);
        this.enemyType = enemyType;
        this.canBreakIce = canBreakIce;
    }

    /**
     * Establece el comportamiento de movimiento del enemigo
     */
    public void setMovementBehavior(MovementBehavior behavior) {
        this.movementBehavior = behavior;
    }

    /**
     * Calcula el siguiente movimiento según el patrón de comportamiento
     * @return Dirección del siguiente movimiento
     */
    public Direction getNextMove() {
        if (movementBehavior != null) {
            return movementBehavior.getNextMove(this);
        }
        return currentDirection;
    }

    /**
     * Ejecuta la habilidad especial del enemigo
     * Cada enemigo tiene su propia implementación
     */
    public abstract void executeAbility();

    /**
     * Rompe un bloque de hielo en la dirección actual
     * @return La posición del bloque roto, o null si no puede romper
     */
    public Position breakIceBlock() {
        if (!canBreakIce) {
            return null;
        }
        return position.move(currentDirection);
    }

    /**
     * Verifica si puede moverse a una posición
     */
    @Override
    public boolean canMoveTo(Position position) {
        // La validación real se hace en Board
        return true;
    }

    /**
     * Actualiza el estado del enemigo
     */
    @Override
    public void update() {
        // Actualizar patrón de movimiento si es necesario
        if (movementBehavior != null) {
            movementBehavior.update();
        }
    }

    /**
     * Obtiene el tipo de objeto
     */
    @Override
    public String getType() {
        return "Enemy-" + enemyType;
    }

    // Getters
    public String getEnemyType() {
        return enemyType;
    }

    public boolean canBreakIce() {
        return canBreakIce;
    }

    public MovementBehavior getMovementBehavior() {
        return movementBehavior;
    }
}
