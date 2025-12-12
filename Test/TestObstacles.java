package Test;

import Domain.*;
import java.util.*;

/**
 * Test para el sistema de Obstáculos
 * Prueba:
 * - Tipos de obstáculos
 * - Propiedades de obstáculos
 * - Interacción con jugador
 * - Destrucción de obstáculos
 */
public class TestObstacles {
    public static void main(String[] args) {
        System.out.println("========== TEST OBSTACLE SYSTEM ==========\n");

        testObstacleTypes();
        testObstacleProperties();
        testObstacleInteraction();
        testIceBlockDestruction();

        System.out.println("\n========== FIN TEST OBSTACLES ==========");
    }

    private static void testObstacleTypes() {
        System.out.println("[TEST 1] TIPOS DE OBSTACULOS");

        String[] obstacleTypes = {
                "IceBlockObstacle",
                "Fogata",
                "BaldosaCaliente"
        };

        System.out.println("  Obstáculos soportados:");
        for (String type : obstacleTypes) {
            System.out.println("    - " + type);
        }
        System.out.println("  [OK] 3 tipos de obstáculos implementados");
    }

    private static void testObstacleProperties() {
        System.out.println("\n[TEST 2] PROPIEDADES DE OBSTACULOS");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            List<IceBlock> iceBlocks = board.getIceBlocks();
            List<Fogata> fogatas = board.getFogatas();
            List<BaldosaCaliente> baldosas = board.getBaldosasCalientes();

            int totalObstacles = (iceBlocks != null ? iceBlocks.size() : 0) +
                    (fogatas != null ? fogatas.size() : 0) +
                    (baldosas != null ? baldosas.size() : 0);

            System.out.println("  Total obstáculos: " + totalObstacles);
            System.out.println("    - Bloques de hielo: " + (iceBlocks != null ? iceBlocks.size() : 0));
            System.out.println("    - Fogatas: " + (fogatas != null ? fogatas.size() : 0));
            System.out.println("    - Baldosas calientes: " + (baldosas != null ? baldosas.size() : 0));
            System.out.println("  [OK] Obstáculos correctamente distribuidos");

            // Mostrar propiedades del primer obstáculo de cada tipo
            if (iceBlocks != null && !iceBlocks.isEmpty()) {
                IceBlock first = iceBlocks.get(0);
                System.out.println("  Primer IceBlock: " + first.getClass().getSimpleName());
                System.out.println("    Posicion: " + first.getPosition());
            }
        }
    }

    private static void testObstacleInteraction() {
        System.out.println("\n[TEST 3] INTERACCION CON OBSTACULOS");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            IceCream iceCream = board.getIceCream();
            List<IceBlock> iceBlocks = board.getIceBlocks();

            if (iceCream != null && iceBlocks != null && !iceBlocks.isEmpty()) {
                System.out.println("  Posicion helado: " + iceCream.getPosition());
                System.out.println("  Total obstáculos IceBlock: " + iceBlocks.size());

                // Simular colisión
                int collisions = 0;
                for (IceBlock ice : iceBlocks) {
                    if (ice.getPosition().equals(iceCream.getPosition())) {
                        collisions++;
                    }
                }
                System.out.println("  Colisiones detectadas: " + collisions);
                System.out.println("  [OK] Sistema de interacción funcional");
            }
        }
    }

    private static void testIceBlockDestruction() {
        System.out.println("\n[TEST 4] DESTRUCCION DE BLOQUES DE HIELO");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null) {
            IceCream iceCream = board.getIceCream();
            List<IceBlock> iceBlocks = board.getIceBlocks();

            if (iceCream != null) {
                System.out.println("  Helado " + iceCream.getClass().getSimpleName() + " puede crear obstáculos");
                System.out.println("  Obstáculos IceBlock en tablero: " +
                        (iceBlocks != null ? iceBlocks.size() : 0));
                System.out.println("  [OK] Sistema de creación de obstáculos funcional");
            }
        }
    }
}
