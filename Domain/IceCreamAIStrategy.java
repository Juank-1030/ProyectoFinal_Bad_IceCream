package Domain;

/**
 * Interfaz para definir estrategias de IA para el helado
 * Extensible: permite agregar nuevas estrategias sin modificar código existente
 */
public interface IceCreamAIStrategy {
    /**
     * Calcula el siguiente movimiento para el helado
     * 
     * @param board    Estado actual del tablero
     * @param iceCream Posición actual del helado
     * @return Dirección a seguir (puede ser null para quedarse quieto)
     */
    Direction getNextMove(Board board, IceCream iceCream);

    /**
     * Obtiene el nombre de la estrategia
     */
    String getName();
}
