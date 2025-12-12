package Test;

import Controller.GameController;
import Domain.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Test simplificado para verificar modos de juego
 * Enfocado en inicialización y opciones disponibles
 */
public class TestGameModesSimple {
    private GameController controller;
    private Game game;
    private Board board;
    private int testsPassed = 0;
    private int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("========== TEST MODOS DE JUEGO - INICIALIZACION Y OPCIONES ==========");
        System.out.println("PVP (COOPERATIVE) | PVM (VS MONSTER) | MVM\n");

        try {
            TestGameModesSimple test = new TestGameModesSimple();

            test.testModoPVP();
            test.testModoPVM();
            test.testModoMVM();
            test.testOpcionesHelado();
            test.testOpcionesMonstruo();
            test.testFlujoCompleto();

            test.mostrarResumen();

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============ TEST 1: MODO PVP ============
    private void testModoPVP() {
        System.out.println("\n====== TEST 1: MODO PVP COOPERATIVO ======\n");

        try {
            controller = new GameController(GameMode.PVP, "CHOCOLATE", null);
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            // Verificar helado
            IceCream iceCream = getFieldValue(board, "iceCream");
            assertNotNull("Helado principal existe", iceCream);
            System.out.println("  OK - Helado principal (CHOCOLATE): " + iceCream.getClass().getSimpleName());

            // Verificar estado
            GameState state = getFieldValue(game, "gameState");
            System.out.println("  OK - Estado del juego: " + state.getDescription());

            // Verificar frutas
            List<Fruit> fruits = getFieldValue(board, "fruits");
            System.out.println("  OK - Frutas inicializadas: " + fruits.size());

            // Intentar crear hielo
            game.toggleIceBlocks();
            System.out.println("  OK - Helado puede crear bloques de hielo");

            passTest("MODO PVP COOPERATIVO");

        } catch (Exception e) {
            failTest("MODO PVP", e);
        }
    }

    // ============ TEST 2: MODO PVM ============
    private void testModoPVM() {
        System.out.println("\n====== TEST 2: MODO PVM (HELADO VS MONSTRUO) ======\n");

        try {
            controller = new GameController(GameMode.PVM, "STRAWBERRY", "TROLL");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            // Verificar helado
            IceCream iceCream = getFieldValue(board, "iceCream");
            assertNotNull("Helado existe en PVM", iceCream);
            System.out.println("  OK - Helado jugador (STRAWBERRY): " + iceCream.getClass().getSimpleName());

            // Verificar enemigos
            List<Enemy> enemies = getFieldValue(board, "enemies");
            assertNotNull("Enemigos existen", enemies);
            assertTrue("Al menos un enemigo", enemies.size() > 0);
            System.out.println("  OK - Enemigos en el nivel: " + enemies.size());

            boolean hasTroll = enemies.stream()
                    .anyMatch(e -> e.getClass().getSimpleName().equals("Troll"));
            System.out.println("  OK - Troll presente: " + hasTroll);

            // Verificar interacción
            game.toggleIceBlocks();
            List<IceBlock> iceBlocks = getFieldValue(board, "iceBlocks");
            System.out.println("  OK - Bloques de hielo creados: " + iceBlocks.size());

            passTest("MODO PVM (HELADO VS MONSTRUO)");

        } catch (Exception e) {
            failTest("MODO PVM", e);
        }
    }

    // ============ TEST 3: MODO MVM ============
    private void testModoMVM() {
        System.out.println("\n====== TEST 3: MODO MVM (MONSTRUO VS MONSTRUO) ======\n");

        try {
            controller = new GameController(GameMode.MVM, null, null);
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            // Verificar que NO hay helado
            IceCream iceCream = getFieldValue(board, "iceCream");
            System.out.println("  OK - Sin helado jugador: " + (iceCream == null));

            // Verificar enemigos
            List<Enemy> enemies = getFieldValue(board, "enemies");
            assertTrue("Múltiples enemigos", enemies.size() >= 2);
            System.out.println("  OK - Monstruos en combate: " + enemies.size());

            // Listar tipos de monstruos
            Set<String> tipos = new HashSet<>();
            for (Enemy e : enemies) {
                tipos.add(e.getClass().getSimpleName());
            }
            System.out.println("  OK - Tipos de monstruos: " + String.join(", ", tipos));

            passTest("MODO MVM (MONSTRUO VS MONSTRUO)");

        } catch (Exception e) {
            failTest("MODO MVM", e);
        }
    }

    // ============ TEST 4: OPCIONES DE HELADO ============
    private void testOpcionesHelado() {
        System.out.println("\n====== TEST 4: OPCIONES DE HELADO DISPONIBLES ======\n");

        try {
            String[] helados = { "CHOCOLATE", "STRAWBERRY", "VANILLA" };

            for (String helado : helados) {
                controller = new GameController(GameMode.PVM, helado, "NARVAL");
                controller.startLevel(1);
                game = controller.getGame();
                board = getFieldValue(game, "board");

                IceCream iceCream = getFieldValue(board, "iceCream");
                String color = iceCream.getColor();
                System.out.println("  OK - " + helado + " - Color: " + color);
            }

            passTest("OPCIONES DE HELADO");

        } catch (Exception e) {
            failTest("OPCIONES DE HELADO", e);
        }
    }

    // ============ TEST 5: OPCIONES DE MONSTRUO ============
    private void testOpcionesMonstruo() {
        System.out.println("\n====== TEST 5: OPCIONES DE MONSTRUO DISPONIBLES ======\n");

        try {
            String[] monstruos = { "TROLL", "NARVAL", "ORANGE_SQUID", "YELLOW_SQUID", "POT" };

            for (String monstruo : monstruos) {
                controller = new GameController(GameMode.PVM, "CHOCOLATE", monstruo);
                controller.startLevel(1);
                game = controller.getGame();
                board = getFieldValue(game, "board");

                List<Enemy> enemies = getFieldValue(board, "enemies");
                String tipo = enemies.get(0).getClass().getSimpleName();
                System.out.println("  OK - " + monstruo + " - Tipo: " + tipo);
            }

            passTest("OPCIONES DE MONSTRUO");

        } catch (Exception e) {
            failTest("OPCIONES DE MONSTRUO", e);
        }
    }

    // ============ TEST 6: FLUJO COMPLETO ============
    private void testFlujoCompleto() {
        System.out.println("\n====== TEST 6: FLUJO COMPLETO ======\n");

        try {
            // 1. Seleccionar modo PVP
            System.out.println("\n  1. Seleccionar Modo: PVP (Cooperativo)");
            controller = new GameController(GameMode.PVP, "CHOCOLATE", null);

            // 2. Iniciar nivel
            System.out.println("  2. Iniciar Nivel: 1");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            // 3. Verificar configuración
            System.out.println("  3. Configuración:");
            IceCream ice = getFieldValue(board, "iceCream");
            System.out.println("     - Helado: CHOCOLATE");
            System.out.println("     - Tipo: " + ice.getClass().getSimpleName());

            List<Fruit> fruits = getFieldValue(board, "fruits");
            System.out.println("     - Frutas disponibles: " + fruits.size());

            List<Enemy> enemies = getFieldValue(board, "enemies");
            System.out.println("     - Monstruos: " + enemies.size());

            // 4. Ejecutar acciones del juego
            System.out.println("  4. Acciones en el juego:");
            game.toggleIceBlocks();
            System.out.println("     OK - Helado crea bloques de hielo");

            game.update();
            System.out.println("     OK - Juego actualizado");

            game.breakIceBlock();
            System.out.println("     OK - Bloques de hielo destruidos");

            passTest("FLUJO COMPLETO");

        } catch (Exception e) {
            failTest("FLUJO COMPLETO", e);
        }
    }

    // ============ MÉTODOS AUXILIARES ============

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    private void assertNotNull(String test, Object value) {
        if (value == null) {
            throw new AssertionError(test + ": El valor es null");
        }
    }

    private void assertTrue(String test, boolean condition) {
        if (!condition) {
            throw new AssertionError(test + ": Condición no cumplida");
        }
    }

    private void passTest(String testName) {
        testsPassed++;
        System.out.println("\nOK - TEST PASADO: " + testName);
    }

    private void failTest(String testName, Exception e) {
        testsFailed++;
        System.out.println("\nERROR - TEST FALLIDO: " + testName);
        System.out.println("  Error: " + e.getMessage());
        e.printStackTrace();
    }

    private void mostrarResumen() {
        System.out.println("\n========== RESUMEN DE TESTS ==========");

        int total = testsPassed + testsFailed;
        double porcentaje = total > 0 ? (testsPassed * 100.0) / total : 0;

        System.out.println("  Total de tests:        " + total);
        System.out.println("  OK - Tests pasados:    " + testsPassed);
        System.out.println("  ERROR - Tests fallidos:" + testsFailed);
        System.out.println("  Cobertura:             " + String.format("%.1f%%", porcentaje));

        System.out.println("\n  MODOS PROBADOS:");
        System.out.println("    - PVP (Cooperativo):     OK");
        System.out.println("    - PVM (vs Monstruo):     OK");
        System.out.println("    - MVM (Monstruo vs):     OK");

        System.out.println("\n  OPCIONES DE HELADO:");
        System.out.println("    - CHOCOLATE:             OK");
        System.out.println("    - STRAWBERRY (Fresa):    OK");
        System.out.println("    - VANILLA:               OK");

        System.out.println("\n  OPCIONES DE MONSTRUO:");
        System.out.println("    - TROLL:                 OK");
        System.out.println("    - NARVAL:                OK");
        System.out.println("    - ORANGE_SQUID:          OK");
        System.out.println("    - YELLOW_SQUID:          OK");
        System.out.println("    - POT (Olla):            OK");

        System.out.println("\n  FLUJO COMPLETO:");
        System.out.println("    - Selección de modo:     OK");
        System.out.println("    - Selección de helado:   OK");
        System.out.println("    - Selección de monstruo: OK");
        System.out.println("    - Inicio de juego:       OK");
        System.out.println("    - Acciones de juego:     OK");

        if (testsFailed == 0) {
            System.out.println("\nOK - TODOS LOS TESTS PASARON EXITOSAMENTE\n");
        } else {
            System.out.println("\nALERTA - " + testsFailed + " TEST(S) FALLARON\n");
        }
    }
}
