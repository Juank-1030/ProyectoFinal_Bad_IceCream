package Domain;


/**
 * Banana (Plátano) - Segunda fruta más común
 * No se mueve
 */
public class Banana extends Fruit {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 15;

    public Banana(Position position) {
        super(position, "Plátano");
        this.setBehavior(new StaticFruitBehavior());
    }

    @Override
    public int getPoints() {
        return POINTS;
    }
}
