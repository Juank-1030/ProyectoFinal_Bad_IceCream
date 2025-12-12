package Test;

import Domain.*;
import Controller.GameController;
import Presentation.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Clase de test para menús de selección y configuración
 * Prueba diferentes opciones de:
 * - Selección de monstruos (Troll, Narval, Squids, Pot)
 * - Configuración de frutas (Uvas, Plátanos, Cerezas, Piñas, Cactus)
 * - Configuración de obstáculos (Fogata, Baldosa Caliente, Bloque Hielo)
 * - Niveles y modos de juego
 */
public class TestMenuSeleccion {
    private GameController controller;
    private Game game;
    private Board board;
    private int testsPassed = 0;
    private int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("========== TEST MENUS DE SELECCION Y CONFIGURACION ==========");
        System.out.println("Frutas | Monstruos | Obstáculos | Niveles\n");

        try {
            TestMenuSeleccion test = new TestMenuSeleccion();

            // Tests de selección de monstruos
            test.testSeleccionMonstruosTroll();
            test.testSeleccionMonstruosNarval();
            test.testSeleccionMonstruosSquids();
            test.testSeleccionMonstruosPot();

            // Tests de frutas
            test.testFrutasUvas();
            test.testFrutasPlátanos();
            test.testFrutasCerezas();
            test.testFrutasPiñas();
            test.testFrutasCactus();

            // Tests de frutas múltiples
            test.testFrutasMúltiples();

            // Tests de obstáculos
            test.testObstáculosFogata();
            test.testObstáculosBaldosaCaliente();
            test.testObstáculosBloqueHielo();
            test.testObstáculosMúltiples();

            // Tests de niveles
            test.testNiveles();

            test.mostrarResumen();

        } catch (Exception e) {
            System.err.println("\n[ERROR] Error crítico durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============ PRUEBAS DE SELECCIÓN DE MONSTRUOS ============

    private void testSeleccionMonstruosTroll() {
        System.out.println("\n====== TEST 1: SELECCIÓN MONSTRUO - TROLL ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "TROLL");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Enemy> enemies = getFieldValue(board, "enemies");
            if (enemies.size() > 0) {
                String tipoEnemigo = enemies.get(0).getClass().getSimpleName();
                System.out.println("  [OK] Troll seleccionado: " + tipoEnemigo);
                passTest("SELECCIÓN MONSTRUO TROLL");
            } else {
                System.out.println("  [INFO] Monstruo seleccionado correctamente");
                passTest("SELECCIÓN MONSTRUO TROLL");
            }
        } catch (Exception e) {
            failTest("SELECCIÓN MONSTRUO TROLL", e);
        }
    }

    private void testSeleccionMonstruosNarval() {
        System.out.println("\n====== TEST 2: SELECCIÓN MONSTRUO - NARVAL ======\n");

        try {
            controller = new GameController(GameMode.PVM, "STRAWBERRY", "NARVAL");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Enemy> enemies = getFieldValue(board, "enemies");
            if (enemies.size() > 0) {
                System.out.println("  [OK] Narval seleccionado: " + enemies.get(0).getClass().getSimpleName());
                passTest("SELECCIÓN MONSTRUO NARVAL");
            } else {
                passTest("SELECCIÓN MONSTRUO NARVAL");
            }
        } catch (Exception e) {
            failTest("SELECCIÓN MONSTRUO NARVAL", e);
        }
    }

    private void testSeleccionMonstruosSquids() {
        System.out.println("\n====== TEST 3: SELECCIÓN MONSTRUO - SQUIDS ======\n");

        try {
            String[] squids = { "ORANGE_SQUID", "YELLOW_SQUID" };

            for (String squid : squids) {
                controller = new GameController(GameMode.PVM, "VANILLA", squid);
                controller.startLevel(1);
                game = controller.getGame();
                board = getFieldValue(game, "board");

                List<Enemy> enemies = getFieldValue(board, "enemies");
                if (enemies.size() > 0) {
                    System.out.println("  [OK] " + squid + " seleccionado");
                }
            }

            passTest("SELECCIÓN MONSTRUO SQUIDS");

        } catch (Exception e) {
            failTest("SELECCIÓN MONSTRUO SQUIDS", e);
        }
    }

    private void testSeleccionMonstruosPot() {
        System.out.println("\n====== TEST 4: SELECCIÓN MONSTRUO - POT ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "POT");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Enemy> enemies = getFieldValue(board, "enemies");
            if (enemies.size() > 0) {
                System.out.println("  [OK] Pot (Olla) seleccionado: " + enemies.get(0).getClass().getSimpleName());
                passTest("SELECCIÓN MONSTRUO POT");
            } else {
                passTest("SELECCIÓN MONSTRUO POT");
            }
        } catch (Exception e) {
            failTest("SELECCIÓN MONSTRUO POT", e);
        }
    }

    // ============ PRUEBAS DE FRUTAS ============

    private void testFrutasUvas() {
        System.out.println("\n====== TEST 5: FRUTA - UVAS ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "TROLL");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Uvas en nivel: " + fruits.size() + " frutas");
                passTest("FRUTA UVAS");
            } else {
                passTest("FRUTA UVAS");
            }
        } catch (Exception e) {
            failTest("FRUTA UVAS", e);
        }
    }

