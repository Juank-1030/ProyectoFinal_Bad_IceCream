package Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nivel del juego con su configuración específica
 * Cada nivel tiene diferentes enemigos, frutas y disposición del tablero
 */
public class Level implements Serializable {
    private static final long serialVersionUID = 1L;

    private int levelNumber;
    private String levelName;
    private int boardWidth;
    private int boardHeight;
    private int timeLimit; // Tiempo límite en segundos (3 minutos = 180)

    // Configuración de entidades
    private List<EnemyConfig> enemyConfigs;
    private List<FruitConfig> fruitConfigs;
    private List<Position> wallPositions; // Bordes indestructibles
    private List<Position> iceBlockPositions; // Bloques rompibles interiores
    private Position iceCreamStartPosition;

    /**
     * Constructor de Level
     */
    public Level(int levelNumber, String levelName, int boardWidth, int boardHeight) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.timeLimit = 180; // 3 minutos por defecto
        this.enemyConfigs = new ArrayList<>();
        this.fruitConfigs = new ArrayList<>();
        this.wallPositions = new ArrayList<>();
        this.iceBlockPositions = new ArrayList<>();
    }

    /**
     * Clase interna para configuración de enemigos
     */
    public static class EnemyConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        public String enemyType; // "Troll", "Maceta", "Calamar"
        public Position startPosition;
        public Direction[] pattern; // Para Troll
        public int stepsPerDirection; // Para Troll

        public EnemyConfig(String enemyType, Position startPosition) {
            this.enemyType = enemyType;
            this.startPosition = startPosition;
        }

        public EnemyConfig(String enemyType, Position startPosition, Direction[] pattern, int stepsPerDirection) {
            this.enemyType = enemyType;
            this.startPosition = startPosition;
            this.pattern = pattern;
            this.stepsPerDirection = stepsPerDirection;
        }
    }

    /**
     * Clase interna para configuración de frutas
     */
    public static class FruitConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        public String fruitType; // "Uvas", "Plátano", "Piña", "Cereza"
        public Position startPosition;
        public int quantity; // Cantidad de esta fruta

        public FruitConfig(String fruitType, Position startPosition) {
            this.fruitType = fruitType;
            this.startPosition = startPosition;
            this.quantity = 1;
        }

        public FruitConfig(String fruitType, int quantity) {
            this.fruitType = fruitType;
            this.quantity = quantity;
            this.startPosition = null; // Se generará aleatoriamente
        }
    }

    /**
     * Crea los niveles predefinidos del juego
     */
    public static Level createLevel1() {
        Level level = new Level(1, "Nivel 1 - Troll's Maze", 13, 14);

        // Posición inicial del helado
        level.setIceCreamStartPosition(new Position(6, 7));

        // 2 Trolls con patrones diferentes
        Direction[] pattern1 = { Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP };
        level.addEnemyConfig(new EnemyConfig("Troll", new Position(3, 3), pattern1, 3));

        Direction[] pattern2 = { Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN };
        level.addEnemyConfig(new EnemyConfig("Troll", new Position(11, 7), pattern2, 3));

        // 8 Uvas y 8 Plátanos
        level.addFruitConfig(new FruitConfig("Uvas", 8));
        level.addFruitConfig(new FruitConfig("Plátano", 8));

        // Crear paredes del laberinto
        level.createLevel1Walls();

        return level;
    }

    /**
     * Nivel 2 - Maceta persigue
     */
    public static Level createLevel2() {
        Level level = new Level(2, "Nivel 2 - Pot Chase", 13, 14);

        level.setIceCreamStartPosition(new Position(6, 7));

        // 1 Maceta
        level.addEnemyConfig(new EnemyConfig("Maceta", new Position(1, 1)));

        // 8 Piñas y 8 Plátanos
        level.addFruitConfig(new FruitConfig("Piña", 8));
        level.addFruitConfig(new FruitConfig("Plátano", 8));

        level.createLevel2Walls();

        return level;
    }

    /**
     * Nivel 3 - Calamar amarillo rompe bloques
     */
    public static Level createLevel3() {
        Level level = new Level(3, "Nivel 3 - Yellow Squid", 13, 14);

        level.setIceCreamStartPosition(new Position(6, 7));

        // 1 Calamar Amarillo
        level.addEnemyConfig(new EnemyConfig("YellowSquid", new Position(1, 1)));

        // 8 Piñas y 8 Cerezas
        level.addFruitConfig(new FruitConfig("Piña", 8));
        level.addFruitConfig(new FruitConfig("Cereza", 8));

        level.createLevel3Walls();

        return level;
    }

    /**
     * Crea las paredes del nivel 1
     */
    private void createLevel1Walls() {
        // Bordes del tablero (muros indestructibles)
        for (int x = 0; x < boardWidth; x++) {
            addWallPosition(new Position(x, 0)); // Borde superior
            addWallPosition(new Position(x, boardHeight - 1)); // Borde inferior
        }
        for (int y = 0; y < boardHeight; y++) {
            addWallPosition(new Position(0, y)); // Borde izquierdo
            addWallPosition(new Position(boardWidth - 1, y)); // Borde derecho
        }

        // Obstáculos internos (laberinto simple - bloques rompibles)
        // Paredes horizontales
        for (int x = 2; x < 6; x++) {
            addIceBlockPosition(new Position(x, 3));
        }
        for (int x = 9; x < 13; x++) {
            addIceBlockPosition(new Position(x, 3));
        }
        for (int x = 2; x < 6; x++) {
            addIceBlockPosition(new Position(x, 7));
        }
        for (int x = 9; x < 13; x++) {
            addIceBlockPosition(new Position(x, 7));
        }

        // Paredes verticales
        for (int y = 2; y < 5; y++) {
            addIceBlockPosition(new Position(4, y));
        }
        for (int y = 6; y < 9; y++) {
            addIceBlockPosition(new Position(10, y));
        }
    }

    /**
     * Crea las paredes del nivel 2
     */
    private void createLevel2Walls() {
        // Bordes (muros indestructibles)
        for (int x = 0; x < boardWidth; x++) {
            addWallPosition(new Position(x, 0));
            addWallPosition(new Position(x, boardHeight - 1));
        }
        for (int y = 0; y < boardHeight; y++) {
            addWallPosition(new Position(0, y));
            addWallPosition(new Position(boardWidth - 1, y));
        }

        // Laberinto más complejo (bloques rompibles)
        for (int x = 3; x < 7; x++) {
            addIceBlockPosition(new Position(x, 2));
            addIceBlockPosition(new Position(x, 8));
        }
        for (int x = 8; x < 12; x++) {
            addIceBlockPosition(new Position(x, 2));
            addIceBlockPosition(new Position(x, 8));
        }
    }

    /**
     * Crea las paredes del nivel 3
     */
    private void createLevel3Walls() {
        // Similar al nivel 2 pero con más espacios abiertos
        // Bordes (muros indestructibles)
        for (int x = 0; x < boardWidth; x++) {
            addWallPosition(new Position(x, 0));
            addWallPosition(new Position(x, boardHeight - 1));
        }
        for (int y = 0; y < boardHeight; y++) {
            addWallPosition(new Position(0, y));
            addWallPosition(new Position(boardWidth - 1, y));
        }

        // Pocas paredes internas (bloques rompibles)
        for (int y = 3; y < 8; y++) {
            addIceBlockPosition(new Position(5, y));
            addIceBlockPosition(new Position(9, y));
        }
    }

    // Métodos auxiliares
    public void addEnemyConfig(EnemyConfig config) {
        enemyConfigs.add(config);
    }

    public void addFruitConfig(FruitConfig config) {
        fruitConfigs.add(config);
    }

    public void addIceBlockPosition(Position pos) {
        // No agregar si está en un borde (donde hay muros)
        if (pos.getX() <= 0 || pos.getX() >= boardWidth - 1 ||
                pos.getY() <= 0 || pos.getY() >= boardHeight - 1) {
            return; // Ignorar posiciones de borde
        }
        if (!iceBlockPositions.contains(pos)) {
            iceBlockPositions.add(pos);
        }
    }

    public void addWallPosition(Position pos) {
        if (!wallPositions.contains(pos)) {
            wallPositions.add(pos);
        }
    }

    // Getters y Setters
    public int getLevelNumber() {
        return levelNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<EnemyConfig> getEnemyConfigs() {
        return new ArrayList<>(enemyConfigs);
    }

    public List<FruitConfig> getFruitConfigs() {
        return new ArrayList<>(fruitConfigs);
    }

    public List<Position> getIceBlockPositions() {
        return new ArrayList<>(iceBlockPositions);
    }

    public List<Position> getWallPositions() {
        return new ArrayList<>(wallPositions);
    }

    public Position getIceCreamStartPosition() {
        return iceCreamStartPosition;
    }

    public void setIceCreamStartPosition(Position iceCreamStartPosition) {
        this.iceCreamStartPosition = iceCreamStartPosition;
    }

    /**
     * Calcula el total de frutas en este nivel
     */
    public int getTotalFruits() {
        int total = 0;
        for (FruitConfig config : fruitConfigs) {
            total += config.quantity;
        }
        return total;
    }
}
