package Controller;

import Domain.*;
import Presentation.GamePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Map;
import Domain.GameState;

/**
 * GameController - Orquestador del juego (Model-View-Controller)
 * 
 * PROPÓSITO:
 * Coordinar la interacción entre el Model (lógica del juego) y la View
 * (interfaz gráfica)
 * 
 * FUNCIÓN:
 * - Consulta InputHandler para saber qué acciones intenta el jugador
 * - Traduce esas acciones en comandos al Model (Game)
 * - Ejecuta el game loop (actualiza lógica + renderiza)
 * - Maneja transiciones entre pantallas y estados del juego
 * - Mantiene callbacks para eventos importantes (pausa, victoria, etc.)
 * 
 * NO TIENE:
 * - Lógica del juego (eso está en Game/Domain)
 * - Reglas de físicas o movimiento (eso está en Game/Domain)
 * - Renderizado gráfico (eso está en GamePanel)
 */
public class GameController implements KeyListener {

    // Referencias al Model y View
    private Game game;
    private GamePanel gamePanel;
    private Timer gameTimer;

    // Callbacks
    private Runnable onReturnToMenuClick; // Callback para volver al menú
    private java.util.function.Consumer<Integer> onLevelComplete; // Callback cuando se completa un nivel (recibe número
                                                                  // de siguiente nivel)
    private java.util.function.Consumer<Integer> onLevelFailed; // Callback cuando se falla un nivel (recibe número del
                                                                // nivel anterior)

    // Control del juego
    private boolean running;
    private static final int FPS = 60;
    private static final int FRAME_TIME = 1000 / FPS; // 16ms por frame

    // Estrategia de IA para el helado
    private String iceCreamAIStrategy; // Nombre de la estrategia de IA

    // InputHandler - Maneja captura de teclas (NEW - Separación de
    // responsabilidades)
    private InputHandler inputHandler;

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
    private Map<String, Integer> enemyConfig; // Configuración personalizada de enemigos
    private Map<String, Integer> fruitConfig; // Configuración personalizada de frutas

    public GameController(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType) {
        this(gameMode, iceCreamFlavor, secondIceCreamFlavor, monsterType, null, null);
    }

    public GameController(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType,
            Map<String, Integer> enemyConfig) {
        this(gameMode, iceCreamFlavor, secondIceCreamFlavor, monsterType, enemyConfig, null);
    }

    public GameController(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType,
            Map<String, Integer> enemyConfig, Map<String, Integer> fruitConfig, Map<String, Integer> obstacleConfig) {
        // 1. Crear el Model (Game)
        this.monsterType = monsterType;
        this.secondIceCreamFlavor = secondIceCreamFlavor;
        this.enemyConfig = enemyConfig;
        this.fruitConfig = fruitConfig;
        this.game = new Game(gameMode, iceCreamFlavor, secondIceCreamFlavor, monsterType, enemyConfig, fruitConfig, obstacleConfig);

        // 2. Crear la View (GamePanel)
        this.gamePanel = new GamePanel(this);

        // 3. Crear InputHandler (NEW - Manejo de eventos separado)
        this.inputHandler = new InputHandler();

        // 4. Configurar listeners
        gamePanel.addKeyListener(inputHandler);
        gamePanel.addKeyListener(this); // GameController aún implementa KeyListener para retrocompatibilidad
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        // 4. Inicializar timer (pero no iniciarlo aún)
        this.running = false;
        setupGameTimer();
    }

    public GameController(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType,
            Map<String, Integer> enemyConfig, Map<String, Integer> fruitConfig) {
        this(gameMode, iceCreamFlavor, secondIceCreamFlavor, monsterType, enemyConfig, fruitConfig, null);
    }

