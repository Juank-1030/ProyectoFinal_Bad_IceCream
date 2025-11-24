package Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el tablero de juego (matriz)
 * Gestiona la posición de todos los elementos: helados, enemigos, frutas, bloques de hielo
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    // Dimensiones del tablero
    private int width;
    private int height;

    // Entidades del juego
    private IceCream iceCream;
    private List<Enemy> enemies;
    private List<Fruit> fruits;
    private List<IceBlock> iceBlocks;
    private List<Position> walls;  // Paredes permanentes

    // Matriz de celdas (para búsqueda rápida)
    private CellType[][] cells;

    /**
     * Constructor del tablero
     * @param width Ancho del tablero
     * @param height Alto del tablero
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.enemies = new ArrayList<>();
        this.fruits = new ArrayList<>();
        this.iceBlocks = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.cells = new CellType[height][width];
        initializeCells();
    }

    /**
     * Inicializa todas las celdas como vacías
     */
    private void initializeCells() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = CellType.EMPTY;
            }
        }
    }

    /**
     * Enum para tipos de celdas
     */
    public enum CellType {
        EMPTY,
        WALL,
        ICE_BLOCK,
        ICE_CREAM,
        ENEMY,
        FRUIT
    }

    /**
     * Verifica si una posición está dentro del tablero
     */
    public boolean isInBounds(Position pos) {
        return pos.getX() >= 0 && pos.getX() < width &&
               pos.getY() >= 0 && pos.getY() < height;
    }

    /**
     * Verifica si una posición es válida para moverse (no hay obstáculos)
     */
    public boolean isValidPosition(Position pos) {
        if (!isInBounds(pos)) {
            return false;
        }

        // Verificar si hay pared
        if (isWall(pos)) {
            return false;
        }

        // Verificar si hay bloque de hielo
        if (hasIceBlock(pos)) {
            return false;
        }

        return true;
    }

    /**
     * Verifica si hay una pared en la posición
     */
    public boolean isWall(Position pos) {
        return walls.contains(pos);
    }

    /**
     * Verifica si hay un bloque de hielo en la posición
     */
    public boolean hasIceBlock(Position pos) {
        for (IceBlock block : iceBlocks) {
            if (block.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el bloque de hielo en una posición
     */
    public IceBlock getIceBlockAt(Position pos) {
        for (IceBlock block : iceBlocks) {
            if (block.getPosition().equals(pos)) {
                return block;
            }
        }
        return null;
    }

    /**
     * Verifica si hay una fruta en la posición
     */
    public Fruit getFruitAt(Position pos) {
        for (Fruit fruit : fruits) {
            if (fruit.getPosition().equals(pos) && !fruit.isCollected()) {
                return fruit;
            }
        }
        return null;
    }

    /**
     * Verifica si hay un enemigo en la posición
     */
    public Enemy getEnemyAt(Position pos) {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(pos) && enemy.isAlive()) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Mueve el helado a una nueva posición
     * @return true si el movimiento fue exitoso
     */
    public boolean moveIceCream(Direction direction) {
        if (iceCream == null || !iceCream.isAlive()) {
            return false;
        }

        Position newPos = iceCream.getNextPosition(direction);

        if (!isValidPosition(newPos)) {
            return false;
        }

        // Actualizar posición
        iceCream.updatePosition(newPos);
        iceCream.setCurrentDirection(direction);

        // Verificar colisión con fruta
        Fruit fruit = getFruitAt(newPos);
        if (fruit != null && !fruit.isCollected()) {
            fruit.collect();
            iceCream.collectFruit();
            lastCollectedFruit = fruit;  // Guardar para que Game sume puntos
        }

        // Verificar colisión con enemigo
        Enemy enemy = getEnemyAt(newPos);
        if (enemy != null) {
            iceCream.setAlive(false);  // El helado muere
        }

        return true;
    }
    
    private Fruit lastCollectedFruit = null;
    
    /**
     * Obtiene y limpia la última fruta recolectada
     * @return La última fruta recolectada, o null
     */
    public Fruit getAndClearLastCollectedFruit() {
        Fruit fruit = lastCollectedFruit;
        lastCollectedFruit = null;
        return fruit;
    }

    /**
     * Mueve un enemigo
     */
    public boolean moveEnemy(Enemy enemy, Direction direction) {
        if (enemy == null || !enemy.isAlive()) {
            return false;
        }

        Position newPos = enemy.getNextPosition(direction);

        // Los enemigos pueden intentar romper bloques si tienen la habilidad
        if (!isValidPosition(newPos)) {
            if (enemy.canBreakIce() && hasIceBlock(newPos)) {
                removeIceBlock(newPos);
                enemy.updatePosition(newPos);
                enemy.setCurrentDirection(direction);
                return true;
            }
            return false;
        }

        enemy.updatePosition(newPos);
        enemy.setCurrentDirection(direction);

        // Verificar colisión con helado
        if (iceCream != null && iceCream.getPosition().equals(newPos)) {
            iceCream.setAlive(false);
        }

        return true;
    }

    /**
     * Crea una FILA de bloques de hielo en la dirección del helado
     * Se detiene al encontrar una pared u otro obstáculo
     * (Como en el Bad Ice-Cream original)
     */
    public int createIceBlock() {
        if (iceCream == null || !iceCream.canCreateIce()) {
            return 0;
        }

        Direction direction = iceCream.getCurrentDirection();
        Position currentPos = iceCream.getPosition().move(direction);
        int blocksCreated = 0;

        // Crear bloques en línea recta hasta encontrar obstáculo
        while (isInBounds(currentPos) && isValidPosition(currentPos)) {
            // Verificar si hay enemigo (no crear bloque ahí)
            if (getEnemyAt(currentPos) != null) {
                break;
            }
            
            // Verificar si hay fruta (no crear bloque ahí)
            if (getFruitAt(currentPos) != null) {
                break;
            }
            
            // Crear bloque si no hay ninguno
            if (!hasIceBlock(currentPos)) {
                IceBlock newBlock = new IceBlock(currentPos, true, iceCream);
                iceBlocks.add(newBlock);
                blocksCreated++;
                currentPos = currentPos.move(direction);
            } else {
                // Ya hay un bloque, detener
                break;
            }
        }

        return blocksCreated;
    }

    /**
     * Rompe UN SOLO bloque de hielo en la dirección del helado
     * (Como en el Bad Ice-Cream original - mismo botón que crear)
     */
    public boolean breakIceBlock() {
        if (iceCream == null || !iceCream.canBreakIce()) {
            return false;
        }

        Direction direction = iceCream.getCurrentDirection();
        Position targetPos = iceCream.getPosition().move(direction);

        // Verificar si hay bloque para romper
        if (isInBounds(targetPos) && hasIceBlock(targetPos)) {
            IceBlock block = getIceBlockAt(targetPos);
            if (block != null && block.isBreakable()) {
                iceBlocks.remove(block);
                return true;
            }
        }

        return false;
    }
    
    /**
     * Rompe bloques de hielo en la dirección del helado (efecto dominó)
     * DEPRECATED: Este método se mantiene por compatibilidad pero no se usa
     */
    public int breakIceBlocks() {
        if (iceCream == null || !iceCream.canBreakIce()) {
            return 0;
        }

        Direction direction = iceCream.getCurrentDirection();
        Position currentPos = iceCream.getPosition().move(direction);
        int brokenBlocks = 0;

        // Romper bloques en línea recta (efecto dominó)
        while (isInBounds(currentPos) && hasIceBlock(currentPos)) {
            IceBlock block = getIceBlockAt(currentPos);
            if (block != null && block.isBreakable()) {
                iceBlocks.remove(block);
                brokenBlocks++;
                currentPos = currentPos.move(direction);
            } else {
                break;  // Bloque no rompible, detener
            }
        }

        return brokenBlocks;
    }

    /**
     * Remueve un bloque de hielo en una posición
     */
    public boolean removeIceBlock(Position pos) {
        IceBlock block = getIceBlockAt(pos);
        if (block != null && block.isBreakable()) {
            iceBlocks.remove(block);
            return true;
        }
        return false;
    }

    /**
     * Obtiene la posición del helado (para IA de enemigos)
     */
    public Position getIceCreamPosition() {
        if (iceCream != null && iceCream.isAlive()) {
            return iceCream.getPosition();
        }
        return null;
    }

    /**
     * Obtiene todas las posiciones vacías del tablero
     */
    public List<Position> getEmptyPositions() {
        List<Position> emptyPositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (isValidPosition(pos) && getFruitAt(pos) == null && 
                    (iceCream == null || !iceCream.getPosition().equals(pos)) &&
                    getEnemyAt(pos) == null) {
                    emptyPositions.add(pos);
                }
            }
        }
        return emptyPositions;
    }

    /**
     * Cuenta cuántas frutas faltan por recolectar
     */
    public int getRemainingFruits() {
        int count = 0;
        for (Fruit fruit : fruits) {
            if (!fruit.isCollected()) {
                count++;
            }
        }
        return count;
    }

    // Getters y Setters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public IceCream getIceCream() {
        return iceCream;
    }

    public void setIceCream(IceCream iceCream) {
        this.iceCream = iceCream;
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public List<Fruit> getFruits() {
        return new ArrayList<>(fruits);
    }

    public void addFruit(Fruit fruit) {
        this.fruits.add(fruit);
    }

    public List<IceBlock> getIceBlocks() {
        return new ArrayList<>(iceBlocks);
    }

    public void addIceBlock(IceBlock block) {
        this.iceBlocks.add(block);
    }

    public void addWall(Position pos) {
        if (!walls.contains(pos)) {
            walls.add(pos);
        }
    }

    public List<Position> getWalls() {
        return new ArrayList<>(walls);
    }
}
