package Domain;

import java.io.Serializable;

/**
 * Interface para los diferentes comportamientos de las frutas
 * Patrón Strategy para encapsular algoritmos de comportamiento
 */
public interface FruitBehavior extends Serializable {
    
    /**
     * Actualiza la posición de la fruta según su comportamiento
     * @param currentPosition Posición actual de la fruta
     * @return Nueva posición, o null si no cambia
     */
    Position updatePosition(Position currentPosition);

    /**
     * Reinicia el comportamiento a su estado inicial
     */
    void reset();
}
