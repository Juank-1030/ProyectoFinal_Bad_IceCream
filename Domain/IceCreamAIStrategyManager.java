package Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de estrategias de IA para el helado
 * Proporciona un registro extensible de estrategias disponibles
 */
public class IceCreamAIStrategyManager {
    private static final Map<String, Class<? extends IceCreamAIStrategy>> strategies = new HashMap<>();

    static {
        // Registrar estrategias por defecto
        registerStrategy("Hungry", HungryAIStrategy.class);
        registerStrategy("Fearful", FearfulAIStrategy.class);
        registerStrategy("Expert", ExpertAIStrategy.class);
    }

    /**
     * Registra una nueva estrategia de IA
     * 
     * @param name          Nombre de la estrategia
     * @param strategyClass Clase que implementa IceCreamAIStrategy
     */
    public static void registerStrategy(String name, Class<? extends IceCreamAIStrategy> strategyClass) {
        strategies.put(name, strategyClass);
    }

    /**
     * Obtiene una instancia de una estrategia por nombre
     * 
     * @param name Nombre de la estrategia
     * @return Instancia de la estrategia, o null si no existe
     */
    public static IceCreamAIStrategy getStrategy(String name) {
        Class<? extends IceCreamAIStrategy> strategyClass = strategies.get(name);
        if (strategyClass == null) {
            return null;
        }

        try {
            return strategyClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene una lista de nombres de todas las estrategias disponibles
     */
    public static String[] getAvailableStrategies() {
        return strategies.keySet().toArray(new String[0]);
    }

    /**
     * Obtiene la cantidad de estrategias disponibles
     */
    public static int getStrategyCount() {
        return strategies.size();
    }
}
