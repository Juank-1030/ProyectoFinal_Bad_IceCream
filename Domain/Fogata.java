package Domain;

public class Fogata extends GameObject {
    private static final long serialVersionUID = 1L;

    private boolean encendida = true;
    private long tiempoUltimaApagada = 0;
    private static final long DURACION_APAGADA = 10000L; // 10 segundos

    public Fogata(Position position) {
        super(position, 0);
    }

    public void apagar() {
        if (encendida) {
            encendida = false;
            tiempoUltimaApagada = System.currentTimeMillis();
        }
    }

    @Override
    public void update() {
        if (!encendida) {
            long now = System.currentTimeMillis();
            if (now - tiempoUltimaApagada >= DURACION_APAGADA) {
                encendida = true;
            }
        }
    }

    public boolean isEncendida() {
        return encendida;
    }

    @Override public boolean canMoveTo(Position position) { return false; }
    @Override public String getType() { return "fogata"; }
}