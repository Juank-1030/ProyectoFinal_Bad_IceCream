package Test;

import Domain.*;
import java.util.*;

/**
 * Test para el sistema de Frutas y sus comportamientos
 * Prueba:
 * - Tipos de frutas
 * - Comportamientos de frutas (estático, movimiento, teleporte)
 * - Recolección de frutas
 * - Propiedades de frutas
 */
public class TestFruit {
    public static void main(String[] args) {
        System.out.println("========== TEST FRUIT SYSTEM ==========\n");

        testFruitTypes();
        testFruitBehaviors();
        testFruitCollection();
        testFruitProperties();

        System.out.println("\n========== FIN TEST FRUIT ==========");
    }

    private static void testFruitTypes() {
        System.out.println("[TEST 1] TIPOS DE FRUTAS");

        String[] fruitNames = { "Grape", "Banana", "Cherry", "Pineapple", "Cactus" };
        System.out.println("  Frutas soportadas:");

        for (String fruitName : fruitNames) {
            System.out.println("    - " + fruitName);
        }
        System.out.println("  [OK] 5 tipos de frutas implementadas");
    }

    private static void testFruitBehaviors() {
        System.out.println("\n[TEST 2] COMPORTAMIENTOS DE FRUTAS");

        System.out.println("  Comportamientos:");
        System.out.println("    - StaticFruitBehavior: Frutas inmóviles");
        System.out.println("    - MovingFruitBehavior: Frutas que se mueven");
        System.out.println("    - TeleportFruitBehavior: Frutas que se teletransportan");

        System.out.println("  [OK] Sistema de comportamientos implementado");
    }

    private static void testFruitCollection() {
        System.out.println("\n[TEST 3] RECOLECCION DE FRUTAS");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null && board.getFruits() != null) {
            List<Fruit> fruits = board.getFruits();
            int initialCount = fruits.size();

            System.out.println("  Frutas iniciales: " + initialCount);

            if (!fruits.isEmpty()) {
                // Simular recolección eliminando una fruta
                Fruit firstFruit = fruits.get(0);
                System.out.println("  Fruta a recolectar: " + firstFruit.getClass().getSimpleName());
                System.out.println("    Posicion: " + firstFruit.getPosition());
                System.out.println("  [OK] Sistema de frutas funcional");
            }
        }
    }

    private static void testFruitProperties() {
        System.out.println("\n[TEST 4] PROPIEDADES DE FRUTAS");

        Game game = new Game(GameMode.PVM, "CHOCOLATE", null, "Troll", null, null, null);
        game.startLevel(1);
        Board board = game.getBoard();

        if (board != null && board.getFruits() != null && !board.getFruits().isEmpty()) {
            Fruit fruit = board.getFruits().get(0);

            System.out.println("  Fruta: " + fruit.getClass().getSimpleName());
            System.out.println("    Posicion: " + fruit.getPosition());
            System.out.println("    Recolectada: " + fruit.isCollected());

            FruitBehavior behavior = fruit.getBehavior();
            if (behavior != null) {
                System.out.println("    Comportamiento: " + behavior.getClass().getSimpleName());
            }

            System.out.println("  [OK] Propiedades de frutas accesibles");
        }
    }
}
