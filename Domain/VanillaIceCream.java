package Domain;

/**
 * Helado de Vainilla - Color blanco
 */
public class VanillaIceCream extends IceCream {
    private static final long serialVersionUID = 1L;

    public VanillaIceCream(Position position) {
        super(position, "Vainilla");
    }

    @Override
    public String getColor() {
        return "WHITE";
    }
}
