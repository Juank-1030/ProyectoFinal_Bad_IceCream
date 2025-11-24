package Domain;

/**
 * Helado de Chocolate - Color café
 */
public class ChocolateIceCream extends IceCream {
    private static final long serialVersionUID = 1L;

    public ChocolateIceCream(Position position) {
        super(position, "Chocolate");
    }

    @Override
    public String getColor() {
        return "BROWN";
    }
}
