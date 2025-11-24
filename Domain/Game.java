package Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase principal que gestiona la lógica del juego
 * Coordina el tablero, niveles, puntuación, tiempo y modos de juego
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private Board board;
    private Level currentLevel;
    private GameMode gameMode;
    private GameState gameState;

    private int score;
    private int remainingTime; // En segundos
    // Sin sistema de vidas (como el juego original)
    private String iceCreamFlavor; // Sabor elegido por el jugador

    // Para modos con IA
    private AI iceCreamAI; // Para modo MVM
    private List<AI> enemyAIs; // Para modos PVM y MVM

    // Control de tiempo
    private transient long lastUpdateTime;
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    /**
     * Constructor del juego
     * 
     * @param gameMode       Modo de juego seleccionado
     * @param iceCreamFlavor Sabor del helado seleccionado
     */
    public Game(GameMode gameMode, String iceCreamFlavor) {
        this.gameMode = gameMode;
        this.iceCreamFlavor = iceCreamFlavor;
        this.gameState = GameState.MENU;
        this.score = 0;
        // Sin sistema de vidas (como el juego original)
        this.enemyAIs = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Inicia un nuevo nivel
     */
    public void startLevel(int levelNumber) {
        // Crear el nivel
        switch (levelNumber) {
            case 1:
                currentLevel = Level.createLevel1();
                break;
            case 2:
                currentLevel = Level.createLevel2();
                break;
            case 3:
                currentLevel = Level.createLevel3();
                break;
            default:
                currentLevel = Level.createLevel1();
        }

        // Crear el tablero
        board = new Board(currentLevel.getBoardWidth(), currentLevel.getBoardHeight());

        // Configurar el tablero con el nivel
        setupBoard();

        // Inicializar tiempo
        remainingTime = currentLevel.getTimeLimit();

        // Configurar IA según el modo de juego
        setupAI();

        gameState = GameState.PLAYING;
    }

    /**
     * Configura el tablero con las entidades del nivel
     */
    private void setupBoard() {
        // Agregar paredes
        for (Position wallPos : currentLevel.getWallPositions()) {
            board.addWall(wallPos);
        }

        // Crear el helado
        IceCream iceCream = createIceCream(currentLevel.getIceCreamStartPosition());
        board.setIceCream(iceCream);

        // Crear enemigos
        for (Level.EnemyConfig config : currentLevel.getEnemyConfigs()) {
            Enemy enemy = createEnemy(config);
            if (enemy != null) {
                board.addEnemy(enemy);
            }
        }

        // Crear frutas
        Random random = new Random();
        for (Level.FruitConfig config : currentLevel.getFruitConfigs()) {
            for (int i = 0; i < config.quantity; i++) {
                Position fruitPos;
                if (config.startPosition != null) {
                    fruitPos = config.startPosition;
                } else {
                    // Generar posición aleatoria válida
                    fruitPos = getRandomEmptyPosition();
                }

                Fruit fruit = createFruit(config.fruitType, fruitPos);
                if (fruit != null) {
                    board.addFruit(fruit);
                }
            }
        }
    }

    /**
     * Crea un helado según el sabor seleccionado
     */
    private IceCream createIceCream(Position position) {
        switch (iceCreamFlavor.toLowerCase()) {
            case "vainilla":
                return new VanillaIceCream(position);
            case "fresa":
                return new StrawberryIceCream(position);
            case "chocolate":
                return new ChocolateIceCream(position);
            default:
                return new VanillaIceCream(position);
        }
    }

    /**
     * Crea un enemigo según la configuración
     */
    private Enemy createEnemy(Level.EnemyConfig config) {
        switch (config.enemyType.toLowerCase()) {
            case "troll":
                if (config.pattern != null) {
                    return new Troll(config.startPosition, config.pattern, config.stepsPerDirection);
                }
                return new Troll(config.startPosition);

            case "maceta":
            case "pot":
                return new Pot(config.startPosition, board);

            case "calamar":
            case "orangesquid":
                return new OrangeSquid(config.startPosition, board);

            default:
                return null;
        }
    }

    /**
     * Crea una fruta según el tipo
     */
    private Fruit createFruit(String fruitType, Position position) {
        switch (fruitType.toLowerCase()) {
            case "uvas":
            case "grape":
                return new Grape(position);

            case "plátano":
            case "platano":
            case "banana":
                return new Banana(position);

            case "piña":
            case "pina":
            case "pineapple":
                return new Pineapple(position, board);

            case "cereza":
            case "cherry":
                return new Cherry(position, board);

            default:
                return null;
        }
    }

    /**
     * Obtiene una posición aleatoria vacía en el tablero
     */
    private Position getRandomEmptyPosition() {
        List<Position> emptyPositions = board.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return new Position(1, 1); // Fallback
        }
        Random random = new Random();
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }

    /**
     * Configura la IA según el modo de juego
     */
    private void setupAI() {
        enemyAIs.clear();

        if (gameMode == GameMode.PVM || gameMode == GameMode.MVM) {
            // Crear IA para cada enemigo
            for (Enemy enemy : board.getEnemies()) {
                enemyAIs.add(new EnemyAI(enemy, board));
            }
        }

        if (gameMode == GameMode.MVM) {
            // Crear IA para el helado
            iceCreamAI = new IceCreamAI(board.getIceCream(), board);
        }
    }

    /**
     * Actualiza el estado del juego (llamar cada frame)
     */
    public void update() {
        if (gameState != GameState.PLAYING) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;

        // Actualizar enemigos
        updateEnemies();

        // Actualizar frutas (movimiento, teletransporte)
        updateFruits();

        // Actualizar IA si aplica
        if (gameMode == GameMode.MVM && iceCreamAI != null) {
            Direction move = iceCreamAI.getNextMove();
            if (move != null) {
                boolean moved = board.moveIceCream(move);

                // Sumar puntos si recolectó fruta
                if (moved) {
                    Fruit fruit = board.getAndClearLastCollectedFruit();
                    if (fruit != null) {
                        score += 50;
                    }
                }
            }
        }

        // Verificar condiciones de victoria/derrota
        checkGameConditions();

        // Actualizar tiempo (cada segundo)
        if (deltaTime >= 1000) {
            remainingTime--;
            lastUpdateTime = currentTime;

            if (remainingTime <= 0) {
                gameState = GameState.LOST;
            }
        }
    }

    /**
     * Actualiza los enemigos (movimiento y IA)
     */
    private void updateEnemies() {
        // En modo PVP, los enemigos NO se mueven automáticamente
        // El Jugador 2 los controla manualmente con moveEnemy()
        if (gameMode == GameMode.PVP) {
            return; // Salir sin mover enemigos
        }

        List<Enemy> enemies = board.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                continue;
            }

            Direction nextMove;

            // Usar IA en modos PVM y MVM
            if (gameMode == GameMode.PVM || gameMode == GameMode.MVM) {
                // Usar IA
                if (i < enemyAIs.size()) {
                    nextMove = enemyAIs.get(i).getNextMove();
                } else {
                    nextMove = enemy.getNextMove();
                }
            } else {
                nextMove = enemy.getNextMove();
            }

            if (nextMove != null) {
                board.moveEnemy(enemy, nextMove);
            }

            enemy.update();
        }
    }

    /**
     * Actualiza las frutas
     */
    private void updateFruits() {
        for (Fruit fruit : board.getFruits()) {
            if (!fruit.isCollected()) {
                fruit.update();
            }
        }
    }

    /**
     * Verifica las condiciones de victoria/derrota
     */
    private void checkGameConditions() {
        // Verificar si el helado murió → GAME OVER inmediato (sin vidas)
        if (!board.getIceCream().isAlive()) {
            gameState = GameState.LOST;
            return;
        }

        // Verificar si recogió todas las frutas
        if (board.getRemainingFruits() == 0) {
            gameState = GameState.WON;
            // Bonus por tiempo restante
            score += remainingTime * 10;
        }
    }

    /**
     * Mueve el helado (para control manual)
     */
    public boolean moveIceCream(Direction direction) {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return false;
        }
        boolean moved = board.moveIceCream(direction);

        // Verificar si recolectó una fruta y sumar puntos
        if (moved) {
            Fruit fruit = board.getAndClearLastCollectedFruit();
            if (fruit != null) {
                score += 50; // Todas las frutas valen 50 puntos
            }
        }

        return moved;
    }

    /**
     * Crea o rompe bloques de hielo (mismo botón)
     * - Si hay espacio: crea FILA de bloques
     * - Si hay bloque enfrente: rompe UN bloque
     * (Como en el Bad Ice-Cream original)
     */
    public int toggleIceBlocks() {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return 0;
        }

        // Verificar si hay un bloque enfrente
        Direction direction = board.getIceCream().getCurrentDirection();
        Position targetPos = board.getIceCream().getPosition().move(direction);

        if (board.hasIceBlock(targetPos)) {
            // Hay bloque enfrente → ROMPER UN bloque
            boolean broken = board.breakIceBlock();
            return broken ? -1 : 0; // -1 indica que rompió
        } else {
            // No hay bloque → CREAR FILA de bloques
            int created = board.createIceBlock();
            return created; // Número positivo indica cuántos creó
        }
    }

    /**
     * @deprecated Usa toggleIceBlocks() en su lugar
     */
    public boolean createIceBlock() {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return false;
        }
        int result = board.createIceBlock();
        return result > 0;
    }

    /**
     * @deprecated Usa toggleIceBlocks() en su lugar
     */
    public int breakIceBlocks() {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return 0;
        }
        return board.breakIceBlocks();
    }

    /**
     * Pausa/reanuda el juego
     */
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public String getIceCreamFlavor() {
        return iceCreamFlavor;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Mueve un enemigo (para modo PVP - Jugador 2)
     * 
     * @param enemyIndex Índice del enemigo (0, 1, 2...)
     * @param direction  Dirección del movimiento
     * @return true si el movimiento fue exitoso
     */
    public boolean moveEnemy(int enemyIndex, Direction direction) {
        // Solo permitido en modo PVP
        if (gameState != GameState.PLAYING || gameMode != GameMode.PVP) {
            return false;
        }

        List<Enemy> enemies = board.getEnemies();
        if (enemyIndex < 0 || enemyIndex >= enemies.size()) {
            return false;
        }

        Enemy enemy = enemies.get(enemyIndex);
        return board.moveEnemy(enemy, direction);
    }

    /**
     * Obtiene el número de enemigos en el juego
     * 
     * @return Cantidad de enemigos
     */
    public int getEnemyCount() {
        return board.getEnemies().size();
    }
}
