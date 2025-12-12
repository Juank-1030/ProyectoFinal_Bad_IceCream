package Domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private String secondIceCreamFlavor; // Segundo sabor para modo cooperativo

    // Para modos con IA
    private List<AI> enemyAIs; // Para modos PVM y MVM

    // Estrategia de IA para el helado controlado por IA
    private String iceCreamAIStrategyName; // Nombre de la estrategia

    // Control de tiempo
    private transient long lastUpdateTime;
    private static final int FPS = 60;
    // FRAME_TIME no se usa actualmente pero se mantiene para futura optimización

    /**
     * Constructor del juego
     * 
     * @param gameMode             Modo de juego seleccionado
     * @param iceCreamFlavor       Sabor del helado seleccionado
     * @param secondIceCreamFlavor Segundo sabor para modo cooperativo (puede ser
     *                             null)
     */
    private String monsterType; // Tipo de monstruo seleccionado en PVP
    private Map<String, Integer> enemyConfig; // Configuración personalizada de enemigos
    private Map<String, Integer> fruitConfig; // Configuración personalizada de frutas
    private Map<String, Integer> obstacleConfig; // Configuración personalizada de obstáculos

    // Constructor principal con todos los parámetros
    public Game(GameMode gameMode, String iceCreamFlavor, String secondIceCreamFlavor, String monsterType,
            Map<String, Integer> enemyConfig, Map<String, Integer> fruitConfig, Map<String, Integer> obstacleConfig) {
        this.gameMode = gameMode;
        this.iceCreamFlavor = iceCreamFlavor;
        this.secondIceCreamFlavor = secondIceCreamFlavor; // null para modo vs Monstruo
        this.monsterType = monsterType; // PVP: Tipo de monstruo controlado
        this.enemyConfig = enemyConfig; // Configuración personalizada de enemigos
        this.fruitConfig = fruitConfig; // Configuración personalizada de frutas
        this.obstacleConfig = obstacleConfig; // Configuración personalizada de obstáculos
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
        // Agregar muros indestructibles (bordes del nivel)
        for (Position wallPos : currentLevel.getWallPositions()) {
            board.addWall(wallPos);
        }

        // Agregar bloques de hielo rompibles (interior del nivel)
        for (Position icePos : currentLevel.getIceBlockPositions()) {
            IceBlock iceBlock = new IceBlock(icePos, true);
            board.addIceBlock(iceBlock);
        }

        // Crear el helado
        IceCream iceCream = createIceCream(currentLevel.getIceCreamStartPosition());

        // Aplicar la estrategia de IA si fue especificada
        if (iceCreamAIStrategyName != null && !iceCreamAIStrategyName.isEmpty()) {
            IceCreamAIStrategy aiStrategy = IceCreamAIStrategyManager.getStrategy(iceCreamAIStrategyName);
            if (aiStrategy != null) {
                iceCream.setAIStrategy(aiStrategy);
                System.out.println("✓ IA aplicada al helado: " + iceCreamAIStrategyName);
            }
        }

        board.setIceCream(iceCream);

        // Crear segundo helado si es modo cooperativo
        if (secondIceCreamFlavor != null) {
            // Crear segunda posición para el segundo helado (diferente a la primera)
            List<Position> emptyPositions = new ArrayList<>();
            for (int x = 1; x < currentLevel.getBoardWidth() - 1; x++) {
                for (int y = 1; y < currentLevel.getBoardHeight() - 1; y++) {
                    Position pos = new Position(x, y);
                    if (board.isValidPosition(pos) && !pos.equals(currentLevel.getIceCreamStartPosition())) {
                        emptyPositions.add(pos);
                    }
                }
            }

            if (!emptyPositions.isEmpty()) {
                Random random = new Random();
                Position secondIceCreamPos = emptyPositions.get(random.nextInt(emptyPositions.size()));
                IceCream secondIceCream = createSecondIceCream(secondIceCreamPos);
                board.setSecondIceCream(secondIceCream);
            }
        }

        // Crear enemigos
        // En PVP Vs Monstruo: crear el monstruo seleccionado + enemigos adicionales
        // En PVM/MVM: usar configuración personalizada si existe, sino usar la del
        // nivel
        if (gameMode == GameMode.PVP && monsterType != null) {
            // Crear el monstruo seleccionado en posición aleatoria del nivel
            List<Position> emptyPositions = new ArrayList<>();
            for (int x = 1; x < currentLevel.getBoardWidth() - 1; x++) {
                for (int y = 1; y < currentLevel.getBoardHeight() - 1; y++) {
                    Position pos = new Position(x, y);
                    if (board.isValidPosition(pos)) {
                        emptyPositions.add(pos);
                    }
                }
            }

            if (!emptyPositions.isEmpty()) {
                Random random = new Random();
                Position enemyPos = emptyPositions.get(random.nextInt(emptyPositions.size()));
                Level.EnemyConfig config = new Level.EnemyConfig(monsterType, enemyPos, null, 0);
                Enemy enemy = createEnemy(config);
                if (enemy != null) {
                    board.addEnemy(enemy);
                }
            }

            // ADEMÁS: agregar enemigos adicionales si existen
            if (enemyConfig != null && !enemyConfig.isEmpty()) {
                createEnemiesFromCustomConfig();
            }
        } else if (enemyConfig != null && !enemyConfig.isEmpty()) {
            // PVM/MVM: usar configuración personalizada de enemigos
            createEnemiesFromCustomConfig();
        } else {
            // PVM/MVM: crear todos los enemigos del nivel predeterminado
            for (Level.EnemyConfig config : currentLevel.getEnemyConfigs()) {
                Enemy enemy = createEnemy(config);
                if (enemy != null) {
                    board.addEnemy(enemy);
                }
            }
        }

        // Crear frutas
        // Siempre crear frutas del nivel predeterminado
        // Además: si hay configuración personalizada, agregar esas también
        System.out.println("🍎 setupBoard() - Creando frutas...");
        System.out.println("   fruitConfig: " + (fruitConfig != null ? "no nulo" : "nulo"));
        if (fruitConfig != null) {
            System.out.println("   tamaño: " + fruitConfig.size());
        }

        // Si hay configuración personalizada, usar SOLO esa (reemplaza la del nivel)
        if (fruitConfig != null && !fruitConfig.isEmpty()) {
            createFruitsFromCustomConfig();
        } else {
            // Si no hay configuración personalizada, usar frutas del nivel predeterminado
            createFruitsFromLevelConfig();
        }

        // Crear obstáculos (Fogatas y Baldosas Calientes)
        System.out.println("🏜️ setupBoard() - Creando obstáculos...");
        System.out.println("   obstacleConfig: " + (obstacleConfig != null ? "no nulo" : "nulo"));
        if (obstacleConfig != null) {
            System.out.println("   tamaño: " + obstacleConfig.size());
        }

        if (obstacleConfig != null && !obstacleConfig.isEmpty()) {
            createObstaclesFromCustomConfig();
        }
    }

    /**
     * Crea frutas desde la configuración personalizada del usuario
     */
    private void createFruitsFromCustomConfig() {
        if (fruitConfig == null || fruitConfig.isEmpty()) {
            System.out.println("⚠️ Configuración de frutas vacía o nula, usando predeterminada");
            createFruitsFromLevelConfig();
            return;
        }

        System.out.println("🍎 Creando frutas desde configuración personalizada:");
        for (String fruitType : fruitConfig.keySet()) {
            int quantity = fruitConfig.get(fruitType);
            System.out.println("  - " + fruitType + ": " + quantity);

            // Crear la cantidad especificada de cada tipo de fruta
            for (int i = 0; i < quantity; i++) {
                Position fruitPos = getRandomEmptyPosition();
                Fruit fruit = createFruit(fruitType, fruitPos);
                if (fruit != null) {
                    board.addFruit(fruit);
                }
            }
        }
    }

    /**
     * Crea frutas desde la configuración predeterminada del nivel
     */
    private void createFruitsFromLevelConfig() {
        System.out.println("🍎 Usando frutas predeterminadas del nivel");
        int totalFruits = 0;
        Random random = new Random();
        for (Level.FruitConfig config : currentLevel.getFruitConfigs()) {
            System.out.println("   Tipo: " + config.fruitType + ", Cantidad: " + config.quantity);
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
                    totalFruits++;
                }
            }
        }
        System.out.println("✅ Total frutas agregadas: " + totalFruits);
    }

    /**
     * Crea obstáculos (Fogatas y Baldosas Calientes) desde configuración
     * personalizada
     */
    private void createObstaclesFromCustomConfig() {
        if (obstacleConfig == null || obstacleConfig.isEmpty()) {
            System.out.println("⚠️ Configuración de obstáculos vacía o nula");
            return;
        }

        System.out.println("🏜️ Creando obstáculos desde configuración personalizada:");
        for (String obstacleType : obstacleConfig.keySet()) {
            int quantity = obstacleConfig.get(obstacleType);
            System.out.println("  - " + obstacleType + ": " + quantity);

            // Crear la cantidad especificada de cada tipo de obstáculo
            for (int i = 0; i < quantity; i++) {
                Position obstaclePos = getRandomEmptyPosition();
                if (obstaclePos == null) {
                    System.out.println("⚠️ No hay más posiciones disponibles para obstáculos");
                    break;
                }

                if (obstacleType.equalsIgnoreCase("Fogata")) {
                    Fogata fogata = new Fogata(obstaclePos);
                    board.addFogata(fogata);
                    System.out.println("  ✓ Fogata agregada en " + obstaclePos);
                } else if (obstacleType.equalsIgnoreCase("Baldosa Caliente")) {
                    BaldosaCaliente baldosa = new BaldosaCaliente(obstaclePos);
                    board.addBaldosaCaliente(baldosa);
                    System.out.println("  ✓ Baldosa Caliente agregada en " + obstaclePos);
                } else if (obstacleType.equalsIgnoreCase("Bloque de Hielo")) {
                    IceBlockObstacle iceBlock = new IceBlockObstacle(obstaclePos);
                    board.addIceBlockObstacle(iceBlock);
                    System.out.println("  ✓ Bloque de Hielo agregado en " + obstaclePos);
                }
            }
        }
        System.out.println("✅ Obstáculos agregados correctamente");
    }

    /**
     * Crea un helado según el sabor seleccionado
     */
    private IceCream createIceCream(Position position) {
        try {
            return IceCreamFactory.create(iceCreamFlavor, position);
        } catch (IllegalArgumentException e) {
            // Fallback a vainilla si hay error
            System.err.println("Error al crear helado: " + e.getMessage());
            return new VanillaIceCream(position);
        }
    }

    /**
     * Crea el segundo helado según el sabor seleccionado (modo cooperativo)
     */
    private IceCream createSecondIceCream(Position position) {
        try {
            return IceCreamFactory.create(secondIceCreamFlavor, position);
        } catch (IllegalArgumentException e) {
            // Fallback a vainilla si hay error
            System.err.println("Error al crear segundo helado: " + e.getMessage());
            return new VanillaIceCream(position);
        }
    }

    /**
     * Crea un enemigo según la configuración
     */
    private Enemy createEnemy(Level.EnemyConfig config) {
        Enemy enemy = null;
        String normalized = config.enemyType.toLowerCase();

        switch (normalized) {
            case "troll":
            case "trol":
                if (config.pattern != null) {
                    enemy = new Troll(config.startPosition, config.pattern, config.stepsPerDirection);
                } else {
                    enemy = new Troll(config.startPosition);
                }
                enemy.setColor(new java.awt.Color(34, 177, 76)); // Verde
                break;

            case "maceta":
            case "pot":
            case "olla":
                enemy = new Pot(config.startPosition, board);
                enemy.setColor(new java.awt.Color(255, 165, 0)); // Naranja
                break;

            case "calamar":
            case "yellowsquid":
            case "calamar naranja":
                enemy = new YellowSquid(config.startPosition, board);
                enemy.setColor(new java.awt.Color(255, 255, 0)); // Amarillo
                break;

            case "narval":
                enemy = new Narval(config.startPosition, board);
                enemy.setColor(new java.awt.Color(0, 112, 192)); // Azul
                break;
        }

        return enemy;
    }

    /**
     * Crea una fruta según el tipo
     */
    private Fruit createFruit(String fruitType, Position position) {
        String normalized = fruitType.toLowerCase().trim();

        switch (normalized) {
            // Uvas
            case "uvas":
            case "uva":
            case "grape":
            case "grapes":
                return new Grape(position);

            // Plátanos
            case "plátano":
            case "plátanos":
            case "platano":
            case "platanos":
            case "banana":
            case "bananas":
                return new Banana(position);

            // Piñas
            case "piña":
            case "piñas":
            case "pina":
            case "pinas":
            case "pineapple":
            case "pineapples":
                return new Pineapple(position, board);

            // Cerezas
            case "cereza":
            case "cerezas":
            case "cherry":
            case "cherries":
                return new Cherry(position, board);

            // Cactus
            case "cactus":
            case "cactuses":
                return new Cactus(position);

            default:
                System.err.println("⚠️ Tipo de fruta desconocido: " + fruitType);
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
     * Crea enemigos según la configuración personalizada
     * Mapea nombres de enemigos a sus tipos y los crea con la cantidad especificada
     */
    private void createEnemiesFromCustomConfig() {
        if (enemyConfig == null || enemyConfig.isEmpty()) {
            return;
        }

        Random random = new Random();
        for (String enemyType : enemyConfig.keySet()) {
            int quantity = enemyConfig.get(enemyType);

            // Crear la cantidad especificada de cada tipo de enemigo
            for (int i = 0; i < quantity; i++) {
                Position enemyPos = getRandomEmptyPosition();
                Level.EnemyConfig config = new Level.EnemyConfig(enemyType, enemyPos, null, 0);
                Enemy enemy = createEnemy(config);
                if (enemy != null) {
                    board.addEnemy(enemy);
                }
            }
        }
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

        // En modo PVP COOPERATIVO (dos helados), también crear IA para enemigos
        if (gameMode == GameMode.PVP && secondIceCreamFlavor != null) {
            for (Enemy enemy : board.getEnemies()) {
                enemyAIs.add(new EnemyAI(enemy, board));
            }
        }

        // En modo PVP Vs Monstruo (sin segundo helado), crear IA para enemigos
        // adicionales ÚNICAMENTE (el primer enemigo es el monstruo principal controlado
        // por Jugador 2)
        if (gameMode == GameMode.PVP && secondIceCreamFlavor == null && enemyConfig != null && !enemyConfig.isEmpty()) {
            List<Enemy> enemies = board.getEnemies();
            // Saltar el primer enemigo (monstruo principal) y crear IA para el resto
            // (enemigos adicionales)
            for (int i = 1; i < enemies.size(); i++) {
                enemyAIs.add(new EnemyAI(enemies.get(i), board));
            }
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

        // Actualizar fogatas (estado de encendida/apagada)
        for (Fogata fogata : board.getFogatas()) {
            fogata.update();
        }

        // Actualizar enemigos
        updateEnemies();

        // Actualizar frutas (movimiento, teletransporte)
        updateFruits();

        // Actualizar IA del helado si aplica
        IceCream iceCream = board.getIceCream();
        if (iceCream != null && iceCream.isAIControlled()) {
            IceCreamAIStrategy strategy = iceCream.getAIStrategy();
            if (strategy != null) {
                Direction move = strategy.getNextMove(board, iceCream);
                if (move != null) {
                    // Establecer dirección actual del helado
                    iceCream.setCurrentDirection(move);

                    boolean moved = board.moveIceCream(move);

                    // ✅ MEJORADA: Si no se movió porque hay hielo, intenta romperlo
                    if (!moved) {
                        Position nextPos = iceCream.getPosition().move(move);
                        if (board.isInBounds(nextPos) && board.hasIceBlock(nextPos)) {
                            // Hay hielo bloqueando - intentar romper
                            int brokenCount = board.toggleIceBlocks();

                            // Si rompió hielo, intentar moverse inmediatamente
                            if (brokenCount > 0) {
                                // Después de romper, intentar moverse en la misma dirección
                                moved = board.moveIceCream(move);
                            }
                        }
                    }

                    // Sumar puntos si recolectó fruta
                    if (moved) {
                        Fruit fruit = board.getAndClearLastCollectedFruit();
                        if (fruit != null) {
                            score += 50;
                        }
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
        List<Enemy> enemies = board.getEnemies();

        // En modo PVP vs Monstruo (1 helado vs 1 monstruo, con enemigos adicionales
        // opcionales)
        if (gameMode == GameMode.PVP && secondIceCreamFlavor == null) {
            int aiIndex = 0; // Índice para acceder a enemyAIs

            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);

                // El primer enemigo es el monstruo principal (controlado por Jugador 2)
                if (i == 0) {
                    // Monstruo principal: sin IA, controlado manualmente
                    if (enemy instanceof Narval) {
                        Narval narval = (Narval) enemy;
                        if (narval.isCharging()) {
                            // El Narval está en modo carga: moverse automáticamente
                            board.moveEnemy(narval, narval.getChargeDirection());
                        }
                    }
                } else {
                    // Enemigos adicionales: controlar con IA
                    if (aiIndex < enemyAIs.size()) {
                        Direction nextMove = enemyAIs.get(aiIndex).getNextMove();
                        if (nextMove != null) {
                            board.moveEnemy(enemy, nextMove);
                        }
                        aiIndex++;
                    }
                }
                enemy.update();
            }
            return; // Salir del método después de procesar PVP vs Monstruo
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) {
                continue;
            }

            Direction nextMove;

            // Usar IA en modos PVM, MVM y PVP COOPERATIVO
            if (gameMode == GameMode.PVM || gameMode == GameMode.MVM ||
                    (gameMode == GameMode.PVP && secondIceCreamFlavor != null)) {
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

        // Verificar contacto con Fogata encendida
        for (Fogata fogata : board.getFogatas()) {
            if (fogata.isEncendida()) {
                IceCream iceCream = board.getIceCream();
                if (iceCream != null && iceCream.getPosition().equals(fogata.getPosition())) {
                    iceCream.setAlive(false);
                    gameState = GameState.LOST;
                    System.out.println("🔥 ¡El helado entró en contacto con una Fogata encendida!");
                    return;
                }

                // Verificar segundo helado si existe
                IceCream secondIceCream = board.getSecondIceCream();
                if (secondIceCream != null && secondIceCream.getPosition().equals(fogata.getPosition())) {
                    secondIceCream.setAlive(false);
                    gameState = GameState.LOST;
                    System.out.println("🔥 ¡El segundo helado entró en contacto con una Fogata encendida!");
                    return;
                }
            }
        }

        // Verificar contacto con Baldosa Caliente
        for (BaldosaCaliente baldosa : board.getBaldosasCalientes()) {
            IceCream iceCream = board.getIceCream();
            if (iceCream != null && iceCream.getPosition().equals(baldosa.getPosition())) {
                iceCream.setAlive(false);
                gameState = GameState.LOST;
                System.out.println("🔥 ¡El helado entró en contacto con una Baldosa Caliente!");
                return;
            }

            // Verificar segundo helado si existe
            IceCream secondIceCream = board.getSecondIceCream();
            if (secondIceCream != null && secondIceCream.getPosition().equals(baldosa.getPosition())) {
                secondIceCream.setAlive(false);
                gameState = GameState.LOST;
                System.out.println("🔥 ¡El segundo helado entró en contacto con una Baldosa Caliente!");
                return;
            }
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
                // Verificar si es Cactus con púas (instancia peligrosa)
                if (fruit instanceof Cactus) {
                    Cactus cactus = (Cactus) fruit;
                    if (cactus.isSpiky()) {
                        // El helado muere al intentar recolectar Cactus con púas
                        board.getIceCream().setAlive(false);
                        gameState = GameState.LOST;
                        System.out.println("🌵 ¡El helado fue pinchado por el Cactus espinudo!");
                        return false;
                    }
                }
                score += 50; // Todas las frutas valen 50 puntos
            }
        }

        return moved;
    }

    /**
     * Mueve el segundo helado (modo cooperativo)
     */
    public boolean moveSecondIceCream(Direction direction) {
        if (gameState != GameState.PLAYING || board.getSecondIceCream() == null) {
            return false;
        }
        boolean moved = board.moveSecondIceCream(direction);

        // Verificar si recolectó una fruta y sumar puntos
        if (moved) {
            Fruit fruit = board.getAndClearLastCollectedFruit();
            if (fruit != null) {
                // Verificar si es Cactus con púas (instancia peligrosa)
                if (fruit instanceof Cactus) {
                    Cactus cactus = (Cactus) fruit;
                    if (cactus.isSpiky()) {
                        // El segundo helado muere al intentar recolectar Cactus con púas
                        board.getSecondIceCream().setAlive(false);
                        gameState = GameState.LOST;
                        System.out.println("🌵 ¡El segundo helado fue pinchado por el Cactus espinudo!");
                        return false;
                    }
                }
                score += 50;
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
    /**
     * Toggle de hielo: Crea o rompe bloques según si hay obstáculos
     * - Si NO hay bloques en dirección: Crea hilera
     * - Si HAY bloques en dirección: Los rompe (efecto dominó)
     * 
     * Devuelve: >0 si creó bloques, <0 si rompió bloques, 0 si no pudo hacer nada
     */
    public int toggleIceBlocks() {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return 0;
        }
        return board.toggleIceBlocks();
    }

    /**
     * Toggle de hielo para el segundo helado (modo cooperativo)
     */
    public int toggleIceBlocksSecond() {
        if (gameState != GameState.PLAYING || board.getSecondIceCream() == null) {
            return 0;
        }
        return board.toggleIceBlocksSecond();
    }

    /**
     * Rompe UN SOLO bloque de hielo en la dirección actual del helado
     */
    public boolean breakIceBlock() {
        if (gameState != GameState.PLAYING || gameMode == GameMode.MVM) {
            return false;
        }
        return board.breakIceBlock();
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

    /**
     * Getter para el tipo de monstruo seleccionado
     */
    public String getMonsterType() {
        return monsterType;
    }

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

    public String getSecondIceCreamFlavor() {
        return secondIceCreamFlavor;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Establece la estrategia de IA para el helado
     */
    public void setIceCreamAIStrategy(String strategyName) {
        this.iceCreamAIStrategyName = strategyName;
    }

    /**
     * Actualiza referencias después de desserialización
     * Necesario porque los comportamientos tienen referencias transient a Board
     */
    public void updateBoardReferences() {
        if (board == null || board.getEnemies() == null) {
            return;
        }

        // Actualizar referencias en enemigos que usan ChaseMovement
        for (Enemy enemy : board.getEnemies()) {
            if (enemy instanceof Pot) {
                ((Pot) enemy).updateStateProvider(board);
            } else if (enemy instanceof YellowSquid) {
                ((YellowSquid) enemy).updateStateProvider(board);
            } else if (enemy instanceof Narval) {
                ((Narval) enemy).updateStateProvider(board);
            }
        }
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
    // ==================== MÉTODOS DE PERSISTENCIA ====================

    /**
     * Guarda la partida actual en un archivo .dat
     * La partida se guarda en la carpeta "saves/" del proyecto
     * 
     * @param filename Nombre del archivo donde guardar (ej: "partida1.dat")
     * @return true si se guardó exitosamente, false en caso de error
     */
    public boolean saveGame(String filename) {
        // Crear directorio "saves" si no existe
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }

        // Asegurar que el filename tenga extensión .dat
        if (!filename.endsWith(".dat")) {
            filename = filename + ".dat";
        }

        // Ruta completa: saves/filename.dat
        String fullPath = "saves" + File.separator + filename;

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fullPath))) {
            out.writeObject(this);
            System.out.println("Partida guardada exitosamente en: " + fullPath);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar la partida: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga una partida desde un archivo .dat
     * Busca el archivo en la carpeta "saves/"
     * 
     * @param filename Nombre del archivo a cargar (ej: "partida1.dat")
     * @return Objeto Game cargado, o null si hubo un error
     */
    public static Game loadGame(String filename) {
        // Asegurar que el filename tenga extensión .dat
        if (!filename.endsWith(".dat")) {
            filename = filename + ".dat";
        }

        // Ruta completa: saves/filename.dat
        String fullPath = "saves" + File.separator + filename;

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(fullPath))) {
            Game game = (Game) in.readObject();

            // Restaurar referencias transient después de la deserialización
            game.lastUpdateTime = System.currentTimeMillis();
            game.updateBoardReferences();

            System.out.println("Partida cargada exitosamente desde: " + fullPath);
            return game;
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + fullPath);
            return null;
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Error al deserializar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si existe un archivo de guardado .dat en la carpeta saves/
     * 
     * @param filename Nombre del archivo a verificar (ej: "partida1.dat")
     * @return true si el archivo existe, false en caso contrario
     */
    public static boolean savedGameExists(String filename) {
        // Asegurar que el filename tenga extensión .dat
        if (!filename.endsWith(".dat")) {
            filename = filename + ".dat";
        }

        File file = new File("saves" + File.separator + filename);
        return file.exists() && file.isFile();
    }

    /**
     * Elimina un archivo de guardado .dat de la carpeta saves/
     * 
     * @param filename Nombre del archivo a eliminar (ej: "partida1.dat")
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public static boolean deleteSavedGame(String filename) {
        // Asegurar que el filename tenga extensión .dat
        if (!filename.endsWith(".dat")) {
            filename = filename + ".dat";
        }

        File file = new File("saves" + File.separator + filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Archivo eliminado: " + file.getPath());
            }
            return deleted;
        }
        return false;
    }

    /**
     * Lista todos los archivos .dat guardados en la carpeta saves/
     * 
     * @return Array de nombres de archivos encontrados (solo nombres, sin ruta)
     */
    public static String[] listSavedGames() {
        File dir = new File("saves");

        if (!dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

        if (files == null || files.length == 0) {
            return new String[0];
        }

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        return fileNames;
    }

    /**
     * Guarda automáticamente la partida con un nombre basado en la fecha/hora
     * Se guarda en saves/ con formato: autosave_YYYYMMDD_HHMMSS.dat
     * 
     * @return Nombre del archivo generado, o null si hubo error
     */
    public String autoSave() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new java.util.Date());
        String filename = "autosave_" + timestamp + ".dat";

        if (saveGame(filename)) {
            return filename;
        }
        return null;
    }
}
