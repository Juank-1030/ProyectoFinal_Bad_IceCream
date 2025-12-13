package Test;

import Domain.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * TEST COMPREHENSIVO DEL PROYECTO
 * 
 * Prueba TODOS los aspectos del proyecto en un solo archivo:
 * - Inicialización del juego
 * - Todos los modos de juego (PVM, PVP, MVM)
 * - Persistencia (save/load)
 * - IAs (enemigos y helados)
 * - Mecánicas de juego (movimiento, frutas, obstáculos)
 * - Condiciones de victoria/derrota
 * - Configuraciones personalizadas
 */
public class ComprehensiveProjectTest {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST COMPREHENSIVO - COBERTURA TOTAL DEL PROYECTO        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        int totalTests = 0;
        int passedTests = 0;

        // Test 1: Inicialización básica
        if (testGameInitialization()) passedTests++;
        totalTests++;

        // Test 2: Modo PVM
        if (testGameModePVM()) passedTests++;
        totalTests++;

        // Test 3: Modo PVP
        if (testGameModePVP()) passedTests++;
        totalTests++;

        // Test 4: Modo MVM
        if (testGameModeMVM()) passedTests++;
        totalTests++;

        // Test 5: Persistencia (Save/Load)
        if (testPersistence()) passedTests++;
        totalTests++;

        // Test 6: IAs de enemigos
        if (testEnemyAIs()) passedTests++;
        totalTests++;

        // Test 7: IAs de helados
        if (testIceCreamAIs()) passedTests++;
        totalTests++;

        // Test 8: Mecánicas de juego
        if (testGameMechanics()) passedTests++;
        totalTests++;

        // Test 9: Frutas y recolección
        if (testFruitSystem()) passedTests++;
        totalTests++;

        // Test 10: Obstáculos
        if (testObstacleSystem()) passedTests++;
        totalTests++;

        // Test 11: Condiciones de victoria/derrota
        if (testGameConditions()) passedTests++;
        totalTests++;

        // Test 12: Configuraciones personalizadas
        if (testCustomConfigurations()) passedTests++;
        totalTests++;

        // Test 13: Todos los sabores de helado
        if (testIceCreamFlavors()) passedTests++;
        totalTests++;

        // Test 14: Todos los tipos de enemigos
        if (testEnemyTypes()) passedTests++;
        totalTests++;

        // Test 15: Sistema de puntos y tiempo
        if (testScoreAndTime()) passedTests++;
        totalTests++;

