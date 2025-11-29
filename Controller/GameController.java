package Controller;

import Domain.*;
import Presentation.GamePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * GameController - Controlador del juego
 * 
 * RESPONSABILIDADES:
 * - Capturar eventos del teclado
 * - Traducir eventos a comandos del juego
 * - Coordinar Model (Game) con View (GamePanel)
 * - Manejar el game loop (Timer)
 * 
 * NO TIENE:
 * - Lógica del juego (eso está en Game)
 * - Reglas (eso está en Game)
 * - Renderizado (eso está en GamePanel)
 */
public class GameController implements KeyListener {

    // Referencias al Model y View
    private Game game;
    private GamePanel gamePanel;
    private Timer gameTimer;

    // Control del juego
    private boolean running;
    private static final int FPS = 60;
    private static final int FRAME_TIME = 1000 / FPS; // 16ms por frame

    // Sistema de teclas presionadas actualmente (Input Buffering Pattern)
    private java.util.Set<Integer> keysPressed = new java.util.HashSet<>();

    // Control de orientación/movimiento: primer click = orientación, clicks
    // subsecuentes = movimiento
    private Direction lastIceCreamDirection = null; // Última dirección establecida del helado
    private Direction lastEnemyDirection = null; // Última dirección establecida del enemigo (PVP)
    private long lastDirectionChangeTime = 0; // Momento del último cambio de dirección
    private static final long DIRECTION_TIMEOUT = 200; // 200ms para cambiar de dirección sin mover

    /**
     * Constructor del GameController
     * 
     * @param gameMode       Modo de juego (PVP, PVM, MVM)
     * @param iceCreamFlavor Sabor del helado elegido
     */
    private String monsterType; // Tipo de monstruo seleccionado

    public GameController(GameMode gameMode, String iceCreamFlavor, String monsterType) {
        // 1. Crear el Model (Game)
        this.monsterType = monsterType;
        this.game = new Game(gameMode, iceCreamFlavor, monsterType);

        // 2. Crear la View (GamePanel)
        this.gamePanel = new GamePanel(this);

        // 3. Configurar listeners
        gamePanel.addKeyListener(this);
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        // 4. Inicializar timer (pero no iniciarlo aún)
        this.running = false;
        setupGameTimer();
    }

    /**
     * Configura el Timer que ejecuta el game loop
     * Implementa el patrón Game Loop con sincronización en tiempo real
     */
    private void setupGameTimer() {
        gameTimer = new Timer(FRAME_TIME, e -> {
            if (running) {
                // 1. Procesar entradas de teclado presionadas actualmente
                processInputs();

                // 2. Actualizar el Model (lógica del juego)
                game.update();

                // 3. Actualizar la View (redibujar)
                gamePanel.repaint();

                // 4. Verificar si terminó el juego
                checkGameEnd();
            }
        });
    }

