package Domain;

/**
 * IceBlockObstacle - Bloque de hielo estático como obstáculo
 * Funciona similar a BaldosaCaliente - es un obstáculo que los jugadores no
 * pueden atravesar
 */
public class IceBlockObstacle extends GameObject {
    private static final long serialVersionUID = 1L;

    public IceBlockObstacle(Position position) {
        super(position, 0);
    }

    @Override
    public boolean canMoveTo(Position position) {
        return false;
    }

    @Override
    public String getType() {
        return "bloque_hielo";
    }

    @Override
    public void update() {
        // No necesita lógica de actualización
    }
}
