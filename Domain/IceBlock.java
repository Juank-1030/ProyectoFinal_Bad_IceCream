package Domain;

import java.io.Serializable;

/**
 * Representa un bloque de hielo que puede ser creado o destruido
 * Los helados pueden crear/romper bloques
 * Algunos enemigos pueden romper bloques
 */
public class IceBlock implements Serializable {
    private static final long serialVersionUID = 1L;

    private Position position;
    private boolean breakable;  // Si puede ser roto
    private GameObject creator; // Quién creó el bloque (si aplica)

    /**
     * Constructor para bloque de hielo
     * @param position Posición del bloque
     * @param breakable Si el bloque puede ser roto
     */
    public IceBlock(Position position, boolean breakable) {
        this.position = new Position(position);
        this.breakable = breakable;
        this.creator = null;
    }

    /**
     * Constructor con creador
     */
    public IceBlock(Position position, boolean breakable, GameObject creator) {
        this.position = new Position(position);
        this.breakable = breakable;
        this.creator = creator;
    }

    public Position getPosition() {
        return new Position(position);
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }

    public GameObject getCreator() {
        return creator;
    }

    public void setCreator(GameObject creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "IceBlock at " + position + (breakable ? " (breakable)" : " (permanent)");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IceBlock iceBlock = (IceBlock) obj;
        return position.equals(iceBlock.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
