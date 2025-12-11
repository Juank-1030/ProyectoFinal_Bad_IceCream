package Domain;

/**
 * Baldosa Caliente - Obstáculo que no puede ser atravesado.
 * El hielo se derrite al estar sobre ella.
 */
public class BaldosaCaliente extends GameObject {
    private static final long serialVersionUID = 1L;

    public BaldosaCaliente(Position position) {
        super(position, 0);
    }

    @Override
    public void update() {
        // No tiene lógica de actualización
    }

    @Override
    public boolean canMoveTo(Position position) {
        return false; // No se puede pasar
    }

    @Override
    public String getType() {
        return "baldosaCaliente";
    }
}
