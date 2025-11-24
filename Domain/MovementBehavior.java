package Domain;

import java.io.Serializable;

/**
 * Interface para los diferentes comportamientos de movimiento de los enemigos
 * Patrón Strategy para encapsular algoritmos de movimiento
 */
public interface MovementBehavior extends Serializable {
    
    /**
     * Calcula la siguiente dirección de movimiento
     * @param enemy El enemigo que se está moviendo
     * @return La dirección del siguiente movimiento
     */
    Direction getNextMove(Enemy enemy);

    /**
     * Actualiza el estado interno del comportamiento
     */
    void update();

    /**
     * Reinicia el comportamiento a su estado inicial
     */
    void reset();
}
