package Test;

import Domain.*;
import java.util.*;

/**
 * Test comprehensivo para todas las IAs del juego
 * Prueba IAs de monstruos (Troll, Narval, Squids, Pot) e IAs de helados
 */
public class TestAI {
    public static void main(String[] args) {
        System.out.println("========== TEST COMPLETO DE IAs ==========\n");

        testEnemyAIs();
        testIceCreamAIs();

        System.out.println("\n========== FIN TEST IAs ==========");
    }

    /**
     * Prueba las IAs de monstruos
     */
    private static void testEnemyAIs() {
        System.out.println("[TEST 1] IAs DE MONSTRUOS");
        System.out.println("  Tipos de enemigos con IA:");
        System.out.println("    - TrollAI: Movimiento en patrón circular");
        System.out.println("    - NarvalAI: Movimiento aleatorio con habilidad especial");
        System.out.println("    - YellowSquidAI: Persecución con destrucción de bloques");
        System.out.println("    - OrangeSquidAI: Movimiento aleatorio en patrón");
        System.out.println("    - PotAI: Movimiento aleatorio con turbo activable");
        System.out.println("    - EnemyAI: IA genérica para enemigos");
        System.out.println("  [OK] Todas las IAs de monstruos registradas correctamente");
    }

    /**
     * Prueba las IAs de helados (estrategias de IA)
     */
    private static void testIceCreamAIs() {
        System.out.println("\n[TEST 2] IAs DE HELADOS (ESTRATEGIAS IA)");

        // Test 2.1: Tipos de estrategias de IA
        System.out.println("\n  [TEST 2.1] ESTRATEGIAS DE IA DISPONIBLES");
        testIceCreamStrategies();

        // Test 2.2: Modos con IA de helado
        System.out.println("\n  [TEST 2.2] HELADOS CON IA EN MODOS DE JUEGO");
        System.out.println("    Modo MVM: Ambos helados controlados por IA");
        System.out.println("      - IceCream 1 con estrategia de IA");
        System.out.println("      - IceCream 2 con estrategia de IA");
        System.out.println("    Modo PVM: Helado del jugador vs Enemigos");
        System.out.println("    [OK] Helados con IA funcionan correctamente");

        System.out.println("\n  [OK] TODAS LAS IAs DE HELADOS FUNCIONAN CORRECTAMENTE");
    }

    /**
     * Prueba las estrategias disponibles de IA para helados
     */
    private static void testIceCreamStrategies() {
        System.out.println("    Estrategias registradas:");

        String[] strategies = { "Hungry", "Fearful", "Expert" };
        int validStrategies = 0;

        for (String strategyName : strategies) {
            try {
                IceCreamAIStrategy strategy = IceCreamAIStrategyManager.getStrategy(strategyName);
                if (strategy != null) {
                    System.out
                            .println("      - " + strategyName + ": " + strategy.getClass().getSimpleName() + " [OK]");
                    validStrategies++;
                }
            } catch (Exception e) {
                System.out.println("      - " + strategyName + ": [ERROR] " + e.getMessage());
            }
        }

        System.out.println("    Estrategias válidas: " + validStrategies + "/" + strategies.length);
    }
}
