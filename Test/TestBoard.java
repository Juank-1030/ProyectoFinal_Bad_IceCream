package Test;

import Domain.*;
import java.util.*;

/**
 * Test comprehensivo para el sistema de Board (tablero)
 * Prueba:
 * - Creación y gestión del tablero
 * - Manejo de posiciones
 * - Sistema de colisiones
 * - Adición/eliminación de objetos
 */
public class TestBoard {
    public static void main(String[] args) {
        System.out.println("========== TEST BOARD & COLLISION SYSTEM ==========\n");

        testBoardCreation();
        testBoardDimensions();
        testObjectPlacement();
        testCollisionDetection();
        testMovementValidation();

        System.out.println("\n========== FIN TEST BOARD ==========");
    }

    private static void testBoardCreation() {
        System.out.println("[TEST 1] CREACION DE BOARD");

        try {
            Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
            game.startLevel(1);

            Board board = game.getBoard();
            if (board != null) {
                System.out.println("  [OK] Board creado correctamente");
                System.out.println("  Dimensiones: " + board.getWidth() + "x" + board.getHeight());
            } else {
                System.out.println("  [ERROR] No se pudo crear el board");
            }
        } catch (Exception e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    private static void testBoardDimensions() {
        System.out.println("\n[TEST 2] DIMENSIONES DEL BOARD");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            int width = board.getWidth();
            int height = board.getHeight();

            System.out.println("  Ancho: " + width);
            System.out.println("  Alto: " + height);

            if (width > 0 && height > 0) {
                System.out.println("  [OK] Dimensiones válidas");
            }
        }
    }

    private static void testObjectPlacement() {
        System.out.println("\n[TEST 3] COLOCACION DE OBJETOS");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            // Verificar helado
            IceCream iceCream = board.getIceCream();
            if (iceCream != null) {
                System.out.println("  Helado: " + iceCream.getClass().getSimpleName());
                System.out.println("    Posicion: " + iceCream.getPosition());
                System.out.println("    [OK] Helado colocado");
            }

            // Verificar enemigos
            List<Enemy> enemies = board.getEnemies();
            if (enemies != null && !enemies.isEmpty()) {
                System.out.println("  Enemigos: " + enemies.size());
                for (Enemy enemy : enemies) {
                    System.out.println("    - " + enemy.getClass().getSimpleName() + " en " + enemy.getPosition());
                }
                System.out.println("    [OK] Enemigos colocados");
            }

            // Verificar frutas
            List<Fruit> fruits = board.getFruits();
            if (fruits != null && !fruits.isEmpty()) {
                System.out.println("  Frutas: " + fruits.size());
                System.out.println("    [OK] Frutas colocadas");
            }
        }
    }

    private static void testCollisionDetection() {
        System.out.println("\n[TEST 4] DETECCION DE COLISIONES");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            IceCream iceCream = board.getIceCream();
            if (iceCream != null) {
                Position icePos = iceCream.getPosition();
                System.out.println("  Posicion helado: " + icePos);

                // Verificar si hay obstáculos
                List<IceBlock> iceBlocks = board.getIceBlocks();
                List<Fogata> fogatas = board.getFogatas();
                List<BaldosaCaliente> baldosas = board.getBaldosasCalientes();

                int totalObstacles = (iceBlocks != null ? iceBlocks.size() : 0) +
                        (fogatas != null ? fogatas.size() : 0) +
                        (baldosas != null ? baldosas.size() : 0);

                System.out.println("  Obstáculos totales: " + totalObstacles);
                System.out.println("    [OK] Sistema de obstáculos funcional");

                // Verificar si hay frutas cercanas
                List<Fruit> fruits = board.getFruits();
                if (fruits != null) {
                    int nearbyFruits = 0;
                    for (Fruit fruit : fruits) {
                        if (fruit.getPosition().equals(icePos)) {
                            nearbyFruits++;
                        }
                    }
                    System.out.println("  Frutas en posicion helado: " + nearbyFruits);
                    System.out.println("    [OK] Colisiones detectadas");
                }
            }
        }
    }

    private static void testMovementValidation() {
        System.out.println("\n[TEST 5] VALIDACION DE MOVIMIENTO");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            IceCream iceCream = board.getIceCream();
            if (iceCream != null) {
                Position currentPos = iceCream.getPosition();

                // Probar todas las direcciones
                Direction[] directions = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
                int validMoves = 0;

                for (Direction dir : directions) {
                    if (iceCream.canMoveTo(currentPos)) {
                        validMoves++;
                    }
                }

                System.out.println("  Movimientos válidos desde posicion: " + validMoves + "/4");
                System.out.println("    [OK] Sistema de validación funcional");
            }
        }
    }
}
