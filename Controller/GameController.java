package Controller;

import Domain.*;
import Presentation.GamePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

    /**
     * Constructor del GameController
     * 
     * @param gameMode       Modo de juego (PVP, PVM, MVM)
     * @param iceCreamFlavor Sabor del helado elegido
     */
    public GameController(GameMode gameMode, String iceCreamFlavor) {
        // 1. Crear el Model (Game)
        this.game = new Game(gameMode, iceCreamFlavor);

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
     */
    private void setupGameTimer() {
        gameTimer = new Timer(FRAME_TIME, e -> {
            if (running) {
                // 1. Actualizar el Model (lógica del juego)
                game.update();

                // 2. Actualizar la View (redibujar)
                gamePanel.repaint();

                // 3. Verificar si terminó el juego
                checkGameEnd();
            }
        });
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

        // Pausar/Reanudar con P o ESC
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
            handlePauseToggle();
            return;
        }

        // No procesar teclas si está pausado
        if (game.getGameState() == GameState.PAUSED) {
            return;
        }

        // Movimiento del helado (solo en modos PVP y PVM)
        if (game.getGameMode() != GameMode.MVM) {
            handleMovement(keyCode);
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
     * Maneja el movimiento del helado
     */
    private void handleMovement(int keyCode) {
        Direction direction = null;

        // Flechas o WASD
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                direction = Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                direction = Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                direction = Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                direction = Direction.RIGHT;
                break;
        }

        // Si se detectó una dirección, mover
        if (direction != null) {
            boolean moved = game.moveIceCream(direction);

            // Debug (opcional)
            if (!moved) {
                System.out.println("Movimiento bloqueado: " + direction);
            }
        }
    }

    /**
     * Maneja acciones de bloques de hielo
     */
    private void handleIceBlockActions(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.VK_SPACE) {
            if (e.isShiftDown()) {
                // SHIFT + ESPACIO = Romper bloques
                int brokenBlocks = game.breakIceBlocks();
                if (brokenBlocks > 0) {
                    System.out.println("Bloques rotos: " + brokenBlocks);
                }
            } else {
                // ESPACIO = Crear bloque
                boolean created = game.createIceBlock();
                if (created) {
                    System.out.println("Bloque de hielo creado");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No se usa por ahora
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