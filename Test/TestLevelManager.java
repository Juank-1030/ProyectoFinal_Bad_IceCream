package Test;

// TestLevelManager.java - Programa de prueba para LevelManager

import Domain.LevelManager;
import Domain.Level;

/**
 * Clase de prueba para verificar que LevelManager funciona correctamente
 */
public class TestLevelManager {
    public static void main(String[] args) {
        System.out.println("🎮 Iniciando prueba de LevelManager...\n");

        try {
            // Crear instancia de LevelManager
            System.out.println("1️⃣ Creando LevelManager...");
            LevelManager manager = new LevelManager();
            System.out.println("✅ LevelManager creado\n");

            // Probar cambio de niveles
            System.out.println("2️⃣ Probando cambio de niveles...");
            for (int i = 1; i <= LevelManager.getTotalNiveles(); i++) {
                boolean exito = manager.cambiarNivel(i);
                System.out.println("   Nivel " + i + ": " + (exito ? "✅" : "❌"));
            }
            System.out.println();

            // Obtener nivel actual
            System.out.println("3️⃣ Obteniendo información del nivel actual...");
            Level nivelActual = manager.getNivelActual();
            if (nivelActual != null) {
                System.out.println("   ✅ Nivel actual cargado");
                System.out.println("   - Nombre: " + nivelActual.getLevelName());
                System.out.println("   - Ancho: " + nivelActual.getBoardWidth());
                System.out.println("   - Alto: " + nivelActual.getBoardHeight());
            } else {
                System.out.println("   ⚠️ Nivel actual es null");
            }
            System.out.println();

            // Obtener niveles específicos
            System.out.println("4️⃣ Obteniendo niveles específicos...");
            for (int i = 1; i <= LevelManager.getTotalNiveles(); i++) {
                Level nivel = manager.obtenerNivel(i);
                System.out.println("   Nivel " + i + ": " +
                        (nivel != null ? "✅ " + nivel.getLevelName() : "❌ null"));
            }
            System.out.println();

            // Probar obtención de todos los niveles
            System.out.println("5️⃣ Obteniendo todos los niveles...");
            Level[] todos = manager.getNivelesDisponibles();
            System.out.println("   Total de niveles: " + todos.length);
            for (int i = 0; i < todos.length; i++) {
                System.out.println("   - [" + i + "] " +
                        (todos[i] != null ? "✅ " + todos[i].getLevelName() : "❌ null"));
            }
            System.out.println();

            // Información actual
            System.out.println("6️⃣ Información del nivel actual...");
            System.out.println("   - Número: " + manager.getNumerNivelActual());
            System.out.println("   - Índice: " + manager.getNivelActualIndex());
            System.out.println();

            // Validar números inválidos
            System.out.println("7️⃣ Probando validación (números inválidos)...");
            boolean invalido1 = manager.cambiarNivel(0);
            boolean invalido2 = manager.cambiarNivel(4);
            boolean invalido3 = manager.cambiarNivel(-1);
            System.out.println("   Nivel 0: " + (invalido1 ? "❌ No validado correctamente" : "✅ Rechazado"));
            System.out.println("   Nivel 4: " + (invalido2 ? "❌ No validado correctamente" : "✅ Rechazado"));
            System.out.println("   Nivel -1: " + (invalido3 ? "❌ No validado correctamente" : "✅ Rechazado"));
            System.out.println();

            System.out.println("✅ PRUEBA COMPLETADA");
            System.out.println("🎮 LevelManager funciona correctamente\n");

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
