// TestSelectLevel.java - Programa de prueba para SelectLevel

import Presentation.SelectLevel;
import javax.swing.SwingUtilities;

/**
 * Clase de prueba para verificar que SelectLevel funciona correctamente
 */
public class TestSelectLevel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear instancia de SelectLevel
            SelectLevel select = new SelectLevel();

            // Registrar callbacks para prueba
            select.setOnLevel1Click(() -> {
                System.out.println("✅ Nivel 1 clickeado");
            });

            select.setOnLevel2Click(() -> {
                System.out.println("✅ Nivel 2 clickeado");
            });

            select.setOnLevel3Click(() -> {
                System.out.println("✅ Nivel 3 clickeado");
            });

            select.setOnBackClick(() -> {
                System.out.println("✅ Botón Atrás clickeado");
            });

            // Hacer visible la ventana
            select.setVisible(true);

            System.out.println("🎮 SelectLevel iniciado");
            System.out.println("📝 Haz clic en los botones para probar:");
            System.out.println("   - Nivel 1 (⭐)");
            System.out.println("   - Nivel 2 (⭐⭐)");
            System.out.println("   - Nivel 3 (⭐⭐⭐)");
            System.out.println("   - Atrás");
        });
    }
}
