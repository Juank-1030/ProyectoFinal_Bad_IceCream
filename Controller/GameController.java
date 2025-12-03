package Controller;

import Domain.*;
import Presentation.GamePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import Domain.GameState;

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

    // Callbacks
    private Runnable onReturnToMenuClick; // Callback para volver al menú

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

    // Sistema de timing para orientación vs movimiento
    private java.util.Map<Integer, Long> keyPressTime = new java.util.HashMap<>(); // Tiempo que se presionó cada tecla
    private static final long ORIENTATION_THRESHOLD = 100; // 0.10 segundos: si se suelta antes, solo orienta

    /**
     * Constructor del GameController
     * 
     * @param gameMode             Modo de juego (PVP, PVM, MVM)
     * @param iceCreamFlavor       Sabor del helado elegido
     * @param secondIceCreamFlavor Segundo sabor para modo cooperativo (puede ser
     *                             null)
     */
    private String monsterType; // Tipo de monstruo seleccionado
    private String secondIceCreamFlavor; // Segundo sabor para modo cooperativo

    public GameController(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType) {
        // 1. Crear el Model (Game)
        this.monsterType = monsterType;
        this.secondIceCreamFlavor = secondIceCreamFlavor;
        this.game = new Game(gameMode, iceCreamFlavor, secondIceCreamFlavor, monsterType);

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

    // Constructor sin secondIceCreamFlavor (retrocompatibilidad para Helado vs
    // Monstruo)
    public GameController(GameMode gameMode, String iceCreamFlavor, String monsterType) {
        this(gameMode, iceCreamFlavor, null, monsterType);
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
     * NUEVO SISTEMA: Si una tecla se presiona menos de 1 segundo, solo orienta
     * Si se presiona más de 1 segundo, se mueve
     */
    private void processInputs() {
        long currentTime = System.currentTimeMillis();

        // ===== HELADO 1 (WASD) =====
        processIceCreamInput(currentTime, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
                game.getBoard().getIceCream(), true);

        // ===== HELADO 2 O MONSTRUO (FLECHAS) =====
        if (secondIceCreamFlavor != null) {
            // Modo Cooperativo: Flechas controlan Helado 2
            processIceCreamInput(currentTime, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                    game.getBoard().getSecondIceCream(), false);
        } else if (game.getGameMode() == GameMode.PVP) {
            // Modo PVP: Flechas controlan Monstruo con mismo sistema de
            // orientación/movimiento
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
            if (!narvalCharging && !enemies.isEmpty()) {
                processEnemyInput(currentTime, enemies.get(0));
            }
        }
    }

    /**
     * Procesa la entrada para un helado específico
     * Determina si debe orientar (presión corta) o moverse (presión larga)
     * LÓGICA:
     * - Presión < 0.10 segundos: Solo orienta (cuando se suelta)
     * - Presión >= 0.10 segundos: Se mueve continuamente mientras esté presionada
     */
    private void processIceCreamInput(long currentTime, Direction upDir, Direction downDir, Direction leftDir,
            Direction rightDir, int upKey, int downKey, int leftKey, int rightKey, IceCream iceCream,
            boolean isFirstIceCream) {
        Direction directionToProcess = null;
        int keyPressed = -1;

        // Determinar qué tecla está presionada (por orden de prioridad)
        if (keysPressed.contains(upKey)) {
            directionToProcess = upDir;
            keyPressed = upKey;
        } else if (keysPressed.contains(downKey)) {
            directionToProcess = downDir;
            keyPressed = downKey;
        } else if (keysPressed.contains(leftKey)) {
            directionToProcess = leftDir;
            keyPressed = leftKey;
        } else if (keysPressed.contains(rightKey)) {
            directionToProcess = rightDir;
            keyPressed = rightKey;
        }

        if (directionToProcess != null && keyPressed != -1 && iceCream != null) {
            // Obtener el tiempo que se ha presionado esta tecla
            long pressTime = keyPressTime.getOrDefault(keyPressed, currentTime);
            long elapsedTime = currentTime - pressTime;

            // SIEMPRE actualizar la dirección
            iceCream.setCurrentDirection(directionToProcess);

            // Si se presionó >= 0.10 segundos, entonces MOVER
            if (elapsedTime >= ORIENTATION_THRESHOLD) {
                if (isFirstIceCream) {
                    game.moveIceCream(directionToProcess);
                } else {
                    game.moveSecondIceCream(directionToProcess);
                }
            }
            // Si es < 0.10 segundos, solo se orientó (ya lo hizo con setCurrentDirection)
        }
    }

    /**
     * Procesa la entrada para un monstruo específico (PVP)
     * Determina si debe orientar (presión corta) o moverse (presión larga)
     * LÓGICA:
     * - Presión < 0.10 segundos: Solo orienta el ataque (cuando se suelta)
     * - Presión >= 0.10 segundos: Se mueve continuamente mientras esté presionada
     */
    private void processEnemyInput(long currentTime, Enemy enemy) {
        if (enemy == null) {
            return;
        }

        Direction directionToProcess = null;
        int keyPressed = -1;

        // Determinar qué tecla está presionada (por orden de prioridad)
        if (keysPressed.contains(KeyEvent.VK_UP)) {
            directionToProcess = Direction.UP;
            keyPressed = KeyEvent.VK_UP;
        } else if (keysPressed.contains(KeyEvent.VK_DOWN)) {
            directionToProcess = Direction.DOWN;
            keyPressed = KeyEvent.VK_DOWN;
        } else if (keysPressed.contains(KeyEvent.VK_LEFT)) {
            directionToProcess = Direction.LEFT;
            keyPressed = KeyEvent.VK_LEFT;
        } else if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
            directionToProcess = Direction.RIGHT;
            keyPressed = KeyEvent.VK_RIGHT;
        }

        if (directionToProcess != null && keyPressed != -1) {
            // Obtener el tiempo que se ha presionado esta tecla
            long pressTime = keyPressTime.getOrDefault(keyPressed, currentTime);
            long elapsedTime = currentTime - pressTime;

            // SIEMPRE actualizar la dirección del monstruo
            enemy.setCurrentDirection(directionToProcess);

            // Si se presionó >= 0.10 segundos, entonces MOVER
            if (elapsedTime >= ORIENTATION_THRESHOLD) {
                game.moveEnemy(0, directionToProcess);
            }
            // Si es < 0.10 segundos, solo se orientó (ya lo hizo con setCurrentDirection)
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
     * Reanuda el juego (método público para llamarse desde listeners)
     */
    public void resume() {
        resumeGame();
    }

    /**
     * Regresa al menú principal
     */
    private void returnToMenu() {
        pauseGame();
        System.out.println("✅ Regresando al menú...");
        if (onReturnToMenuClick != null) {
            onReturnToMenuClick.run();
        }
    }

    // ========================================
    // IMPLEMENTACIÓN DE KeyListener
    // ========================================

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Registrar el tiempo cuando se presiona la tecla (para sistema de
        // orientación/movimiento)
        if (!keyPressTime.containsKey(keyCode)) {
            keyPressTime.put(keyCode, System.currentTimeMillis());
        }

        keysPressed.add(keyCode); // Agregar tecla al Set para input buffering

        // Pausar/Reanudar con P o ESC
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
            handlePauseToggle();
            return;
        }

        // Si está pausado, permitir volver al menú con M
        if (game.getGameState() == GameState.PAUSED) {
            if (keyCode == KeyEvent.VK_M) {
                // Volver al menú
                if (onReturnToMenuClick != null) {
                    onReturnToMenuClick.run();
                }
            }
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
     * Muestra mensaje de pausa con opciones de Guardar/Cargar
     */
    private void showPauseMessage() {
        String[] opciones = {
                "Continuar jugando",
                "Guardar Partida",
                "Cargar Partida",
                "Salir al menú"
        };

        int opcion = JOptionPane.showOptionDialog(
                gamePanel,
                "JUEGO PAUSADO\n\n¿Qué deseas hacer?",
                "Pausa",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        switch (opcion) {
            case 0: // Continuar jugando
                game.togglePause();
                resumeGame();
                break;

            case 1: // Guardar Partida
                handleSaveGame();
                break;

            case 2: // Cargar Partida
                handleLoadGame();
                break;

            case 3: // Salir al menú
                returnToMenu();
                break;

            default: // Si cierra el diálogo (X)
                game.togglePause();
                resumeGame();
                break;
        }
    }

    /**
     * Maneja el guardado de partida
     */
    private void handleSaveGame() {
        String nombreArchivo = JOptionPane.showInputDialog(
                gamePanel,
                "Ingresa un nombre para la partida:",
                "Guardar Partida",
                JOptionPane.QUESTION_MESSAGE);

        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            boolean guardado = game.saveGame(nombreArchivo.trim());

            if (guardado) {
                JOptionPane.showMessageDialog(
                        gamePanel,
                        "¡Partida guardada exitosamente!\n" +
                                "Archivo: saves/" + nombreArchivo.trim() + ".dat",
                        "Guardado exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        gamePanel,
                        "Error al guardar la partida.\nIntenta con otro nombre.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Volver a mostrar el menú de pausa
        showPauseMessage();
    }

    /**
     * Maneja la carga de partida
     */
    private void handleLoadGame() {
        String[] partidasGuardadas = Game.listSavedGames();

        if (partidasGuardadas == null || partidasGuardadas.length == 0) {
            JOptionPane.showMessageDialog(
                    gamePanel,
                    "No hay partidas guardadas.\n¡Guarda una partida primero!",
                    "Sin partidas",
                    JOptionPane.INFORMATION_MESSAGE);
            showPauseMessage();
            return;
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                gamePanel,
                "Selecciona la partida a cargar:",
                "Cargar Partida",
                JOptionPane.QUESTION_MESSAGE,
                null,
                partidasGuardadas,
                partidasGuardadas[0]);

        if (seleccion == null) {
            // Usuario canceló
            showPauseMessage();
            return;
        }

        Game partidaCargada = Game.loadGame(seleccion);

        if (partidaCargada != null) {
            // Detener el juego actual
            stopGame();

            // Reemplazar el juego con el cargado
            this.game = partidaCargada;

            // Reiniciar el timer y actualizar vista
            setupGameTimer();
            gamePanel.repaint();

            JOptionPane.showMessageDialog(
                    gamePanel,
                    "¡Partida cargada exitosamente!\n" +
                            "Nivel: " + partidaCargada.getCurrentLevel().getLevelNumber() + "\n" +
                            "Puntos: " + partidaCargada.getScore(),
                    "Carga exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            // Reanudar el juego cargado
            game.setGameState(GameState.PLAYING);
            resumeGame();
        } else {
            JOptionPane.showMessageDialog(
                    gamePanel,
                    "Error al cargar la partida.\nEl archivo puede estar corrupto.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            showPauseMessage();
        }
    }

    /**
     * Maneja acciones de bloques de hielo y habilidades
     * 
     * MODO COOPERATIVO (Dos Helados):
     * - P1 (Helado 1): WASD + Q para crear/romper bloques
     * - P2 (Helado 2): FLECHAS + ESPACIO para crear/romper bloques
     * 
     * MODO PVP (Helado vs Monstruo):
     * - Helado (WASD): Q para crear/romper bloques
     * - Monstruo (Flechas): ESPACIO para activar habilidades
     * 
     * MODO PVM (Helado vs IA):
     * - Helado (WASD): Q para crear/romper bloques
     */
    private void handleIceBlockActions(int keyCode, KeyEvent e) {
        // Tecla Q: Para P1 (Helado 1) en todos los modos
        if (keyCode == KeyEvent.VK_Q) {
            // Actualizar la dirección del helado según la última tecla presionada
            if (keysPressed.contains(KeyEvent.VK_W)) {
                game.getBoard().getIceCream().setCurrentDirection(Direction.UP);
            } else if (keysPressed.contains(KeyEvent.VK_S)) {
                game.getBoard().getIceCream().setCurrentDirection(Direction.DOWN);
            } else if (keysPressed.contains(KeyEvent.VK_A)) {
                game.getBoard().getIceCream().setCurrentDirection(Direction.LEFT);
            } else if (keysPressed.contains(KeyEvent.VK_D)) {
                game.getBoard().getIceCream().setCurrentDirection(Direction.RIGHT);
            }

            int result = game.toggleIceBlocks();
            if (result > 0) {
                if (secondIceCreamFlavor != null) {
                    System.out.println("✓ (P1) Hilera de " + result + " bloque(s) de hielo creada");
                } else {
                    System.out.println("✓ Hilera de " + result + " bloque(s) de hielo creada");
                }
            } else if (result < 0) {
                if (secondIceCreamFlavor != null) {
                    System.out.println("✓ (P1) Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                } else {
                    System.out.println("✓ Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                }
            }
            return;
        }

        // Tecla ESPACIO: Diferentes acciones según el modo
        if (keyCode == KeyEvent.VK_SPACE) {
            // En modo Cooperativo: ESPACIO controla helado 2 (P2)
            if (secondIceCreamFlavor != null) {
                // Actualizar la dirección del helado 2 según la última tecla de flecha
                // presionada
                if (keysPressed.contains(KeyEvent.VK_UP)) {
                    game.getBoard().getSecondIceCream().setCurrentDirection(Direction.UP);
                } else if (keysPressed.contains(KeyEvent.VK_DOWN)) {
                    game.getBoard().getSecondIceCream().setCurrentDirection(Direction.DOWN);
                } else if (keysPressed.contains(KeyEvent.VK_LEFT)) {
                    game.getBoard().getSecondIceCream().setCurrentDirection(Direction.LEFT);
                } else if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
                    game.getBoard().getSecondIceCream().setCurrentDirection(Direction.RIGHT);
                }

                int result = game.toggleIceBlocksSecond();
                if (result > 0) {
                    System.out.println("✓ (P2) Hilera de " + result + " bloque(s) de hielo creada");
                } else if (result < 0) {
                    System.out.println("✓ (P2) Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                }
                return;
            }

            // En PVP: ESPACIO controla al monstruo (habilidades)
            if (game.getGameMode() == GameMode.PVP) {
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
                // En PVM/MVM: ESPACIO no hace nada en estos modos (solo Q controla hielo)
                // Esto es solo para monstruo en PVP
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.remove(keyCode); // Remover tecla del Set cuando se suelta
        keyPressTime.remove(keyCode); // Limpiar el registro de tiempo de presión
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

    /**
     * Establece el callback para volver al menú
     */
    public void setOnReturnToMenuClick(Runnable callback) {
        this.onReturnToMenuClick = callback; // Guardar referencia directa
        gamePanel.setOnReturnToMenuClick(callback);
    }

    /**
     * Establece el callback para guardar la partida desde el overlay de pausa
     */
    public void setOnSaveGameClick(Runnable callback) {
        gamePanel.setOnSaveGameClick(callback);
    }

    /**
     * Establece el callback para continuar la partida desde el overlay de pausa
     */
    public void setOnContinueGameClick(Runnable callback) {
        gamePanel.setOnContinueGameClick(callback);
    }
}
