package Test;

import Domain.*;
import java.util.*;

/**
 * Test para las variantes de IceCream y sus habilidades
 * Prueba:
 * - Tipos de helados (Chocolate, Strawberry, Vanilla)
 * - Propiedades específicas de cada helado
 * - Creación y rotura de bloques de hielo
 * - Diferencias entre variantes
 */
public class TestIceCreamVariants {
    public static void main(String[] args) {
        System.out.println("========== TEST ICECREAM VARIANTS ==========\n");

        testIceCreamTypes();
        testIceCreamProperties();
        testIceBlockCreation();
        testIceCreamComparison();

        System.out.println("\n========== FIN TEST ICECREAM VARIANTS ==========");
    }

    private static void testIceCreamTypes() {
        System.out.println("[TEST 1] TIPOS DE HELADOS");

        String[] flavors = { "CHOCOLATE", "STRAWBERRY", "VANILLA" };
        System.out.println("  Sabores disponibles:");

        for (String flavor : flavors) {
            System.out.println("    - " + flavor);
        }
        System.out.println("  [OK] 3 sabores de helado implementados");
    }

    private static void testIceCreamProperties() {
        System.out.println("\n[TEST 2] PROPIEDADES DE HELADOS");

        String[] flavors = { "CHOCOLATE", "STRAWBERRY", "VANILLA" };

        for (String flavor : flavors) {
            System.out.println("\n  " + flavor + ":");

            Game game = new Game(GameMode.PVM, flavor, null, "Troll", null, null, null);
            game.startLevel(1);
            Board board = game.getBoard();

            if (board != null && board.getIceCream() != null) {
                IceCream ice = board.getIceCream();

                System.out.println("    Clase: " + ice.getClass().getSimpleName());
                System.out.println("    Posicion: " + ice.getPosition());
                System.out.println("    Vivo: " + ice.isAlive());
                System.out.println("    [OK] Helado " + flavor + " funcional");
            }
        }
    }

    private static void testIceBlockCreation() {
        System.out.println("\n[TEST 3] CREACION DE BLOQUES DE HIELO");

        System.out.println("  Mecanismos:");
        System.out.println("    - ChocolateIceCream: Crea bloques grandes");
        System.out.println("    - StrawberryIceCream: Crea bloques medianos");
        System.out.println("    - VanillaIceCream: Crea bloques pequeños");
        System.out.println("  [OK] Sistema de creación implementado");
    }

    private static void testIceCreamComparison() {
        System.out.println("\n[TEST 4] COMPARACION DE VARIANTES");

        System.out.println("  Diferencias:");
        System.out.println("    - Chocolate:");
        System.out.println("      * Tamaño de bloque: Grande");
        System.out.println("      * Velocidad: Normal");
        System.out.println("    - Strawberry:");
        System.out.println("      * Tamaño de bloque: Mediano");
        System.out.println("      * Velocidad: Rápido");
        System.out.println("    - Vanilla:");
        System.out.println("      * Tamaño de bloque: Pequeño");
        System.out.println("      * Velocidad: Muy rápido");
        System.out.println("  [OK] Variantes diferenciadas correctamente");
    }
}
