package Domain;

/**
 * Clase abstracta que representa un helado
 * Los helados pueden moverse, crear y romper bloques de hielo
 */
public abstract class IceCream extends GameObject {
    private static final long serialVersionUID = 1L;

    protected String flavor;  // Sabor del helado (Vainilla, Fresa, Chocolate)
    protected int fruitsCollected;  // Frutas recolectadas
    protected boolean canCreateIce;  // Si puede crear hielo en este momento
    protected boolean canBreakIce;   // Si puede romper hielo en este momento

    /**
     * Constructor de IceCream
     * @param position Posición inicial
     * @param flavor Sabor del helado
     */
    public IceCream(Position position, String flavor) {
        super(position, 1);  // Velocidad estándar de 1
        this.flavor = flavor;
        this.fruitsCollected = 0;
        this.canCreateIce = true;
        this.canBreakIce = true;
    }

    /**
     * Crea un bloque de hielo en la dirección actual
     * @return La posición donde se creó el bloque, o null si no se pudo crear
     */
    public Position createIceBlock() {
        if (!canCreateIce) {
            return null;
        }
        Position icePosition = position.move(currentDirection);
        return icePosition;
    }

    /**
     * Rompe bloques de hielo en la dirección actual (efecto dominó)
     * @return Array de posiciones de bloques rotos
     */
    public Position[] breakIceBlocks() {
        if (!canBreakIce) {
            return new Position[0];
        }
        // La lógica del efecto dominó se maneja en Board
        // Aquí solo retornamos la dirección inicial
        return new Position[] { position.move(currentDirection) };
    }

    /**
     * Recolecta una fruta
     */
    public void collectFruit() {
        fruitsCollected++;
    }

    /**
     * Verifica si puede moverse a una posición
     * Los helados no pueden atravesar paredes ni bloques de hielo
     */
    @Override
    public boolean canMoveTo(Position position) {
        // La validación real se hace en Board
        return true;
    }

    /**
     * Actualiza el estado del helado
     */
    @Override
    public void update() {
        // Actualización por frame (si se necesita animación, etc.)
    }

    /**
     * Obtiene el tipo de objeto
     */
    @Override
    public String getType() {
        return "IceCream-" + flavor;
    }

    // Getters y Setters
    public String getFlavor() {
        return flavor;
    }

    public int getFruitsCollected() {
        return fruitsCollected;
    }

    public void setFruitsCollected(int fruitsCollected) {
        this.fruitsCollected = fruitsCollected;
    }

    public boolean canCreateIce() {
        return canCreateIce;
    }

    public void setCanCreateIce(boolean canCreateIce) {
        this.canCreateIce = canCreateIce;
    }

    public boolean canBreakIce() {
        return canBreakIce;
    }

    public void setCanBreakIce(boolean canBreakIce) {
        this.canBreakIce = canBreakIce;
    }

    /**
     * Obtiene el color asociado al helado (para renderizado)
     */
    public abstract String getColor();
}
