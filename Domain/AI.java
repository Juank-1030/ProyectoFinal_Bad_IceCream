package Domain;


/**
 * Interface para la inteligencia artificial que controla entidades
 * Usado en modos PVM (enemigos) y MVM (helado y enemigos)
 */
public interface AI {
    
    /**
     * Calcula el siguiente movimiento de la entidad
     * @return La dirección del siguiente movimiento
     */
    Direction getNextMove();

    /**
     * Actualiza el estado interno de la IA
     */
    void update();

    /**
     * Reinicia la IA a su estado inicial
     */
    void reset();
}
