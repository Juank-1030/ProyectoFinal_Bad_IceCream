package Test;

import Domain.Game;

/**
 * Test para verificar que el guardado y carga de partida funcionan
 * correctamente
 */
public class TestPersistence {
    public static void main(String[] args) {
        System.out.println("========== TEST PERSISTENCIA ==========");

        // Test 1: Verificar si existe archivo guardado
        System.out.println("\n[TEST 1] Verificar existencia de archivo guardado");
        boolean exists = Game.savedGameExists("savegame.dat");
        System.out.println("  Archivo exists: " + exists);

        if (exists) {
            // Test 2: Intentar cargar el archivo
            System.out.println("\n[TEST 2] Cargar partida guardada");
            Game loadedGame = Game.loadGame("savegame.dat");

            if (loadedGame != null) {
                System.out.println("[OK] Juego cargado correctamente");
                System.out.println("  - GameMode: " + loadedGame.getGameMode());
                System.out.println("  - Board: " + (loadedGame.getBoard() != null ? "OK" : "NULL"));
                if (loadedGame.getBoard() != null) {
                    System.out
                            .println("  - IceCream: " + (loadedGame.getBoard().getIceCream() != null ? "OK" : "NULL"));
                    System.out.println("  - Enemies: " + (loadedGame.getBoard().getEnemies() != null
                            ? loadedGame.getBoard().getEnemies().size() + " enemigos"
                            : "NULL"));
                    System.out.println("  - Fruits: " + (loadedGame.getBoard().getFruits() != null
                            ? loadedGame.getBoard().getFruits().size() + " frutas"
                            : "NULL"));
                }
                System.out.println("  - GameState: " + loadedGame.getGameState());
            } else {
                System.out.println("[ERROR] No se pudo cargar la partida");
            }
        } else {
            System.out.println("[INFO] No hay archivo guardado disponible");
        }

        System.out.println("\n========== FIN TEST PERSISTENCIA ==========");
    }
}