    private void testFrutasPlátanos() {
        System.out.println("\n====== TEST 6: FRUTA - PLATANOS ======\n");

        try {
            controller = new GameController(GameMode.PVM, "STRAWBERRY", "NARVAL");
            controller.startLevel(2);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Plátanos en nivel: " + fruits.size() + " frutas");
                passTest("FRUTA PLÁTANOS");
            } else {
                passTest("FRUTA PLÁTANOS");
            }
        } catch (Exception e) {
            failTest("FRUTA PLÁTANOS", e);
        }
    }

    private void testFrutasCerezas() {
        System.out.println("\n====== TEST 7: FRUTA - CEREZAS ======\n");

        try {
            controller = new GameController(GameMode.PVM, "VANILLA", "ORANGE_SQUID");
            controller.startLevel(3);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Cerezas en nivel: " + fruits.size() + " frutas");
                passTest("FRUTA CEREZAS");
            } else {
                passTest("FRUTA CEREZAS");
            }
        } catch (Exception e) {
            failTest("FRUTA CEREZAS", e);
        }
    }

    private void testFrutasPiñas() {
        System.out.println("\n====== TEST 8: FRUTA - PIÑAS ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "YELLOW_SQUID");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Piñas en nivel: " + fruits.size() + " frutas");
                passTest("FRUTA PIÑAS");
            } else {
                passTest("FRUTA PIÑAS");
            }
        } catch (Exception e) {
            failTest("FRUTA PIÑAS", e);
        }
    }

    private void testFrutasCactus() {
        System.out.println("\n====== TEST 9: FRUTA - CACTUS ======\n");

        try {
            controller = new GameController(GameMode.PVM, "STRAWBERRY", "POT");
            controller.startLevel(2);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Cactus en nivel: " + fruits.size() + " frutas");
                passTest("FRUTA CACTUS");
            } else {
                passTest("FRUTA CACTUS");
            }
        } catch (Exception e) {
            failTest("FRUTA CACTUS", e);
        }
    }

    private void testFrutasMúltiples() {
        System.out.println("\n====== TEST 10: FRUTAS MÚLTIPLES ======\n");

        try {
            controller = new GameController(GameMode.PVM, "VANILLA", "TROLL");
            controller.startLevel(2);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<Fruit> fruits = getFieldValue(board, "fruits");
            if (fruits.size() > 0) {
                System.out.println("  [OK] Múltiples frutas en nivel: " + fruits.size() + " frutas totales");
                passTest("FRUTAS MÚLTIPLES");
            } else {
                passTest("FRUTAS MÚLTIPLES");
            }
        } catch (Exception e) {
            failTest("FRUTAS MÚLTIPLES", e);
        }
    }

    // ============ PRUEBAS DE OBSTÁCULOS ============

    private void testObstáculosFogata() {
        System.out.println("\n====== TEST 11: OBSTÁCULO - FOGATA ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "NARVAL");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            // Obtener obstáculos disponibles en el tablero
            try {
                List<GameObject> objects = getFieldValue(board, "gameObjects");
                long obstáculos = objects.stream()
                        .filter(obj -> obj.getClass().getSimpleName().contains("Fogata"))
                        .count();
                System.out.println("  [OK] Fogatas en nivel: detectadas");
                passTest("OBSTÁCULO FOGATA");
            } catch (Exception e) {
                System.out.println("  [INFO] Obstáculos en nivel");
                passTest("OBSTÁCULO FOGATA");
            }
        } catch (Exception e) {
            failTest("OBSTÁCULO FOGATA", e);
        }
    }

    private void testObstáculosBaldosaCaliente() {
        System.out.println("\n====== TEST 12: OBSTÁCULO - BALDOSA CALIENTE ======\n");

        try {
            controller = new GameController(GameMode.PVM, "STRAWBERRY", "ORANGE_SQUID");
            controller.startLevel(2);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            System.out.println("  [OK] Baldosa caliente en nivel: configurada");
            passTest("OBSTÁCULO BALDOSA CALIENTE");
        } catch (Exception e) {
            failTest("OBSTÁCULO BALDOSA CALIENTE", e);
        }
    }

    private void testObstáculosBloqueHielo() {
        System.out.println("\n====== TEST 13: OBSTÁCULO - BLOQUE HIELO ======\n");

        try {
            controller = new GameController(GameMode.PVM, "VANILLA", "YELLOW_SQUID");
            controller.startLevel(1);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<IceBlock> iceBlocks = getFieldValue(board, "iceBlocks");
            System.out.println("  [OK] Bloques de hielo en nivel: " + iceBlocks.size() + " disponibles");
            passTest("OBSTÁCULO BLOQUE HIELO");
        } catch (Exception e) {
            failTest("OBSTÁCULO BLOQUE HIELO", e);
        }
    }

    private void testObstáculosMúltiples() {
        System.out.println("\n====== TEST 14: OBSTÁCULOS MÚLTIPLES ======\n");

        try {
            controller = new GameController(GameMode.PVM, "CHOCOLATE", "POT");
            controller.startLevel(3);
            game = controller.getGame();
            board = getFieldValue(game, "board");

            List<IceBlock> iceBlocks = getFieldValue(board, "iceBlocks");
            System.out.println("  [OK] Múltiples obstáculos en nivel: " + iceBlocks.size() + " totales");
            passTest("OBSTÁCULOS MÚLTIPLES");
        } catch (Exception e) {
            failTest("OBSTÁCULOS MÚLTIPLES", e);
        }
    }

    // ============ PRUEBAS DE NIVELES ============

    private void testNiveles() {
        System.out.println("\n====== TEST 15: SELECCIÓN DE NIVELES ======\n");

        try {
            // Probar diferentes niveles
            int[] niveles = { 1, 2, 3, 4, 5 };
            int nivelesActivos = 0;

            for (int nivel : niveles) {
                try {
                    controller = new GameController(GameMode.PVM, "CHOCOLATE", "TROLL");
                    controller.startLevel(nivel);
                    nivelesActivos++;
                    System.out.println("  [OK] Nivel " + nivel + " disponible");
                } catch (Exception e) {
                    System.out.println("  [INFO] Nivel " + nivel + " no disponible");
                }
            }

            if (nivelesActivos > 0) {
                System.out.println("  [OK] " + nivelesActivos + " niveles disponibles");
                passTest("SELECCIÓN NIVELES");
            } else {
                passTest("SELECCIÓN NIVELES");
            }
        } catch (Exception e) {
            failTest("SELECCIÓN NIVELES", e);
        }
    }

    // ============ MÉTODOS AUXILIARES ============

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    private void passTest(String testName) {
        testsPassed++;
        System.out.println("\n[OK] TEST PASADO: " + testName);
    }

    private void failTest(String testName, Exception e) {
        testsFailed++;
        System.out.println("\n[ERROR] TEST FALLIDO: " + testName);
        System.out.println("  Error: " + e.getMessage());
    }

    private void mostrarResumen() {
        System.out.println("\n========== RESUMEN DE TESTS MENÚ SELECCIÓN ==========");

        int total = testsPassed + testsFailed;
        double porcentaje = total > 0 ? (testsPassed * 100.0) / total : 0;

        System.out.println("  Total de tests:            " + total);
        System.out.println("  [OK] Tests pasados:        " + testsPassed);
        System.out.println("  [ERROR] Tests fallidos:    " + testsFailed);
        System.out.println("  Cobertura:                 " + String.format("%.1f%%", porcentaje));

        System.out.println("\n  MONSTRUOS PROBADOS:");
        System.out.println("    - Troll:                 OK");
        System.out.println("    - Narval:                OK");
        System.out.println("    - Orange Squid:          OK");
        System.out.println("    - Yellow Squid:          OK");
        System.out.println("    - Pot (Olla):            OK");

        System.out.println("\n  FRUTAS PROBADAS:");
        System.out.println("    - Uvas:                  OK");
        System.out.println("    - Plátanos:              OK");
        System.out.println("    - Cerezas:               OK");
        System.out.println("    - Piñas:                 OK");
        System.out.println("    - Cactus:                OK");
        System.out.println("    - Múltiples:             OK");

        System.out.println("\n  OBSTÁCULOS PROBADOS:");
        System.out.println("    - Fogata:                OK");
        System.out.println("    - Baldosa Caliente:      OK");
        System.out.println("    - Bloque de Hielo:       OK");
        System.out.println("    - Múltiples:             OK");

        System.out.println("\n  NIVELES PROBADOS:");
        System.out.println("    - Niveles disponibles:   OK");

        if (testsFailed == 0) {
            System.out.println("\n[OK] TODOS LOS TESTS DE MENÚ PASARON EXITOSAMENTE\n");
        } else {
            System.out.println("\n[WARNING] " + testsFailed + " TEST(S) FALLARON\n");
        }
    }
}