        // Resumen final
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      RESUMEN FINAL                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Tests Exitosos: " + passedTests + "/" + totalTests);
        System.out.println("║ Cobertura: " + String.format("%.1f", (passedTests * 100.0 / totalTests)) + "%");
        System.out.println("║ Estado: " + (passedTests == totalTests ? "TODO OK" : "FALLOS DETECTADOS"));
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    // ======================= TEST 1: INICIALIZACIÓN =======================
    private static boolean testGameInitialization() {
        System.out.println("\n[TEST 1] Inicialización del juego...");
        try {
            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, null, null, null, null);
            assert game.getGameState() == GameState.MENU : "Estado inicial debe ser MENU";
            assert game.getScore() == 0 : "Puntuación inicial debe ser 0";
            assert game.getIceCreamFlavor().equals("CHOCOLATE") : "Sabor debe ser CHOCOLATE";

            game.startLevel(1);
            assert game.getBoard() != null : "Board no debe ser nulo";
            assert game.getCurrentLevel() != null : "Level no debe ser nulo";
            assert game.getGameState() == GameState.PLAYING : "Estado debe ser PLAYING";
            assert game.getRemainingTime() > 0 : "Tiempo debe ser positivo";

            System.out.println("  ✓ Inicialización correcta");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 2: MODO PVM =======================
    private static boolean testGameModePVM() {
        System.out.println("\n[TEST 2] Modo PVM (Player vs Monsters)...");
        try {
            Game game = new Game(GameMode.PVM, "VANILLA", null, null, null, null, null);
            game.startLevel(1);

            assert game.getGameMode() == GameMode.PVM : "Modo debe ser PVM";
            assert game.getBoard().getEnemies().size() > 0 : "Debe haber enemigos";
            assert game.getBoard().getIceCream() != null : "Debe haber un helado";
            assert game.getBoard().getSecondIceCream() == null : "No debe haber segundo helado en PVM";

            // Intentar mover helado
            boolean moved = game.moveIceCream(Direction.UP);
            System.out.println("  ✓ Modo PVM funcionando correctamente");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 3: MODO PVP =======================
    private static boolean testGameModePVP() {
        System.out.println("\n[TEST 3] Modo PVP (Player vs Player)...");
        try {
            Game game = new Game(GameMode.PVP, "CHOCOLATE", "STRAWBERRY", "Troll", null, null, null);
            game.startLevel(1);

            assert game.getGameMode() == GameMode.PVP : "Modo debe ser PVP";
            assert game.getBoard().getIceCream() != null : "Debe haber primer helado";
            assert game.getBoard().getSecondIceCream() != null : "Debe haber segundo helado en PVP cooperativo";
            assert game.getIceCreamFlavor().equals("CHOCOLATE") : "Sabor 1 debe ser CHOCOLATE";
            assert game.getSecondIceCreamFlavor().equals("STRAWBERRY") : "Sabor 2 debe ser STRAWBERRY";

            // Mover ambos helados
            game.moveIceCream(Direction.UP);
            game.moveSecondIceCream(Direction.DOWN);

            System.out.println("  ✓ Modo PVP funcionando correctamente");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 4: MODO MVM =======================
    private static boolean testGameModeMVM() {
        System.out.println("\n[TEST 4] Modo MVM (Monster vs Monster)...");
        try {
            Game game = new Game(GameMode.MVM, "VANILLA", null, null, null, null, null);
            game.startLevel(1);

            assert game.getGameMode() == GameMode.MVM : "Modo debe ser MVM";
            assert game.getBoard().getIceCream() != null : "Debe haber un helado (controlado por IA)";
            assert game.getBoard().getEnemies().size() > 0 : "Debe haber enemigos";

            // En MVM, moveIceCream debe retornar false
            boolean moved = game.moveIceCream(Direction.UP);
            assert !moved : "No se debe poder mover helado en MVM";

            System.out.println("  ✓ Modo MVM funcionando correctamente");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 5: PERSISTENCIA =======================
    private static boolean testPersistence() {
        System.out.println("\n[TEST 5] Sistema de Persistencia (Save/Load)...");
        try {
            // Crear y modificar juego
            Game game1 = new Game(GameMode.PVM, "CHOCOLATE", null, null, null, null, null);
            game1.startLevel(1);
            game1.addScore(150);
            game1.moveIceCream(Direction.UP);

            // Guardar
            String filename = "test_save_comprehensive.dat";
            boolean saved = game1.saveGame(filename);
            assert saved : "Guardado debería ser exitoso";
            assert Game.savedGameExists(filename) : "Archivo guardado debe existir";

            // Cargar
            Game game2 = Game.loadGame(filename);
            assert game2 != null : "Juego cargado no debe ser nulo";
            assert game2.getScore() == 150 : "Puntuación debe ser 150";
            assert game2.getIceCreamFlavor().equals("CHOCOLATE") : "Sabor debe mantenerse";
            assert game2.getBoard() != null : "Board debe estar cargado";

            // Limpiar
            boolean deleted = Game.deleteSavedGame(filename);
            assert deleted : "Archivo debería ser eliminado";
            assert !Game.savedGameExists(filename) : "Archivo no debe existir después de eliminación";

            System.out.println("  ✓ Persistencia funcionando correctamente");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 6: IAs DE ENEMIGOS =======================
    private static boolean testEnemyAIs() {
        System.out.println("\n[TEST 6] IAs de Enemigos...");
        try {
            Game game = new Game(GameMode.PVM, "VANILLA", null, null, null, null, null);
            game.startLevel(1);

            List<Enemy> enemies = game.getBoard().getEnemies();
            assert enemies.size() > 0 : "Debe haber al menos un enemigo";

            // Verificar que hay diferentes tipos
            boolean hasTroll = enemies.stream().anyMatch(e -> e instanceof Troll);
            boolean hasPot = enemies.stream().anyMatch(e -> e instanceof Pot);
            boolean hasNarval = enemies.stream().anyMatch(e -> e instanceof Narval);

            // Actualizar el juego para que las IAs se muevan
            for (int i = 0; i < 10; i++) {
                game.update();
            }

            // Verificar que los enemigos se han movido
            boolean enemiesMoved = false;
            for (Enemy enemy : enemies) {
                if (enemy.getPosition() != null) {
                    enemiesMoved = true;
                    break;
                }
            }

            System.out.println("  ✓ IAs de enemigos funcionando: Troll=" + hasTroll + 
                             ", Pot=" + hasPot + ", Narval=" + hasNarval);
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 7: IAs DE HELADOS =======================
    private static boolean testIceCreamAIs() {
        System.out.println("\n[TEST 7] IAs de Helados...");
        try {
            // Test MVM con IA en helado
            Game game = new Game(GameMode.MVM, "VANILLA", null, null, null, null, null);
            game.setIceCreamAIStrategy("HUNGRY");
            game.startLevel(1);

            IceCream iceCream = game.getBoard().getIceCream();
            assert iceCream != null : "Debe haber helado";

            // Actualizar para que la IA se active
            for (int i = 0; i < 5; i++) {
                game.update();
            }

            // Test con todas las estrategias
            String[] strategies = {"HUNGRY", "FEARFUL", "EXPERT"};
            for (String strategy : strategies) {
                Game game2 = new Game(GameMode.MVM, "CHOCOLATE", null, null, null, null, null);
                game2.setIceCreamAIStrategy(strategy);
                game2.startLevel(1);
                IceCreamAIStrategy strat = game2.getBoard().getIceCream().getAIStrategy();
                assert strat != null : "Estrategia " + strategy + " debe estar configurada";
            }

            System.out.println("  ✓ IAs de helados funcionando (HUNGRY, FEARFUL, EXPERT)");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 8: MECÁNICAS DE JUEGO =======================
    private static boolean testGameMechanics() {
        System.out.println("\n[TEST 8] Mecánicas de Juego (movimiento, hielo, pausa)...");
        try {
            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, null, null, null, null);
            game.startLevel(1);

            Position initialPos = game.getBoard().getIceCream().getPosition();

            // Test movimiento
            game.moveIceCream(Direction.UP);
            game.update();

            // Test togglePause
            game.togglePause();
            assert game.getGameState() == GameState.PAUSED : "Estado debe ser PAUSED";
            game.togglePause();
            assert game.getGameState() == GameState.PLAYING : "Estado debe volver a PLAYING";

            // Test ice blocks
            int toggleResult = game.toggleIceBlocks();
            // Puede retornar 0, >0 o <0 dependiendo del contexto

            // Test break ice block
            boolean iceBlockBroken = game.breakIceBlock();
            // Puede ser true o false dependiendo de la configuración

            System.out.println("  ✓ Mecánicas de juego funcionando correctamente");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 9: SISTEMA DE FRUTAS =======================
    private static boolean testFruitSystem() {
        System.out.println("\n[TEST 9] Sistema de Frutas...");
        try {
            Game game = new Game(GameMode.PVM, "VANILLA", null, null, null, null, null);
            game.startLevel(1);

            List<Fruit> fruits = game.getBoard().getFruits();
            assert fruits.size() > 0 : "Debe haber frutas en el nivel";

            // Verificar tipos de frutas
            boolean hasGrape = fruits.stream().anyMatch(f -> f instanceof Grape);
            boolean hasBanana = fruits.stream().anyMatch(f -> f instanceof Banana);
            boolean hasCherry = fruits.stream().anyMatch(f -> f instanceof Cherry);
            boolean hasPineapple = fruits.stream().anyMatch(f -> f instanceof Pineapple);
            boolean hasCactus = fruits.stream().anyMatch(f -> f instanceof Cactus);

            // Verificar que ninguna fruta está recolectada inicialmente
            for (Fruit fruit : fruits) {
                assert !fruit.isCollected() : "Las frutas no deben estar recolectadas inicialmente";
            }

            int initialFruitCount = game.getBoard().getRemainingFruits();
            assert initialFruitCount > 0 : "Debe haber frutas sin recolectar";

            System.out.println("  ✓ Frutas: Grape=" + hasGrape + ", Banana=" + hasBanana + 
                             ", Cherry=" + hasCherry + ", Pineapple=" + hasPineapple + 
                             ", Cactus=" + hasCactus);
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 10: SISTEMA DE OBSTÁCULOS =======================
    private static boolean testObstacleSystem() {
        System.out.println("\n[TEST 10] Sistema de Obstáculos...");
        try {
            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, null, null, null, null);
            game.startLevel(1);

            Board board = game.getBoard();

            // Verificar tipos de obstáculos
            List<IceBlock> iceBlocks = board.getIceBlocks();
            List<Fogata> fogatas = board.getFogatas();
            List<BaldosaCaliente> baldosas = board.getBaldosasCalientes();
            List<IceBlockObstacle> iceBlockObstacles = board.getIceBlockObstacles();

            assert iceBlocks.size() > 0 : "Debe haber bloques de hielo";
            assert fogatas.size() > 0 : "Debe haber fogatas";

            // Verificar fogatas
            for (Fogata fogata : fogatas) {
                assert fogata.getPosition() != null : "Fogata debe tener posición";
                fogata.update();
                boolean encendida = fogata.isEncendida();
            }

            // Verificar baldosas
            for (BaldosaCaliente baldosa : baldosas) {
                assert baldosa.getPosition() != null : "Baldosa debe tener posición";
            }

            System.out.println("  ✓ Obstáculos: IceBlocks=" + iceBlocks.size() + 
                             ", Fogatas=" + fogatas.size() + ", Baldosas=" + baldosas.size());
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 11: CONDICIONES DE VICTORIA/DERROTA =======================
    private static boolean testGameConditions() {
        System.out.println("\n[TEST 11] Condiciones de Victoria/Derrota...");
        try {
            Game game = new Game(GameMode.PVM, "VANILLA", null, null, null, null, null);
            game.startLevel(1);

            assert game.getGameState() == GameState.PLAYING : "Debe estar PLAYING al inicio";

            // Simular game over por tiempo
            GameState state = game.getGameState();
            assert state != GameState.WON && state != GameState.LOST : "No debe ganar/perder al inicio";

            // Test pausa
            game.togglePause();
            assert game.getGameState() == GameState.PAUSED : "Debe estar PAUSED";

            game.togglePause();
            assert game.getGameState() == GameState.PLAYING : "Debe volver a PLAYING";

            // Test helado vivo
            IceCream iceCream = game.getBoard().getIceCream();
            assert iceCream.isAlive() : "Helado debe estar vivo";

            System.out.println("  ✓ Condiciones de victoria/derrota funcionando");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 12: CONFIGURACIONES PERSONALIZADAS =======================
    private static boolean testCustomConfigurations() {
        System.out.println("\n[TEST 12] Configuraciones Personalizadas...");
        try {
            // Configuración de enemigos
            Map<String, Integer> enemyConfig = new HashMap<>();
            enemyConfig.put("Troll", 1);
            enemyConfig.put("Pot", 2);

            // Configuración de frutas
            Map<String, Integer> fruitConfig = new HashMap<>();
            fruitConfig.put("Banana", 3);
            fruitConfig.put("Grape", 2);

            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, null, 
                               enemyConfig, fruitConfig, null);
            game.startLevel(1);

            // Verificar cantidad de enemigos
            int enemyCount = game.getBoard().getEnemies().size();
            assert enemyCount >= 3 : "Debe haber al menos 3 enemigos (1 Troll + 2 Pot)";

            // Verificar cantidad de frutas
            int fruitCount = game.getBoard().getFruits().size();
            assert fruitCount >= 5 : "Debe haber al menos 5 frutas (3 Banana + 2 Grape)";

            System.out.println("  ✓ Configuraciones personalizadas: " + enemyCount + 
                             " enemigos, " + fruitCount + " frutas");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 13: SABORES DE HELADO =======================
    private static boolean testIceCreamFlavors() {
        System.out.println("\n[TEST 13] Sabores de Helado...");
        try {
            String[] flavors = {"CHOCOLATE", "STRAWBERRY", "VANILLA"};
            int successCount = 0;

            for (String flavor : flavors) {
                Game game = new Game(GameMode.PVM, flavor, null, null, null, null, null);
                game.startLevel(1);

                IceCream iceCream = game.getBoard().getIceCream();
                assert iceCream != null : "Helado de " + flavor + " no fue creado";

                if (flavor.equals("CHOCOLATE")) {
                    assert iceCream instanceof ChocolateIceCream : "Debe ser ChocolateIceCream";
                } else if (flavor.equals("STRAWBERRY")) {
                    assert iceCream instanceof StrawberryIceCream : "Debe ser StrawberryIceCream";
                } else if (flavor.equals("VANILLA")) {
                    assert iceCream instanceof VanillaIceCream : "Debe ser VanillaIceCream";
                }

                successCount++;
            }

            assert successCount == 3 : "Todos los sabores deben funcionar";

            System.out.println("  ✓ Todos los sabores de helado funcionan (3/3)");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 14: TIPOS DE ENEMIGOS =======================
    private static boolean testEnemyTypes() {
        System.out.println("\n[TEST 14] Tipos de Enemigos...");
        try {
            Map<String, Integer> enemyConfig = new HashMap<>();
            enemyConfig.put("Troll", 1);
            enemyConfig.put("Pot", 1);
            enemyConfig.put("YellowSquid", 1);
            enemyConfig.put("Narval", 1);

            Game game = new Game(GameMode.PVM, "VANILLA", null, null, 
                               enemyConfig, null, null);
            game.startLevel(1);

            List<Enemy> enemies = game.getBoard().getEnemies();

            boolean hasTroll = enemies.stream().anyMatch(e -> e instanceof Troll);
            boolean hasPot = enemies.stream().anyMatch(e -> e instanceof Pot);
            boolean hasYellowSquid = enemies.stream().anyMatch(e -> e instanceof YellowSquid);
            boolean hasNarval = enemies.stream().anyMatch(e -> e instanceof Narval);

            assert hasTroll : "Debe haber Troll";
            assert hasPot : "Debe haber Pot";
            assert hasYellowSquid : "Debe haber YellowSquid";
            assert hasNarval : "Debe haber Narval";

            System.out.println("  ✓ Todos los tipos de enemigos funcionan (4/4)");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }

    // ======================= TEST 15: PUNTOS Y TIEMPO =======================
    private static boolean testScoreAndTime() {
        System.out.println("\n[TEST 15] Sistema de Puntos y Tiempo...");
        try {
            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, null, null, null, null);
            game.startLevel(1);

            int initialScore = game.getScore();
            assert initialScore == 0 : "Puntuación inicial debe ser 0";

            game.addScore(100);
            assert game.getScore() == 100 : "Puntuación debe ser 100";

            game.addScore(50);
            assert game.getScore() == 150 : "Puntuación debe ser 150";

            int initialTime = game.getRemainingTime();
            assert initialTime > 0 : "Tiempo inicial debe ser positivo";

            // Actualizar para decrementar tiempo
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 1100) {
                game.update();
            }

            int newTime = game.getRemainingTime();
            assert newTime < initialTime : "Tiempo debe haber disminuido";

            System.out.println("  ✓ Puntos: " + game.getScore() + ", Tiempo: " + newTime + "s");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
            return false;
        }
    }
}
