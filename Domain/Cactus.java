package Domain;

/**
 * Cactus - Fruta especial que alterna entre estado normal y con púas
 * Solo puede recolectarse si NO tiene púas. Cada ciclo de púas es de 30
 * segundos.
 */
public class Cactus extends Fruit {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 250;
    private static final long SPIKY_INTERVAL = 30000L; // 30 segundos en ms

    private boolean spiky;
    private long lastStateChange;

    public Cactus(Position position) {
        super(position, "Cactus");
        this.setBehavior(new StaticFruitBehavior());
        this.spiky = false;
        this.lastStateChange = System.currentTimeMillis();
    }

    @Override
    public void update() {
        super.update();
        if (collected)
            return;
        long now = System.currentTimeMillis();
        if (now - lastStateChange >= SPIKY_INTERVAL) {
            spiky = !spiky;
            lastStateChange = now;
        }
    }

    @Override
    public void collect() {
        if (!spiky && !collected) {
            this.collected = true;
        }
    }

    @Override
    public int getPoints() {
        return (collected && !spiky) ? POINTS : 0;
    }

    @Override
    public String getVisualState() {
        if (collected)
            return "collected";
        if (isAppearing())
            return "appear";
        return spiky ? "spiky" : "normal";
    }

    public boolean isSpiky() {
        return spiky;
    }

    public void resetTimerAndSpiky() {
        this.spiky = false;
        this.lastStateChange = System.currentTimeMillis();
        this.collected = false;
    }
}
