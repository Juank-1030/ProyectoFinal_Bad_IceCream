package Domain;

public class BaldosaCaliente extends GameObject {
    public BaldosaCaliente(Position position) {
        super(position, 0);
    }

    @Override
    public boolean canMoveTo(Position position) {
        return false;
    }

    @Override
    public String getType() {
        return "baldosa_caliente";
    }

    @Override
    public void update() {
        // No necesita lógica de actualización
    }
}