    // Constructor sin secondIceCreamFlavor (retrocompatibilidad para Helado vs
    // Monstruo)
    public GameController(GameMode gameMode, String iceCreamFlavor, String monsterType) {
        this(gameMode, iceCreamFlavor, null, monsterType, null);
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
        // Solo procesar entrada si el helado NO está bajo control de IA
        IceCream iceCream = game.getBoard().getIceCream();
        if (iceCream != null && !iceCream.isAIControlled()) {
            processIceCreamInput(currentTime, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
                    iceCream, true);
        }

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
     * ACTUALIZADO: Cambia el sprite según se mueva o esté quieto
     */
    private void processIceCreamInput(long currentTime, Direction upDir, Direction downDir, Direction leftDir,
            Direction rightDir, int upKey, int downKey, int leftKey, int rightKey, IceCream iceCream,
            boolean isFirstIceCream) {
        Direction directionToProcess = null;
        int keyPressed = -1;

        // Determinar qué tecla está presionada (por orden de prioridad)
        if (inputHandler.isKeyPressed(upKey)) {
            directionToProcess = upDir;
            keyPressed = upKey;
        } else if (inputHandler.isKeyPressed(downKey)) {
            directionToProcess = downDir;
            keyPressed = downKey;
        } else if (inputHandler.isKeyPressed(leftKey)) {
            directionToProcess = leftDir;
            keyPressed = leftKey;
        } else if (inputHandler.isKeyPressed(rightKey)) {
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
                // NUEVO: Cambiar a acción "walk"
                iceCream.setCurrentAction("walk");

                if (isFirstIceCream) {
                    game.moveIceCream(directionToProcess);
                } else {
                    game.moveSecondIceCream(directionToProcess);
                }
            } else {
                // NUEVO: Si solo se orientó, mantener "stand"
                if (!iceCream.getCurrentAction().equals("shoot") &&
                        !iceCream.getCurrentAction().equals("break")) {
                    iceCream.setCurrentAction("stand");
                }
            }
        } else if (iceCream != null) {
            // NUEVO: No hay teclas presionadas, volver a "stand"
            if (!iceCream.getCurrentAction().equals("shoot") &&
                    !iceCream.getCurrentAction().equals("break")) {
                iceCream.setCurrentAction("stand");
            }
        }
    }

