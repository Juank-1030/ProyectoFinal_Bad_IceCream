package Domain;

import java.util.Scanner;

/**
 * ConsoleGame - Versión de consola del juego
 * 
 * PROPÓSITO:
 * - Testing de la lógica del juego SIN GUI
 * - Demostración de que Domain es independiente de UI
 * - Debugging rápido durante desarrollo
 * 
 * NO ES LA VERSIÓN FINAL DEL JUEGO
 * Solo para desarrollo y testing
 */
public class ConsoleGame {

    private Game game;
    private Scanner scanner;
    private boolean running;

    public ConsoleGame() {
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Inicia el juego en modo consola
     */
    public void start() {
        mostrarBienvenida();

        // Configuración del juego
        GameMode modo = seleccionarModo();
        String helado = seleccionarHelado();
        int nivel = seleccionarNivel();

        // Crear y configurar el juego
        game = new Game(modo, helado);
        game.startLevel(nivel);

        System.out.println("\n¡Juego iniciado!\n");

        // Game loop
        gameLoop();

        // Resultado final
        mostrarResultadoFinal();

        scanner.close();
    }

    /**
     * Muestra pantalla de bienvenida
     */
    private void mostrarBienvenida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          🍦 BAD DOPO-CREAM - Modo Consola 🍦");
        System.out.println("=".repeat(60));
        System.out.println("\nVersión de testing - Solo lógica del juego");
        System.out.println("(Para la versión completa, usa la GUI)\n");
    }

    /**
     * Permite al usuario seleccionar el modo de juego
     */
    private GameMode seleccionarModo() {
        System.out.println("Selecciona el modo de juego:");
        System.out.println("1. PVP (Player vs Player)");
        System.out.println("2. PVM (Player vs Machine)");
        System.out.println("3. MVM (Machine vs Machine)");
        System.out.print("Opción (1-3): ");

        int opcion = leerEntero(1, 3);

        switch (opcion) {
            case 1:
                return GameMode.PVP;
            case 2:
                return GameMode.PVM;
            case 3:
                return GameMode.MVM;
            default:
                return GameMode.PVP;
        }
    }

    /**
     * Permite al usuario seleccionar el helado
     */
    private String seleccionarHelado() {
        System.out.println("\nSelecciona tu helado:");
        System.out.println("1. Vainilla");
        System.out.println("2. Fresa");
        System.out.println("3. Chocolate");
        System.out.print("Opción (1-3): ");

        int opcion = leerEntero(1, 3);

        switch (opcion) {
            case 1:
                return "Vainilla";
            case 2:
                return "Fresa";
            case 3:
                return "Chocolate";
            default:
                return "Vainilla";
        }
    }

    /**
     * Permite al usuario seleccionar el nivel
     */
    private int seleccionarNivel() {
        System.out.println("\nSelecciona el nivel:");
        System.out.println("1. Nivel 1 - Troll's Maze");
        System.out.println("2. Nivel 2 - Pot Chase");
        System.out.println("3. Nivel 3 - Orange Squid");
        System.out.print("Opción (1-3): ");

        return leerEntero(1, 3);
    }

    /**
     * Loop principal del juego
     */
    private void gameLoop() {
        mostrarAyuda();

        while (running && game.getGameState() == GameState.PLAYING) {
            // Mostrar estado actual
            mostrarEstado();

            // Leer comando
            System.out.print("\n> ");
            String comando = scanner.nextLine().trim().toLowerCase();

            // Procesar comando
            if (!procesarComando(comando)) {
                continue;
            }

            // Actualizar juego
            game.update();

            // Pequeña pausa (simula frames)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verificar si terminó
            if (game.getGameState() != GameState.PLAYING) {
                running = false;
            }
        }
    }

    /**
     * Muestra la ayuda de comandos
     */
    private void mostrarAyuda() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("COMANDOS JUGADOR 1 (Helado):");
        System.out.println("  w         - Mover arriba");
        System.out.println("  s         - Mover abajo");
        System.out.println("  a         - Mover izquierda");
        System.out.println("  d         - Mover derecha");
        System.out.println("  espacio   - Crear/Romper bloques de hielo");

