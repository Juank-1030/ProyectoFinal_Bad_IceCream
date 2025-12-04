package Domain;

import java.io.Serializable;

/**
 * Interfaz que abstrae el acceso al estado del tablero
 * 
 * Problema solucionado:
 * - ChaseMovement necesitaba referencia directa a Board (acoplamiento fuerte)
 * - Esta interfaz permite que MovementBehavior consulte el tablero sin
 * conocerlo
 * 
 * Ventajas MVC:
 * - Domain puede inyectar esta interfaz sin exponer Board
 * - Fácil de testear (mock sin crear Board real)
 * - Permite diferentes implementaciones de consulta de estado
 */
public interface BoardStateProvider extends Serializable {

    /**
     * Obtiene la posición actual del helado
     * 
     * @return Posición del helado o null si no existe
     */
    Position getIceCreamPosition();

    /**
     * Verifica si una posición es válida para moverse
     * 
     * @param position Posición a verificar
     * @return true si se puede mover a esa posición
     */
    boolean isValidPosition(Position position);

    /**
     * Obtiene el helado del tablero
     * 
     * @return IceCream o null si no existe
     */
    IceCream getIceCream();
}
