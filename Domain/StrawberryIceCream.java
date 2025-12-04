package Domain;

/**
 * Helado de Fresa - Color rosado
 */
public class StrawberryIceCream extends IceCream {
    private static final long serialVersionUID = 1L;

    public StrawberryIceCream(Position position) {
        super(position, "Fresa");
    }

    @Override
    public String getColor() {
        return "PINK";
    }
}