        if (game.getGameMode() == GameMode.PVP) {
            System.out.println("\nCOMANDOS JUGADOR 2 (Monstruo):");
            System.out.println("  i         - Mover monstruo ARRIBA");
            System.out.println("  k         - Mover monstruo ABAJO");
            System.out.println("  j         - Mover monstruo IZQUIERDA");
            System.out.println("  l         - Mover monstruo DERECHA");
        }

        System.out.println("\nOTROS COMANDOS:");
        System.out.println("  p/pausa   - Pausar/Reanudar");
        System.out.println("  h/ayuda   - Mostrar esta ayuda");
        System.out.println("  q/salir   - Salir del juego");
        System.out.println("-".repeat(60));
    }

    /**
     * Muestra el estado actual del juego
     */
    private void mostrarEstado() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("NIVEL: " + game.getCurrentLevel().getLevelNumber() +
                " | PUNTOS: " + game.getScore() +
                " | TIEMPO: " + game.getRemainingTime() + "s");
        System.out.println("FRUTAS RESTANTES: " + game.getBoard().getRemainingFruits());

        // Posición del helado
        Position icePos = game.getBoard().getIceCream().getPosition();
        System.out.println("\nHELADO (" + game.getIceCreamFlavor() + "): [" +
                icePos.getX() + ", " + icePos.getY() + "]");

        // Enemigos
        System.out.println("\nENEMIGOS:");
        for (Enemy enemy : game.getBoard().getEnemies()) {
            if (enemy.isAlive()) {
                Position pos = enemy.getPosition();
                System.out.println("  • " + enemy.getEnemyType() + " [" +
                        pos.getX() + ", " + pos.getY() + "]");
            }
        }

        // Frutas (mostrar solo algunas)
        int frutasVisibles = 0;
        System.out.println("\nFRUTAS (primeras 5):");
        for (Fruit fruit : game.getBoard().getFruits()) {
            if (!fruit.isCollected() && frutasVisibles < 5) {
                Position pos = fruit.getPosition();
                System.out.println("  • " + fruit.getFruitType() + " [" +
                        pos.getX() + ", " + pos.getY() + "]");
                frutasVisibles++;
            }
        }

        System.out.println("=".repeat(60));
    }

    /**
     * Procesa los comandos del usuario
     * 
     * @return true si el comando fue válido, false si no
     */
    private boolean procesarComando(String comando) {
        // ========== JUGADOR 1 (Helado) ==========
        // Comandos de movimiento
        if (comando.equals("w") || comando.equals("arriba")) {
            game.moveIceCream(Direction.UP);
            System.out.println("🍦 J1: ↑ Moviendo arriba");
            return true;
        }

        if (comando.equals("s") || comando.equals("abajo")) {
            game.moveIceCream(Direction.DOWN);
            System.out.println("🍦 J1: ↓ Moviendo abajo");
            return true;
        }

        if (comando.equals("a") || comando.equals("izquierda")) {
            game.moveIceCream(Direction.LEFT);
            System.out.println("🍦 J1: ← Moviendo izquierda");
            return true;
        }

        if (comando.equals("d") || comando.equals("derecha")) {
            game.moveIceCream(Direction.RIGHT);
            System.out.println("🍦 J1: → Moviendo derecha");
            return true;
        }

        // Acciones de hielo (ESPACIO: crear fila O romper 1 bloque)
        if (comando.equals("espacio") || comando.equals(" ")) {
            int result = game.toggleIceBlocks();
            if (result > 0) {
                System.out.println("❄️ Fila de hielo creada: " + result + " bloques");
            } else if (result == -1) {
                System.out.println("💥 Bloque de hielo roto");
            } else {
                System.out.println("❌ No se pudo crear/romper");
            }
            return true;
        }

        // Comando B ahora solo muestra ayuda (el juego original solo usa espacio)
        if (comando.equals("b") || comando.equals("romper")) {
            System.out.println("ℹ️  Usa ESPACIO para crear/romper bloques");
            System.out.println("   • Si no hay bloque enfrente: crea FILA");
            System.out.println("   • Si hay bloque enfrente: rompe UN bloque");
            return false;
        }

        // ========== JUGADOR 2 (Enemigos - Solo en PVP) ==========
        // ========== JUGADOR 2 (Monstruo - Solo en PVP) ==========
        if (game.getGameMode() == GameMode.PVP) {
            Direction dirEnemy = null;
            String dirName = "";

            // Comandos de teclas (i, k, j, l)
            if (comando.equals("i")) {
                dirEnemy = Direction.UP;
                dirName = "arriba (i)";
            } else if (comando.equals("k")) {
                dirEnemy = Direction.DOWN;
                dirName = "abajo (k)";
            } else if (comando.equals("j")) {
                dirEnemy = Direction.LEFT;
                dirName = "izquierda (j)";
            } else if (comando.equals("l")) {
                dirEnemy = Direction.RIGHT;
                dirName = "derecha (l)";
            }

            // Si se detectó una dirección, mover TODOS los enemigos
            if (dirEnemy != null) {
                int enemyIndex = 0;
                int movidosCount = 0;

                for (Enemy enemy : game.getBoard().getEnemies()) {
                    boolean moved = game.moveEnemy(enemyIndex, dirEnemy);
                    if (moved) {
                        movidosCount++;
                        Position pos = enemy.getPosition();
                        System.out.println("👹 J2 Monstruo " + (enemyIndex + 1) + " (" + enemy.getEnemyType() +
                                "): ← " + dirName + " → Ahora en [" + pos.getX() + ", " + pos.getY() + "]");
                    }
                    enemyIndex++;
                }

                if (movidosCount == 0) {
                    System.out.println("⚠️ Monstruos no pudieron moverse (obstáculo o borde)");
                }

                return true;
            }
        }

        // Control del juego
        if (comando.equals("p") || comando.equals("pausa")) {
            game.togglePause();
            if (game.getGameState() == GameState.PAUSED) {
                System.out.println("⏸️  JUEGO PAUSADO");
                System.out.print("Presiona Enter para continuar...");
                scanner.nextLine();
                game.togglePause();
                System.out.println("▶️  JUEGO REANUDADO");
            }
            return false; // No actualizar el juego
        }

        if (comando.equals("h") || comando.equals("ayuda")) {
            mostrarAyuda();
            return false;
        }

        if (comando.equals("q") || comando.equals("salir")) {
            System.out.println("\n👋 ¡Hasta luego!");
            running = false;
            return false;
        }

        // Comando no reconocido
        System.out.println("❓ Comando no reconocido. Escribe 'h' para ayuda.");
        return false;
    }

    /**
     * Muestra el resultado final del juego
     */
    private void mostrarResultadoFinal() {
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("                    FIN DEL JUEGO");
        System.out.println("=".repeat(60));

        if (game.getGameState() == GameState.WON) {
            System.out.println("\n          🎉🎉🎉 ¡VICTORIA! 🎉🎉🎉");
            System.out.println("\n¡Has completado el nivel exitosamente!");
        } else if (game.getGameState() == GameState.LOST) {
            System.out.println("\n             💀 GAME OVER 💀");
            System.out.println("\n¡Inténtalo de nuevo!");
        }

        System.out.println("\n" + "-".repeat(60));
        System.out.println("ESTADÍSTICAS FINALES:");
        System.out.println("  Puntuación: " + game.getScore());
        System.out.println("  Tiempo restante: " + game.getRemainingTime() + "s");
        System.out.println("  Nivel: " + game.getCurrentLevel().getLevelNumber());
        System.out.println("-".repeat(60));
        System.out.println("\n");
    }

    /**
     * Lee un entero del usuario con validación
     */
    private int leerEntero(int min, int max) {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine());
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.print("Número fuera de rango. Intenta de nuevo (" + min + "-" + max + "): ");
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Intenta de nuevo (" + min + "-" + max + "): ");
            }
        }
    }

    /**
     * Main - Punto de entrada para modo consola
     */
    public static void main(String[] args) {
        ConsoleGame consoleGame = new ConsoleGame();
        consoleGame.start();
    }
}
