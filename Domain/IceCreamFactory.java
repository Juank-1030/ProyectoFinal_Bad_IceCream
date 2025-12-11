package Domain;

/**
 * Factory para crear instancias de IceCream
 * 
 * Ventajas MVC:
 * - Centraliza la creación de helados
 * - Separa lógica de creación de lógica de juego
 * - Fácil de extender con nuevos tipos de helado
 * - Cumple con Factory Pattern
 * 
 * Ejemplo:
 * IceCream helado = IceCreamFactory.create("vainilla", position);
 */
public class IceCreamFactory {

    /**
     * Crea una instancia de IceCream según el sabor
     * 
     * @param flavor   Sabor del helado ("vainilla", "fresa", "chocolate")
     * @param position Posición inicial del helado
     * @return Instancia de IceCream del tipo especificado
     * @throws IllegalArgumentException Si el sabor no es válido
     */
    public static IceCream create(String flavor, Position position) {
        if (flavor == null) {
            throw new IllegalArgumentException("El sabor no puede ser null");
        }

        switch (flavor.toLowerCase().trim()) {
            case "vainilla":
            case "vanilla":
                return new VanillaIceCream(position);

            case "fresa":
            case "strawberry":
                return new StrawberryIceCream(position);

            case "chocolate":
                return new ChocolateIceCream(position);

            default:
                throw new IllegalArgumentException("Sabor de helado no válido: " + flavor);
        }
    }

    /**
     * Valida si un sabor es válido
     * 
     * @param flavor Sabor a validar
     * @return true si el sabor existe, false en caso contrario
     */
    public static boolean isValidFlavor(String flavor) {
        if (flavor == null) {
            return false;
        }

        String normalizedFlavor = flavor.toLowerCase().trim();
        return normalizedFlavor.equals("vainilla") ||
                normalizedFlavor.equals("vanilla") ||
                normalizedFlavor.equals("fresa") ||
                normalizedFlavor.equals("strawberry") ||
                normalizedFlavor.equals("chocolate");
    }

    /**
     * Obtiene los sabores disponibles
     * 
     * @return Array con los nombres de los sabores
     */
    public static String[] getAvailableFlavors() {
        return new String[] { "Vainilla", "Fresa", "Chocolate" };
    }
}
