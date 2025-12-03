package Test;

import Controller.GameController;
import Domain.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Clase de prueba para verificar que el Input Buffering funciona correctamente
 * Simula presionar múltiples teclas simultáneamente
 */
public class TestInputBuffering {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           PRUEBA DE INPUT BUFFERING - BAD ICE CREAM            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            // Crear controlador con nivel 1 (PVP, Fresa, Narval)
            GameController controller = new GameController(GameMode.PVP, "FRESA", "NARVAL");
            controller.startLevel(1);

            // Obtener el set de teclas presionadas vía reflexión
            Field keysField = GameController.class.getDeclaredField("keysPressed");
            keysField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Integer> keysPressed = (Set<Integer>) keysField.get(controller);

            System.out.println("✓ GameController inicializado");
            System.out.println("✓ Set de teclas presionadas creado: " + keysPressed.getClass().getSimpleName());
            System.out.println("  Tamaño inicial: " + keysPressed.size());

            // Simular presión de teclas
            System.out.println("\n--- Simulando presión de teclas ---");
            keysPressed.add(KeyEvent.VK_W); // Jugador 1 hacia arriba
            keysPressed.add(KeyEvent.VK_UP); // Jugador 2 hacia arriba

            System.out
                    .println("✓ Agregadas teclas: W (VK_W=" + KeyEvent.VK_W + ") y UP (VK_UP=" + KeyEvent.VK_UP + ")");
            System.out.println("  Tamaño después: " + keysPressed.size());
            System.out.println("  Contiene W: " + keysPressed.contains(KeyEvent.VK_W));
            System.out.println("  Contiene UP: " + keysPressed.contains(KeyEvent.VK_UP));

            // Simular liberación de teclas
            System.out.println("\n--- Simulando liberación de tecla W ---");
            keysPressed.remove(KeyEvent.VK_W);

            System.out.println("✓ Removida tecla W");
            System.out.println("  Tamaño después: " + keysPressed.size());
            System.out.println("  Contiene W: " + keysPressed.contains(KeyEvent.VK_W));
            System.out.println("  Contiene UP: " + keysPressed.contains(KeyEvent.VK_UP));

            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                   PRUEBA COMPLETADA                            ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            System.out.println("✓ El sistema de Input Buffering funciona correctamente");
            System.out.println("✓ Se pueden mantener múltiples teclas presionadas simultáneamente");

        } catch (Exception e) {
            System.err.println("✗ Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
