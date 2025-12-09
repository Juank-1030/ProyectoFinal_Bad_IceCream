import Controller.GameController;
import Domain.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test de integración para verificar:
 * 1. Creación de frutas personalizadas
 * 2. Renderizado de frutas
 * 3. Flujo PVP Vs Monstruo
 */
public class TestFlowIntegration {
    private static int testCount = 0;
    private static int passCount = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        TEST DE INTEGRACIÓN - FLUJO COMPLETO DEL JUEGO          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            // Test 1: Crear frutas personalizadas
            testFruitCustomConfig();

            // Test 2: Verificar nombres de frutas
            testFruitNaming();

            // Test 3: Crear juego PVP con frutas personalizadas
            testPVPWithCustomFruits();

            // Test 4: Crear juego PVM con frutas personalizadas
            testPVMWithCustomFruits();

            printSummary();
        } catch (Exception e) {
            System.err.println("\n❌ ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testFruitCustomConfig() {
        System.out.println("\n>>> TEST 1: Crear Frutas Personalizadas <<<\n");

        try {
            // Crear configuración personalizada de frutas
            Map<String, Integer> fruitConfig = new HashMap<>();
            fruitConfig.put("Uvas", 5);
            fruitConfig.put("Plátanos", 3);
            fruitConfig.put("Cerezas", 4);
            fruitConfig.put("Piñas", 2);

            System.out.println("Configuración de frutas a crear:");
            for (String fruit : fruitConfig.keySet()) {
                System.out.println("  - " + fruit + ": " + fruitConfig.get(fruit));
            }

            // Crear GameController con modo PVM y frutas personalizadas
            GameController controller = new GameController(GameMode.PVM, "CHOCOLATE", null, null, null, fruitConfig);
            controller.startLevel(1);

            // Obtener el juego
            java.lang.reflect.Field gameField = GameController.class.getDeclaredField("game");
            gameField.setAccessible(true);
            Game game = (Game) gameField.get(controller);

            // Verificar frutas
            Board board = game.getBoard();
            List<Fruit> fruits = board.getFruits();

            System.out.println("\n✅ Frutas creadas en el tablero:");
            int totalFruits = 0;
            for (Fruit fruit : fruits) {
                if (!fruit.isCollected()) {
                    System.out.println("  - Tipo: " + fruit.getFruitType());
                    totalFruits++;
                }
            }

            System.out.println("\n📊 Resumen:");
            System.out.println(
                    "  Total de frutas esperadas: " + fruitConfig.values().stream().mapToInt(Integer::intValue).sum());
            System.out.println("  Total de frutas creadas: " + totalFruits);

            if (totalFruits >= 10) {
                System.out.println("✅ TEST PASADO: Frutas creadas correctamente");
                passCount++;
            } else {
                System.out.println("❌ TEST FALLIDO: No se crearon suficientes frutas");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST FALLIDO: " + e.getMessage());
            e.printStackTrace();
        }
        testCount++;
    }

    private static void testFruitNaming() {
        System.out.println("\n>>> TEST 2: Verificar Nombres de Frutas <<<\n");

        try {
            System.out.println("Verificando que los nombres de frutas sean compatibles:");

            // Crear una posición arbitraria
            Position pos = new Position(1, 1);

            // Probar crear cada tipo de fruta
            Fruit grape = new Grape(pos);
            Fruit banana = new Banana(pos);
            Fruit cherry = new Cherry(pos, null);
            Fruit pineapple = new Pineapple(pos, null);

            System.out.println("  - Grape type: " + grape.getFruitType());
            System.out.println("  - Banana type: " + banana.getFruitType());
            System.out.println("  - Cherry type: " + cherry.getFruitType());
            System.out.println("  - Pineapple type: " + pineapple.getFruitType());

            // Verificar que los nombres sean válidos
            String grapeType = grape.getFruitType().toLowerCase();
            String bananaType = banana.getFruitType().toLowerCase();
            String cherryType = cherry.getFruitType().toLowerCase();
            String pineappleType = pineapple.getFruitType().toLowerCase();

            boolean grapeValid = grapeType.contains("uva") || grapeType.contains("grape");
            boolean bananaValid = bananaType.contains("plátano") || bananaType.contains("platano")
                    || bananaType.contains("banana");
            boolean cherryValid = cherryType.contains("cereza") || cherryType.contains("cherry");
            boolean pineappleValid = pineappleType.contains("piña") || pineappleType.contains("pina")
                    || pineappleType.contains("pineapple");

            System.out.println("\n✅ Validación de nombres:");
            System.out.println("  - Grape válido: " + (grapeValid ? "✅" : "❌"));
            System.out.println("  - Banana válido: " + (bananaValid ? "✅" : "❌"));
            System.out.println("  - Cherry válido: " + (cherryValid ? "✅" : "❌"));
            System.out.println("  - Pineapple válido: " + (pineappleValid ? "✅" : "❌"));

            if (grapeValid && bananaValid && cherryValid && pineappleValid) {
                System.out.println("✅ TEST PASADO: Todos los nombres de frutas son válidos");
                passCount++;
            } else {
                System.out.println("❌ TEST FALLIDO: Algunos nombres de frutas no son válidos");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST FALLIDO: " + e.getMessage());
            e.printStackTrace();
        }
        testCount++;
    }

    private static void testPVPWithCustomFruits() {
        System.out.println("\n>>> TEST 3: PVP Vs Monstruo con Frutas Personalizadas <<<\n");

        try {
            // Crear configuración personalizada
            Map<String, Integer> fruitConfig = new HashMap<>();
            fruitConfig.put("Cerezas", 10);

            Map<String, Integer> enemyConfig = new HashMap<>();
            enemyConfig.put("Narval", 1);

            System.out.println("Configuración:");
            System.out.println("  Modo: PVP vs Monstruo");
            System.out.println("  Helado: Chocolate");
            System.out.println("  Monstruo: Narval");
            System.out.println("  Cerezas: 10");
            System.out.println("  Enemigos adicionales - Narval: 1");

            // Crear GameController PVP
            GameController controller = new GameController(GameMode.PVP, "CHOCOLATE", null, "Narval", enemyConfig,
                    fruitConfig);
            controller.startLevel(1);

            System.out.println("✅ GameController creado exitosamente");
            System.out.println("✅ Nivel iniciado correctamente");
            System.out.println("✅ TEST PASADO: PVP Vs Monstruo funciona con frutas personalizadas");
            passCount++;
        } catch (Exception e) {
            System.err.println("❌ TEST FALLIDO: " + e.getMessage());
            e.printStackTrace();
        }
        testCount++;
    }

    private static void testPVMWithCustomFruits() {
        System.out.println("\n>>> TEST 4: PVM con Frutas Personalizadas <<<\n");

        try {
            // Crear configuración personalizada
            Map<String, Integer> fruitConfig = new HashMap<>();
            fruitConfig.put("Uvas", 8);
            fruitConfig.put("Piñas", 6);

            Map<String, Integer> enemyConfig = new HashMap<>();
            enemyConfig.put("Troll", 2);
            enemyConfig.put("Pot", 1);

            System.out.println("Configuración:");
            System.out.println("  Modo: PVM");
            System.out.println("  Helado: Fresa");
            System.out.println("  Uvas: 8");
            System.out.println("  Piñas: 6");
            System.out.println("  Enemigos adicionales - Troll: 2, Pot: 1");

            // Crear GameController PVM
            GameController controller = new GameController(GameMode.PVM, "FRESA", null, null, enemyConfig, fruitConfig);
            controller.startLevel(1);

            System.out.println("✅ GameController creado exitosamente");
            System.out.println("✅ Nivel iniciado correctamente");
            System.out.println("✅ TEST PASADO: PVM funciona con frutas personalizadas");
            passCount++;
        } catch (Exception e) {
            System.err.println("❌ TEST FALLIDO: " + e.getMessage());
            e.printStackTrace();
        }
        testCount++;
    }

    private static void printSummary() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      RESUMEN DE PRUEBAS                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Tests ejecutados: " + testCount);
        System.out.println("║  Tests pasados:    " + passCount);
        System.out.println("║  Tests fallidos:   " + (testCount - passCount));
        System.out.println("║  Tasa de éxito:    " + String.format("%.1f%%", (passCount * 100.0 / testCount)));
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        if (passCount == testCount) {
            System.out.println("\n✅ ¡TODOS LOS TESTS PASARON!");
            System.exit(0);
        } else {
            System.out.println("\n❌ ALGUNOS TESTS FALLARON");
            System.exit(1);
        }
    }
}
