package Domain;

/**
 * Grape (Uvas) - Fruta estática más común
 * No se mueve
 */
public class Grape extends Fruit {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 50;

    public Grape(Position position) {
        super(position, "Uvas");
        this.setBehavior(new StaticFruitBehavior());
    }

    @Override
    public int getPoints() {
        return POINTS;
    }
}