    /**
     * Procesa todas las teclas presionadas actualmente
     * Esto permite movimiento en tiempo real sin esperar a keyPressed/keyReleased
     */
    private void processInputs() {
        // Procesar movimientos de WASD (Helado)
        if (keysPressed.contains(KeyEvent.VK_W)) {
            game.moveIceCream(Direction.UP);
        } else if (keysPressed.contains(KeyEvent.VK_S)) {
            game.moveIceCream(Direction.DOWN);
        } else if (keysPressed.contains(KeyEvent.VK_A)) {
            game.moveIceCream(Direction.LEFT);
        } else if (keysPressed.contains(KeyEvent.VK_D)) {
            game.moveIceCream(Direction.RIGHT);
        }

        // Procesar movimientos de Flechas (Monstruos en PVP)
        // EXCEPTO si el Narval está en modo carga (se mueve automáticamente)
        if (game.getGameMode() == GameMode.PVP) {
            List<Enemy> enemies = game.getBoard().getEnemies();
            boolean narvalCharging = false;

            // Verificar si el Narval está cargando
            for (Enemy enemy : enemies) {
                if (enemy instanceof Narval) {
                    Narval narval = (Narval) enemy;
                    if (narval.isCharging()) {
                        narvalCharging = true;
                        break;
                    }
                }
            }

            // Solo procesar entrada de flechas si el Narval NO está cargando
            if (!narvalCharging) {
                if (keysPressed.contains(KeyEvent.VK_UP)) {
                    game.moveEnemy(0, Direction.UP);
                } else if (keysPressed.contains(KeyEvent.VK_DOWN)) {
                    game.moveEnemy(0, Direction.DOWN);
                } else if (keysPressed.contains(KeyEvent.VK_LEFT)) {
                    game.moveEnemy(0, Direction.LEFT);
                } else if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
                    game.moveEnemy(0, Direction.RIGHT);
                }
            }
        }
    }

    /**
     * Inicia el juego en un nivel específico
     * 
     * @param levelNumber Número del nivel (1, 2, 3)
     */
    public void startLevel(int levelNumber) {
        try {
            // Intentar inicializar niveles si no existen
            if (RecursosNivel.listarNivelesDisponibles().isEmpty()) {
                System.out.println("Creando niveles predefinidos...");
                RecursosNivel.crearNivelesPredefinidos();
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar recursos: " + e.getMessage());
        }

        // Iniciar el nivel en el Model
        game.startLevel(levelNumber);

        // Iniciar el game loop
        running = true;
        gameTimer.start();

        // Asegurar que el panel tenga el foco para capturar teclas
        gamePanel.requestFocusInWindow();

        System.out.println("Nivel " + levelNumber + " iniciado");
        System.out.println("Modo: " + game.getGameMode());
        System.out.println("Helado: " + game.getIceCreamFlavor());
    }

    /**
     * Verifica si el juego terminó y maneja el final
     */
    private void checkGameEnd() {
        GameState state = game.getGameState();

        if (state == GameState.WON) {
            pauseGame();
            showVictoryMessage();
        } else if (state == GameState.LOST) {
            pauseGame();
            showGameOverMessage();
        }
    }

    /**
     * Muestra mensaje de victoria
     */
    private void showVictoryMessage() {
        int option = JOptionPane.showConfirmDialog(
                gamePanel,
                "¡VICTORIA!\n" +
                        "Puntuación: " + game.getScore() + "\n" +
                        "Tiempo restante: " + game.getRemainingTime() + "s\n\n" +
                        "¿Continuar al siguiente nivel?",
                "¡Ganaste!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Cargar siguiente nivel
            int nextLevel = game.getCurrentLevel().getLevelNumber() + 1;
            if (nextLevel <= 3) {
                startLevel(nextLevel);
            } else {
                showGameCompleteMessage();
            }
        } else {
            returnToMenu();
        }
    }

    /**
     * Muestra mensaje de derrota
     */
    private void showGameOverMessage() {
        int option = JOptionPane.showConfirmDialog(
                gamePanel,
                "GAME OVER\n" +
                        "Puntuación final: " + game.getScore() + "\n\n" +
                        "¿Reintentar?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Reiniciar nivel actual
            int currentLevel = game.getCurrentLevel().getLevelNumber();
            startLevel(currentLevel);
        } else {
            returnToMenu();
        }
    }

    /**
     * Muestra mensaje de juego completado
     */
    private void showGameCompleteMessage() {
        JOptionPane.showMessageDialog(
                gamePanel,
                "¡FELICIDADES!\n" +
                        "Has completado todos los niveles\n" +
                        "Puntuación final: " + game.getScore(),
                "Juego Completado",
                JOptionPane.INFORMATION_MESSAGE);
        returnToMenu();
    }

    /**
     * Pausa el juego
     */
    private void pauseGame() {
        running = false;
        gameTimer.stop();
    }

    /**
     * Reanuda el juego
     */
    private void resumeGame() {
        running = true;
        gameTimer.start();
        gamePanel.requestFocusInWindow();
    }

    /**
     * Regresa al menú principal
     */
    private void returnToMenu() {
        pauseGame();
        // Aquí se llamaría a PresentationController para cambiar de vista
        System.out.println("Regresando al menú...");
        // TODO: Implementar navegación al menú
    }

    // ========================================
    // IMPLEMENTACIÓN DE KeyListener
    // ========================================

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.add(keyCode); // Agregar tecla al Set para input buffering

        // Pausar/Reanudar con P o ESC
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
            handlePauseToggle();
            return;
        }

        // No procesar teclas si está pausado
        if (game.getGameState() == GameState.PAUSED) {
            return;
        }

        // Solo procesar acciones de bloques de hielo y habilidades aquí
        // El movimiento es procesado por processInputs() en cada frame del game loop
        if (game.getGameMode() != GameMode.MVM) {
            handleIceBlockActions(keyCode, e);
        }
    }

    /**
     * Maneja el toggle de pausa
     */
    private void handlePauseToggle() {
        game.togglePause();

        if (game.getGameState() == GameState.PAUSED) {
            pauseGame();
            showPauseMessage();
        } else if (game.getGameState() == GameState.PLAYING) {
            resumeGame();
        }
    }

    /**
     * Muestra mensaje de pausa
     */
    private void showPauseMessage() {
        int option = JOptionPane.showConfirmDialog(
                gamePanel,
                "JUEGO PAUSADO\n\n" +
                        "¿Continuar jugando?",
                "Pausa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            game.togglePause(); // Reanudar
            resumeGame();
        } else {
            returnToMenu();
        }
    }

    /**
     * Maneja el movimiento del helado y monstruos con sistema de orientación
     * SISTEMA DE CONTROLES:
     * - Primer click en dirección: establece orientación del personaje
     * - Clicks subsecuentes en MISMA dirección: mueve el personaje
     * - Click en DIFERENTE dirección: cambia orientación (sin mover)
     * 
     * En PVP: WASD para helado (jugador 1), Flechas para monstruos (jugador 2)
     * En PVM: WASD para helado, Flechas para IA del monstruo (ignoradas)
     */
    private void handleMovement(int keyCode) {
        Direction direction = null;
        boolean isWASD = false;
        boolean isArrow = false;

        // Detectar si es WASD (helado - Jugador 1)
        switch (keyCode) {
            case KeyEvent.VK_W:
                direction = Direction.UP;
                isWASD = true;
                break;
            case KeyEvent.VK_S:
                direction = Direction.DOWN;
                isWASD = true;
                break;
            case KeyEvent.VK_A:
                direction = Direction.LEFT;
                isWASD = true;
                break;
            case KeyEvent.VK_D:
                direction = Direction.RIGHT;
                isWASD = true;
                break;
        }

        // Procesar WASD (Helado/Jugador 1)
        if (direction != null && isWASD) {
            // Verificar si es la misma dirección
            if (direction == lastIceCreamDirection) {
                // MISMA dirección → MOVER el helado
                boolean moved = game.moveIceCream(direction);
                if (!moved) {
                    System.out.println("→ Helado no puede avanzar: " + direction);
                }
            } else {
                // DIFERENTE dirección → ORIENTAR el helado (sin mover)
                lastIceCreamDirection = direction;
                game.getBoard().getIceCream().setCurrentDirection(direction);
                lastDirectionChangeTime = System.currentTimeMillis();
                System.out.println("→ Helado orientado a: " + direction);
            }
            return;
        }

        // Detectar si es flecha (monstruos/Jugador 2 - solo en PVP)
        direction = null;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                direction = Direction.UP;
                isArrow = true;
                break;
            case KeyEvent.VK_DOWN:
                direction = Direction.DOWN;
                isArrow = true;
                break;
            case KeyEvent.VK_LEFT:
                direction = Direction.LEFT;
                isArrow = true;
                break;
            case KeyEvent.VK_RIGHT:
                direction = Direction.RIGHT;
                isArrow = true;
                break;
        }

        // Procesar Flechas (Monstruo/Jugador 2 - solo en PVP)
        if (direction != null && isArrow && game.getGameMode() == GameMode.PVP) {
            // Verificar si es la misma dirección
            if (direction == lastEnemyDirection) {
                // MISMA dirección → MOVER el monstruo
                boolean moved = game.moveEnemy(0, direction);
                if (!moved) {
                    System.out.println("→ Monstruo no puede avanzar: " + direction);
                }
            } else {
                // DIFERENTE dirección → ORIENTAR el monstruo (sin mover)
                lastEnemyDirection = direction;
                List<Enemy> enemies = game.getBoard().getEnemies();
                if (!enemies.isEmpty()) {
                    enemies.get(0).setCurrentDirection(direction);
                    lastDirectionChangeTime = System.currentTimeMillis();
                    System.out.println("→ Monstruo orientado a: " + direction);
                }
            }
        }
    }

    /**
     * Maneja acciones de bloques de hielo y habilidades
     * 
     * HELADO (WASD): Tecla Q o ESPACIO para crear/romper bloques
     * MONSTRUOS (Flechas): Tecla ESPACIO para activar habilidades
     */
    private void handleIceBlockActions(int keyCode, KeyEvent e) {
        // Tecla Q: Para helado (crear/romper bloques de hielo)
        if (keyCode == KeyEvent.VK_Q) {
            int result = game.toggleIceBlocks();

            if (result > 0) {
                // Se crearon bloques
                System.out.println("✓ Hilera de " + result + " bloque(s) de hielo creada");
            } else if (result < 0) {
                // Se rompieron bloques
                System.out.println("✓ Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
            }
            return;
        }

        // Tecla ESPACIO: Para helado O monstruos (PVP)
        if (keyCode == KeyEvent.VK_SPACE) {
            // En PVP: determinar quién controla
            if (game.getGameMode() == GameMode.PVP) {
                // El ESPACIO controla al monstruo (habilidades)
                List<Enemy> enemies = game.getBoard().getEnemies();
                if (!enemies.isEmpty()) {
                    Enemy controlledEnemy = enemies.get(0);

                    // Activar habilidad según el tipo de enemigo
                    if (controlledEnemy instanceof Pot) {
                        Pot pot = (Pot) controlledEnemy;
                        if (pot.isTurboActive()) {
                            System.out
                                    .println("⚡ Turbo activo: " + (pot.getTurboTimeRemaining() / 1000) + "s restantes");
                        } else if (pot.getTurboRechargeTimeRemaining() <= 0) {
                            pot.executeAbility(); // Activar turbo manualmente
                            System.out.println("⚡ ¡Turbo ACTIVADO!");
                        } else {
                            System.out.println(
                                    "⏳ Turbo en recarga: " + (pot.getTurboRechargeTimeRemaining() / 1000) + "s");
                        }
                    } else if (controlledEnemy instanceof Narval) {
                        Narval narval = (Narval) controlledEnemy;
                        if (narval.canCharge()) {
                            narval.activateCharge(controlledEnemy.getCurrentDirection());
                            System.out.println("🔱 ¡Carga de Narval ACTIVADA!");
                        } else {
                            System.out.println(
                                    "⏳ Carga en recarga: " + (narval.getChargeRechargeTimeRemaining() / 1000) + "s");
                        }
                    } else if (controlledEnemy instanceof YellowSquid) {
                        YellowSquid squid = (YellowSquid) controlledEnemy;

                        // Intentar romper hielo en la dirección apuntada
                        boolean broken = game.getBoard().yellowSquidBreakIce(squid);

                        if (broken) {
                            System.out.println("🟡 ¡Bloque roto!");
                        } else {
                            // Mostrar progreso si no hay bloque o si sigue contando
                            Position targetPos = squid.getPosition().move(squid.getCurrentDirection());
                            if (game.getBoard().hasIceBlock(targetPos)) {
                                System.out.println("🟡 Golpe " + squid.getIceBreakCounter() + "/3");
                            } else {
                                System.out.println("⚠️ No hay bloque en esa dirección");
                                squid.resetIceBreakCounter(); // Resetear si no hay bloque
                            }
                        }
                    }
                }
            } else {
                // En PVM/MVM: El ESPACIO controla al helado (crear/romper bloques)
                int result = game.toggleIceBlocks();

                if (result > 0) {
                    // Se crearon bloques
                    System.out.println("✓ Hilera de " + result + " bloque(s) de hielo creada");
                } else if (result < 0) {
                    // Se rompieron bloques
                    System.out.println("✓ Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.remove(keyCode); // Remover tecla del Set cuando se suelta
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa por ahora
    }

    // ========================================
    // GETTERS
    // ========================================

    /**
     * Obtiene el juego (Model)
     */
    public Game getGame() {
        return game;
    }

    /**
     * Obtiene el panel de juego (View)
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Verifica si el juego está corriendo
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Detiene completamente el juego
     */
    public void stopGame() {
        running = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
}