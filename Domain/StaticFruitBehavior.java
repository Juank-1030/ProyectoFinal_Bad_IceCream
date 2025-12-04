package Domain;


/**
 * Comportamiento para frutas estáticas que no se mueven
 * Usado por Uvas y Plátano
 */
public class StaticFruitBehavior implements FruitBehavior {
    private static final long serialVersionUID = 1L;

    @Override
    public Position updatePosition(Position currentPosition) {
        // Las frutas estáticas no cambian de posición
        return null;
    }

    @Override
    public void reset() {
        // No hay estado que reiniciar
    }
}