    /**
     * Procesa la entrada para un monstruo específico (PVP)
     * ACTUALIZADO: Cambia el sprite según se mueva o esté quieto
     */
    private void processEnemyInput(long currentTime, Enemy enemy) {
        if (enemy == null) {
            return;
        }

        Direction directionToProcess = null;
        int keyPressed = -1;

        // Determinar qué tecla está presionada (por orden de prioridad)
        if (inputHandler.isKeyPressed(KeyEvent.VK_UP)) {
            directionToProcess = Direction.UP;
            keyPressed = KeyEvent.VK_UP;
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_DOWN)) {
            directionToProcess = Direction.DOWN;
            keyPressed = KeyEvent.VK_DOWN;
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_LEFT)) {
            directionToProcess = Direction.LEFT;
            keyPressed = KeyEvent.VK_LEFT;
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_RIGHT)) {
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
                // NUEVO: Cambiar a acción "walk"
                enemy.setCurrentAction("walk");
                game.moveEnemy(0, directionToProcess);
            } else {
                // NUEVO: Solo se orientó, mantener "stand"
                enemy.setCurrentAction("stand");
            }
        } else {
            // NUEVO: No hay teclas presionadas, volver a "stand"
            enemy.setCurrentAction("stand");
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

        // RESETEAR EL ESTADO DE LAS TECLAS PRESIONADAS
        inputHandler.clearAllKeys();
        keyPressTime.clear();
        lastIceCreamDirection = null;
        lastEnemyDirection = null;
        lastDirectionChangeTime = 0;

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
                // Llamar al callback si está configurado
                if (onLevelComplete != null) {
                    onLevelComplete.accept(nextLevel);
                } else {
                    // Fallback: iniciar directamente el nivel
                    startLevel(nextLevel);
                }
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
            // Volver al nivel 1 siempre que se pierda en cualquier nivel
            if (onLevelFailed != null) {
                // Volver siempre al nivel 1
                onLevelFailed.accept(1);
            } else {
                // Volver al menú si no hay callback
                returnToMenu();
            }
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

        // RESETEAR EL ESTADO DE LAS TECLAS PRESIONADAS AL PAUSAR
        inputHandler.clearAllKeys();
        keyPressTime.clear();
        lastIceCreamDirection = null;
        lastEnemyDirection = null;
        lastDirectionChangeTime = 0;
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
        System.out.println("✅ Regresando al menú.. .");
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

        inputHandler.keyPressed(e); // Delegar a InputHandler

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
            // NO mostrar el diálogo de pausa, usar el overlay visual
            // showPauseMessage(); // ← COMENTADO
            gamePanel.repaint(); // Forzar redibujado del overlay
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
     * ACTUALIZADO: Cambia la acción del sprite según la tecla presionada
     */
    private void handleIceBlockActions(int keyCode, KeyEvent e) {
        IceCream iceCream = game.getBoard().getIceCream();
        IceCream secondIceCream = game.getBoard().getSecondIceCream();

        // Tecla Q: Para P1 (Helado 1) en todos los modos
        if (keyCode == KeyEvent.VK_Q) {
            if (iceCream == null)
                return;

            // Actualizar la dirección del helado según la última tecla presionada
            if (inputHandler.isKeyPressed(KeyEvent.VK_W)) {
                iceCream.setCurrentDirection(Direction.UP);
            } else if (inputHandler.isKeyPressed(KeyEvent.VK_S)) {
                iceCream.setCurrentDirection(Direction.DOWN);
            } else if (inputHandler.isKeyPressed(KeyEvent.VK_A)) {
                iceCream.setCurrentDirection(Direction.LEFT);
            } else if (inputHandler.isKeyPressed(KeyEvent.VK_D)) {
                iceCream.setCurrentDirection(Direction.RIGHT);
            }

            int result = game.toggleIceBlocks();

            // NUEVO: Cambiar acción según si creó o rompió
            if (result > 0) {
                // Creó bloques de hielo
                iceCream.setCurrentAction("shoot");
                if (secondIceCreamFlavor != null) {
                    System.out.println("✓ (P1) Hilera de " + result + " bloque(s) de hielo creada");
                } else {
                    System.out.println("✓ Hilera de " + result + " bloque(s) de hielo creada");
                }

                // Volver a "stand" después de un delay
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        iceCream.setCurrentAction("stand");
                    }
                }, 200); // 200ms delay

            } else if (result < 0) {
                // Rompió bloques de hielo
                iceCream.setCurrentAction("break");
                if (secondIceCreamFlavor != null) {
                    System.out.println("✓ (P1) Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                } else {
                    System.out.println("✓ Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");
                }

                // Volver a "stand" después de un delay
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        iceCream.setCurrentAction("stand");
                    }
                }, 200);
            }
            return;
        }

        // Tecla ESPACIO: Diferentes acciones según el modo
        if (keyCode == KeyEvent.VK_SPACE) {
            // En modo Cooperativo: ESPACIO controla helado 2 (P2)
            if (secondIceCreamFlavor != null && secondIceCream != null) {
                // Actualizar la dirección del helado 2 según la última tecla de flecha
                // presionada
                if (inputHandler.isKeyPressed(KeyEvent.VK_UP)) {
                    secondIceCream.setCurrentDirection(Direction.UP);
                } else if (inputHandler.isKeyPressed(KeyEvent.VK_DOWN)) {
                    secondIceCream.setCurrentDirection(Direction.DOWN);
                } else if (inputHandler.isKeyPressed(KeyEvent.VK_LEFT)) {
                    secondIceCream.setCurrentDirection(Direction.LEFT);
                } else if (inputHandler.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    secondIceCream.setCurrentDirection(Direction.RIGHT);
                }

                int result = game.toggleIceBlocksSecond();

                // NUEVO: Cambiar acción según si creó o rompió
                if (result > 0) {
                    secondIceCream.setCurrentAction("shoot");
                    System.out.println("✓ (P2) Hilera de " + result + " bloque(s) de hielo creada");

                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            secondIceCream.setCurrentAction("stand");
                        }
                    }, 200);

                } else if (result < 0) {
                    secondIceCream.setCurrentAction("break");
                    System.out.println("✓ (P2) Hilera de " + (-result) + " bloque(s) roto(s) en efecto dominó");

                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            secondIceCream.setCurrentAction("stand");
                        }
                    }, 200);
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
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        inputHandler.keyReleased(e); // Delegar a InputHandler
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

    /**
     * Establece el callback para cuando se completa un nivel
     */
    public void setOnLevelComplete(java.util.function.Consumer<Integer> callback) {
        this.onLevelComplete = callback;
    }

    /**
     * Establece el callback para cuando se falla un nivel
     */
    public void setOnLevelFailed(java.util.function.Consumer<Integer> callback) {
        this.onLevelFailed = callback;
    }

    /**
     * Establece la estrategia de IA para el helado
     */
    public void setIceCreamAIStrategy(String strategyName) {
        this.iceCreamAIStrategy = strategyName;
        // Pasar también a Game para que la aplique cuando inicie el nivel
        if (game != null) {
            game.setIceCreamAIStrategy(strategyName);
        }
    }

    /**
     * Obtiene las estrategias de IA disponibles para el helado
     * (Para que SelectIceCreamAI no importe de Domain)
     */
    public String[] getAvailableIceCreamAIStrategies() {
        return IceCreamAIStrategyManager.getAvailableStrategies();
    }

    /**
     * Obtiene valores del enum PVPMode sin necesidad de importar Domain
     */
    public enum PVPModeValue {
        ICE_CREAM_VS_MONSTER,
        ICE_CREAM_COOPERATIVE
    }

    /**
     * Convierte PVPModeValue a PVPMode del Domain
     */
    public PVPMode getPVPModeFromValue(PVPModeValue value) {
        if (value == PVPModeValue.ICE_CREAM_VS_MONSTER) {
            return PVPMode.ICE_CREAM_VS_MONSTER;
        } else {
            return PVPMode.ICE_CREAM_COOPERATIVE;
        }
    }

    // ========================================
    // MÉTODOS PARA GAMEPANEL (Presentation)
    // Acceso a información sin importar Domain directamente
    // ========================================

    /**
     * Obtiene el estado actual del juego como String
     */
    public String getGameStateAsString() {
        if (game == null)
            return "LOADING";
        GameState state = game.getGameState();
        if (state == GameState.PLAYING)
            return "PLAYING";
        if (state == GameState.PAUSED)
            return "PAUSED";
        if (state == GameState.WON)
            return "WON";
        if (state == GameState.LOST)
            return "LOST";
        return "UNKNOWN";
    }

    /**
     * Verifica si el juego está en cierto estado
     */
    public boolean isGameState(String stateName) {
        return getGameStateAsString().equals(stateName);
    }

    /**
     * Obtiene las dimensiones del tablero
     */
    public int[] getBoardDimensions() {
        if (game == null || game.getBoard() == null) {
            return new int[] { 0, 0 };
        }
        Board board = game.getBoard();
        return new int[] { board.getWidth(), board.getHeight() };
    }

    /**
     * Actualiza posiciones visuales de todas las entidades
     */
    public void updateAllVisualPositions() {
        if (game == null || game.getBoard() == null)
            return;
        Board board = game.getBoard();

        if (board.getIceCream() != null) {
            board.getIceCream().updateVisualPosition();
        }
        if (board.getSecondIceCream() != null) {
            board.getSecondIceCream().updateVisualPosition();
        }
        for (Enemy enemy : board.getEnemies()) {
            if (enemy != null) {
                enemy.updateVisualPosition();
            }
        }
    }

    /**
     * Obtiene lista de frutas (solo las no recolectadas)
     */
    public List<Fruit> getFruits() {
        if (game == null || game.getBoard() == null) {
            return new java.util.ArrayList<>();
        }
        return game.getBoard().getFruits();
    }

    /**
     * Obtiene lista de muros
     */
    public List<Position> getWalls() {
        if (game == null || game.getBoard() == null) {
            return new java.util.ArrayList<>();
        }
        return game.getBoard().getWalls();
    }

    /**
     * Obtiene lista de bloques de hielo
     */
    public List<IceBlock> getIceBlocks() {
        if (game == null || game.getBoard() == null) {
            return new java.util.ArrayList<>();
        }
        return game.getBoard().getIceBlocks();
    }

    /**
     * Obtiene el helado principal (Jugador 1)
     */
    public IceCream getMainIceCream() {
        if (game == null || game.getBoard() == null) {
            return null;
        }
        return game.getBoard().getIceCream();
    }

    /**
     * Obtiene el segundo helado (Jugador 2, solo en modo cooperativo)
     */
    public IceCream getSecondIceCream() {
        if (game == null || game.getBoard() == null) {
            return null;
        }
        return game.getBoard().getSecondIceCream();
    }

    /**
     * Obtiene lista de enemigos
     */
    public List<Enemy> getEnemies() {
        if (game == null || game.getBoard() == null) {
            return new java.util.ArrayList<>();
        }
        return game.getBoard().getEnemies();
    }

    /**
     * Obtiene todos los datos necesarios para renderizado en un DTO
     * Permite que GamePanel NO necesite importar Domain
     */
    public RenderData getRenderData() {
        RenderData data = new RenderData();

        // Estado del juego
        data.gameState = getGameStateAsString();

        if (game == null || game.getBoard() == null) {
            data.boardWidth = 0;
            data.boardHeight = 0;
            return data;
        }

        Board board = game.getBoard();

        // Dimensiones
        data.boardWidth = board.getWidth();
        data.boardHeight = board.getHeight();

        // UI
        data.score = game.getScore();
        data.remainingTime = game.getRemainingTime();
        data.currentLevelNumber = game.getCurrentLevel() != null ? game.getCurrentLevel().getLevelNumber() : 0;
        data.remainingFruits = board.getRemainingFruits();
        data.gameMode = game.getGameMode().toString();

        // Helado principal
        IceCream iceCream = board.getIceCream();
        if (iceCream != null) {
            data.iceCreamX = iceCream.getVisualX();
            data.iceCreamY = iceCream.getVisualY();
            data.iceCreamFlavor = iceCream.getFlavor().toLowerCase();
            data.iceCreamDirection = iceCream.getCurrentDirection().toString().toLowerCase();
            data.iceCreamAction = iceCream.getCurrentAction();
            data.iceCreamAlive = iceCream.isAlive();
        }

        // Segundo helado
        IceCream secondIceCream = board.getSecondIceCream();
        if (secondIceCream != null) {
            data.secondIceCreamX = secondIceCream.getVisualX();
            data.secondIceCreamY = secondIceCream.getVisualY();
            data.secondIceCreamFlavor = secondIceCream.getFlavor().toLowerCase();
            data.secondIceCreamDirection = secondIceCream.getCurrentDirection().toString().toLowerCase();
            data.secondIceCreamAction = secondIceCream.getCurrentAction();
            data.secondIceCreamAlive = secondIceCream.isAlive();
        }

        // Enemigos
        data.enemies = new java.util.ArrayList<>();
        for (Enemy enemy : board.getEnemies()) {
            if (enemy != null) {
                RenderData.EnemyData enemyData = new RenderData.EnemyData();
                enemyData.x = enemy.getVisualX();
                enemyData.y = enemy.getVisualY();
                enemyData.type = enemy.getEnemyType().toLowerCase();
                enemyData.direction = enemy.getCurrentDirection().toString().toLowerCase();
                enemyData.action = enemy.getCurrentAction();
                if (enemy.getColor() != null) {
                    enemyData.color = String.format("#%06X", enemy.getColor().getRGB() & 0xFFFFFF);
                }
                enemyData.alive = enemy.isAlive();
                data.enemies.add(enemyData);
            }
        }

        // Frutas
        data.fruits = new java.util.ArrayList<>();
        for (Fruit fruit : board.getFruits()) {
            RenderData.FruitData fruitData = new RenderData.FruitData();
            Position pos = fruit.getPosition();
            fruitData.x = pos.getX();
            fruitData.y = pos.getY();
            fruitData.type = fruit.getFruitType().toLowerCase();
            fruitData.collected = fruit.isCollected();
            fruitData.visualState = fruit.getVisualState();
            data.fruits.add(fruitData);
        }

        // Bloques de hielo
        data.iceBlocks = new java.util.ArrayList<>();
        for (IceBlock block : board.getIceBlocks()) {
            RenderData.IceBlockData iceBlockData = new RenderData.IceBlockData();
            Position pos = block.getPosition();
            iceBlockData.x = pos.getX();
            iceBlockData.y = pos.getY();
            data.iceBlocks.add(iceBlockData);
        }

        // Muros
        data.walls = new java.util.ArrayList<>();
        for (Position pos : board.getWalls()) {
            data.walls.add(new RenderData.PositionData(pos.getX(), pos.getY()));
        }

        return data;
    }

    /**
     * Obtiene todos los datos necesarios para renderizado usando ViewData (MVC
     * completo)
     * Permite que GamePanel NO necesite importar Domain
     */
    public ViewData getViewData() {
        ViewData data = new ViewData();

        // Estado del juego
        data.gameState = getGameStateAsString();

        if (game == null || game.getBoard() == null) {
            data.boardWidth = 0;
            data.boardHeight = 0;
            return data;
        }

        Board board = game.getBoard();

        // ✅ IMPORTANTE: Actualizar posiciones visuales ANTES de capturar datos
        // Esto asegura que los helados y monstruos se vean en movimiento suave
        if (board.getIceCream() != null) {
            board.getIceCream().updateVisualPosition();
        }
        if (board.getSecondIceCream() != null) {
            board.getSecondIceCream().updateVisualPosition();
        }
        for (Enemy enemy : board.getEnemies()) {
            if (enemy != null) {
                enemy.updateVisualPosition();
            }
        }

        // Dimensiones
        data.boardWidth = board.getWidth();
        data.boardHeight = board.getHeight();

        // UI
        data.score = game.getScore();
        data.remainingTime = game.getRemainingTime();
        data.currentLevel = game.getCurrentLevel() != null ? game.getCurrentLevel().getLevelNumber() : 0;
        data.remainingFruits = board.getRemainingFruits();
        data.gameMode = game.getGameMode().toString();

        // Helado principal
        IceCream iceCream = board.getIceCream();
        if (iceCream != null) {
            data.iceCreamX = iceCream.getVisualX();
            data.iceCreamY = iceCream.getVisualY();
            data.iceCreamFlavor = iceCream.getFlavor().toLowerCase();
            data.iceCreamDirection = iceCream.getCurrentDirection().toString().toLowerCase();
            data.iceCreamAction = iceCream.getCurrentAction();
            data.iceCreamAlive = iceCream.isAlive();
        }

        // Segundo helado
        IceCream secondIceCream = board.getSecondIceCream();
        if (secondIceCream != null) {
            data.secondIceCreamX = secondIceCream.getVisualX();
            data.secondIceCreamY = secondIceCream.getVisualY();
            data.secondIceCreamFlavor = secondIceCream.getFlavor().toLowerCase();
            data.secondIceCreamDirection = secondIceCream.getCurrentDirection().toString().toLowerCase();
            data.secondIceCreamAction = secondIceCream.getCurrentAction();
            data.secondIceCreamAlive = secondIceCream.isAlive();
        }

        // Enemigos
        data.enemies = new java.util.ArrayList<>();
        for (Enemy enemy : board.getEnemies()) {
            if (enemy != null) {
                ViewData.EnemyView enemyData = new ViewData.EnemyView();
                enemyData.x = enemy.getVisualX();
                enemyData.y = enemy.getVisualY();
                enemyData.type = enemy.getEnemyType().toLowerCase();
                enemyData.direction = enemy.getCurrentDirection().toString().toLowerCase();
                enemyData.action = enemy.getCurrentAction();
                if (enemy.getColor() != null) {
                    enemyData.color = String.format("#%06X", enemy.getColor().getRGB() & 0xFFFFFF);
                }
                enemyData.alive = enemy.isAlive();
                data.enemies.add(enemyData);
            }
        }

        // Frutas
        data.fruits = new java.util.ArrayList<>();
        data.cactuses = new java.util.ArrayList<>();
        for (Fruit fruit : board.getFruits()) {
            if (fruit.getFruitType().equalsIgnoreCase("Cactus")) {
                // Manejar Cactus especialmente
                ViewData.FrutaEspecialView cactusData = new ViewData.FrutaEspecialView();
                Position pos = fruit.getPosition();
                cactusData.x = pos.getX();
                cactusData.y = pos.getY();
                cactusData.tipo = "cactus";
                cactusData.visualState = fruit.getVisualState();
                cactusData.collected = fruit.isCollected();
                data.cactuses.add(cactusData);
            } else {
                // Frutas normales
                ViewData.FruitView fruitData = new ViewData.FruitView();
                Position pos = fruit.getPosition();
                fruitData.x = pos.getX();
                fruitData.y = pos.getY();
                fruitData.type = fruit.getFruitType().toLowerCase();
                fruitData.collected = fruit.isCollected();
                fruitData.visualState = fruit.getVisualState();
                data.fruits.add(fruitData);
            }
        }

        // Bloques de hielo
        data.iceBlocks = new java.util.ArrayList<>();
        for (IceBlock block : board.getIceBlocks()) {
            ViewData.PositionView iceBlockData = new ViewData.PositionView();
            Position pos = block.getPosition();
            iceBlockData.x = pos.getX();
            iceBlockData.y = pos.getY();
            data.iceBlocks.add(iceBlockData);
        }

        // Fogatas
        data.fogatas = new java.util.ArrayList<>();
        for (GameObject fogata : board.getFogatas()) {
            ViewData.ObstaculoView fogataView = new ViewData.ObstaculoView();
            fogataView.x = fogata.getPosition().getX();
            fogataView.y = fogata.getPosition().getY();
            // Verificar si es instancia de Fogata para obtener estado encendida
            if (fogata instanceof Fogata) {
                fogataView.encendida = ((Fogata) fogata).isEncendida();
            }
            data.fogatas.add(fogataView);
        }

        // Baldosas calientes
        data.baldosasCalientes = new java.util.ArrayList<>();
        for (GameObject baldosa : board.getBaldosasCalientes()) {
            ViewData.ObstaculoView baldosaView = new ViewData.ObstaculoView();
            baldosaView.x = baldosa.getPosition().getX();
            baldosaView.y = baldosa.getPosition().getY();
            data.baldosasCalientes.add(baldosaView);
        }

        // Muros
        data.walls = new java.util.ArrayList<>();
        for (Position pos : board.getWalls()) {
            data.walls.add(new ViewData.PositionView(pos.getX(), pos.getY()));
        }

        return data;
    }
}