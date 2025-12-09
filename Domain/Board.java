package Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el tablero de juego (matriz)
 * Gestiona la posición de todos los elementos: helados, enemigos, frutas,
 * bloques de hielo
 */
public class Board implements BoardStateProvider {
    private static final long serialVersionUID = 1L;

    // Dimensiones del tablero
    private int width;
    private int height;

    // Entidades del juego
    private IceCream iceCream;
    private IceCream secondIceCream; // Segundo helado para modo cooperativo
    private List<Enemy> enemies;
    private List<Fruit> fruits;
    private List<Position> walls;        // Muros indestructibles (bordes)
    private List<IceBlock> iceBlocks;   // Bloques de hielo rompibles

    // Matriz de celdas (para búsqueda rápida)
    private CellType[][] cells;

    /**
     * Constructor del tablero
     * 
     * @param width  Ancho del tablero
     * @param height Alto del tablero
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.enemies = new ArrayList<>();
        this.fruits = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.iceBlocks = new ArrayList<>();
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

        // Verificar si hay muro (indestructible)
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
     * Verifica si hay un muro en la posición
     */
    public boolean isWall(Position pos) {
        for (Position wall : walls) {
            if (wall.equals(pos)) {
                return true;
            }
        }
        return false;
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
     * CAMBIO: Retorna null si hay un bloque de hielo en la misma posición
     * (la fruta no puede ser recolectada si está bajo un bloque de hielo)
     */
    public Fruit getFruitAt(Position pos) {
        for (Fruit fruit : fruits) {
            if (fruit.getPosition().equals(pos) && !fruit.isCollected()) {
                // Verificar si hay bloque de hielo en la misma posición
                if (hasIceBlock(pos)) {
                    // Hay hielo, no devolver la fruta (protegida)
                    return null;
                }
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
     * Obtiene una fruta en la posición sin verificar si hay bloque de hielo
     * Utilizado para renderizar frutas que estén bajo bloques de hielo
     * 
     * @return La fruta en la posición, incluso si está bajo hielo
     */
    public Fruit getFruitAtForRendering(Position pos) {
        for (Fruit fruit : fruits) {
            if (fruit.getPosition().equals(pos) && !fruit.isCollected()) {
                return fruit;
            }
        }
        return null;
    }

    /**
     * Mueve el helado a una nueva posición
     * Los bloques de hielo BLOQUEAN el paso (no se rompen automáticamente)
     * 
     * @return true si el movimiento fue exitoso
     */
    public boolean moveIceCream(Direction direction) {
        if (iceCream == null || !iceCream.isAlive()) {
            return false;
        }

        // Verificar si es tiempo de moverse según su velocidad
        if (!iceCream.canMoveNow()) {
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
            lastCollectedFruit = fruit; // Guardar para que Game sume puntos
        }

        // Verificar colisión con enemigo
        Enemy enemy = getEnemyAt(newPos);
        if (enemy != null) {
            iceCream.setAlive(false); // El helado muere
        }

        return true;
    }

    /**
     * Mueve el segundo helado (modo cooperativo)
     */
    public boolean moveSecondIceCream(Direction direction) {
        if (secondIceCream == null || !secondIceCream.isAlive()) {
            return false;
        }

        // Verificar si es tiempo de moverse según su velocidad
        if (!secondIceCream.canMoveNow()) {
            return false;
        }

        Position newPos = secondIceCream.getNextPosition(direction);

        if (!isValidPosition(newPos)) {
            return false;
        }

        // Actualizar posición
        secondIceCream.updatePosition(newPos);
        secondIceCream.setCurrentDirection(direction);

        // Verificar colisión con fruta
        Fruit fruit = getFruitAt(newPos);
        if (fruit != null && !fruit.isCollected()) {
            fruit.collect();
            secondIceCream.collectFruit();
            lastCollectedFruit = fruit;
        }

        // Verificar colisión con enemigo
        Enemy enemy = getEnemyAt(newPos);
        if (enemy != null) {
            secondIceCream.setAlive(false); // El segundo helado muere
        }

        return true;
    }

    private Fruit lastCollectedFruit = null;

    /**
     * Obtiene y limpia la última fruta recolectada
     * 
     * @return La última fruta recolectada, o null
     */
    public Fruit getAndClearLastCollectedFruit() {
        Fruit fruit = lastCollectedFruit;
        lastCollectedFruit = null;
        return fruit;
    }

    /**
     * Mueve un enemigo
     * ACTUALIZADO: Cambia el sprite según la acción
     */
    public boolean moveEnemy(Enemy enemy, Direction direction) {
        if (enemy == null || !enemy.isAlive()) {
            return false;
        }

        // Verificar si es tiempo de moverse según su velocidad
        if (!enemy.canMoveNow()) {
            return false;
        }

        // Caso especial: Narval en modo carga
        if (enemy instanceof Narval) {
            Narval narval = (Narval) enemy;
            if (narval.isCharging()) {
                // NUEVO: Cambiar acción a "break" durante la carga
                narval.setCurrentAction("break");

                // En modo carga: intentar avanzar continuamente en la dirección de carga
                Position newPos = narval.getPosition().move(narval.getChargeDirection());

                // Verificar si está fuera de los límites del mapa
                if (!isInBounds(newPos)) {
                    // Chocó con el borde del mapa
                    narval.deactivateCharge();
                    narval.setCurrentAction("stand"); // NUEVO
                    return false;
                }

                // Romper hielo si hay
                if (hasIceBlock(newPos)) {
                    removeIceBlock(newPos);
                    System.out.println("💥 Narval rompiendo bloque en carga");
                    narval.updatePosition(newPos);
                    narval.setCurrentDirection(narval.getChargeDirection());
                    return true;
                }

                // Verificar colisión con helado
                if (iceCream != null && iceCream.getPosition().equals(newPos)) {
                    narval.updatePosition(newPos);
                    narval.setCurrentDirection(narval.getChargeDirection());
                    iceCream.setAlive(false);
                    System.out.println("💥 Narval chocó contra el helado");
                    narval.deactivateCharge();
                    narval.setCurrentAction("stand"); // NUEVO
                    return true;
                }

                // Si es posición válida y vacía, moverse
                if (isValidPosition(newPos)) {
                    narval.updatePosition(newPos);
                    narval.setCurrentDirection(narval.getChargeDirection());
                    return true;
                }

                // Algo más bloqueó el camino, detener carga
                narval.deactivateCharge();
                narval.setCurrentAction("stand"); // NUEVO
                return false;
            }
        }

        Position newPos = enemy.getNextPosition(direction);

        // Los enemigos pueden intentar romper bloques si tienen la habilidad
        // EXCEPCIÓN: YellowSquid NO rompe automáticamente (requiere ESPACIO)
        if (!isValidPosition(newPos)) {
            if (enemy.canBreakIce() && hasIceBlock(newPos) && !(enemy instanceof YellowSquid)) {
                // NUEVO: Cambiar acción a "break" al romper hielo
                enemy.setCurrentAction("break");

                removeIceBlock(newPos);
                enemy.updatePosition(newPos);
                enemy.setCurrentDirection(direction);

                // Ejecutar habilidad especial del enemigo
                enemy.executeAbility();

                // NUEVO: Volver a "stand" después de un delay
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (!enemy.getCurrentAction().equals("walk")) {
                            enemy.setCurrentAction("stand");
                        }
                    }
                }, 200);

                return true;
            }
            return false;
        }

        // NUEVO: Cambiar acción a "walk" cuando se mueve normalmente
        enemy.setCurrentAction("walk");

        enemy.updatePosition(newPos);
        enemy.setCurrentDirection(direction);

        // Ejecutar habilidad especial del enemigo
        enemy.executeAbility();

        // Verificar colisión con helado
        if (iceCream != null && iceCream.getPosition().equals(newPos)) {
            iceCream.setAlive(false);
        }

        return true;
    }

    /**
     * Intenta que YellowSquid rompa un bloque de hielo en su dirección actual
     * ACTUALIZADO: Cambia la acción a "break"
     */
    public boolean yellowSquidBreakIce(YellowSquid squid) {
        if (squid == null || !squid.isAlive()) {
            return false;
        }

        Direction direction = squid.getCurrentDirection();
        Position targetPos = squid.getPosition().move(direction);

        // Verificar si hay un bloque en la dirección apuntada
        if (isInBounds(targetPos) && hasIceBlock(targetPos)) {
            // NUEVO: Cambiar acción a "break"
            squid.setCurrentAction("break");

            // Incrementar contador de golpes
            squid.executeAbility();

            // Verificar si llegó a 3 golpes (executeAbility resetea a 0 después de 3)
            if (squid.getIceBreakCounter() == 0) {
                // Acababa de completar 3 golpes, se rompió el bloque
                removeIceBlock(targetPos);
                System.out.println("💥 ¡Bloque de hielo roto!");

                // NUEVO: Volver a "stand" después de un delay
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        squid.setCurrentAction("stand");
                    }
                }, 300);

                return true;
            }

            // NUEVO: Volver a "stand" si no rompió aún
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (!squid.getCurrentAction().equals("walk")) {
                        squid.setCurrentAction("stand");
                    }
                }
            }, 200);

            return false;
        }

        return false;
    }

    /**
     * Maneja la carga del Narval
     * NO mueve inmediatamente, solo activa la carga
     * El movimiento se hace gradualmente respetando la velocidad
     * 
     * @param narval    El Narval que ejecuta la carga
     * @param direction Dirección de la carga
     */
    public void executeNarvalCharge(Narval narval, Direction direction) {
        // Solo marcar que la carga está activa
        // El movimiento se hará gradualmente respetando la velocidad
        if (narval.isCharging()) {
            // Si hay una carga activa, detenerse
            narval.deactivateCharge();
        }
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
        while (isInBounds(currentPos)) {
            // Verificar si hay enemigo (no crear bloque ahí)
            if (getEnemyAt(currentPos) != null) {
                break;
            }

            // Verificar si ya hay bloque de hielo (detener)
            if (hasIceBlock(currentPos)) {
                break;
            }

            // Verificar si es posición válida (sin hielo, sin pared)
            if (!isValidPosition(currentPos)) {
                // Si hay una fruta, crear bloque de hielo SOBRE la fruta
                Fruit fruitAtPos = getFruitAt(currentPos);
                if (fruitAtPos != null) {
                    // Crear bloque de hielo en la misma posición que la fruta
                    IceBlock newBlock = new IceBlock(currentPos, true, iceCream);
                    iceBlocks.add(newBlock);
                    blocksCreated++;
                    currentPos = currentPos.move(direction);
                    continue; // Seguir creando bloques
                } else {
                    // Hay pared o algo más, detener
                    break;
                }
            }

            // Crear bloque en posición válida
            IceBlock newBlock = new IceBlock(currentPos, true, iceCream);
            iceBlocks.add(newBlock);
            blocksCreated++;
            currentPos = currentPos.move(direction);
        }

        return blocksCreated;
    }

    /**
     * Crea bloques de hielo para el segundo helado
     */
    public int createIceBlockSecond() {
        if (secondIceCream == null || !secondIceCream.canCreateIce()) {
            return 0;
        }

        Direction direction = secondIceCream.getCurrentDirection();
        Position currentPos = secondIceCream.getPosition().move(direction);
        int blocksCreated = 0;

        // Crear bloques en línea recta hasta encontrar obstáculo
        while (isInBounds(currentPos)) {
            // Verificar si hay enemigo (no crear bloque ahí)
            if (getEnemyAt(currentPos) != null) {
                break;
            }

            // Verificar si ya hay bloque de hielo (detener)
            if (hasIceBlock(currentPos)) {
                break;
            }

            // Verificar si es posición válida (sin hielo, sin pared)
            if (!isValidPosition(currentPos)) {
                // Si hay una fruta, crear bloque de hielo SOBRE la fruta
                Fruit fruitAtPos = getFruitAt(currentPos);
                if (fruitAtPos != null) {
                    // Crear bloque de hielo en la misma posición que la fruta
                    IceBlock newBlock = new IceBlock(currentPos, true, secondIceCream);
                    iceBlocks.add(newBlock);
                    blocksCreated++;
                    currentPos = currentPos.move(direction);
                    continue; // Seguir creando bloques
                } else {
                    // Hay pared o algo más, detener
                    break;
                }
            }

            // Crear bloque en posición válida
            IceBlock newBlock = new IceBlock(currentPos, true, secondIceCream);
            iceBlocks.add(newBlock);
            blocksCreated++;
            currentPos = currentPos.move(direction);
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
     * Toggle de hielo: Verifica si hay bloques en la dirección
     * - Si HAY bloques: Los rompe todos (efecto dominó)
     * - Si NO hay bloques: Los crea en hilera
     * 
     * Devuelve: >0 si creó bloques, <0 si rompió bloques, 0 si no pudo hacer nada
     */
    public int toggleIceBlocks() {
        if (iceCream == null) {
            return 0;
        }

        Direction direction = iceCream.getCurrentDirection();
        Position checkPos = iceCream.getPosition().move(direction);

        // Verificar si hay un bloque de hielo en la posición inmediata
        if (isInBounds(checkPos) && hasIceBlock(checkPos)) {
            // HAY BLOQUES: Romper en efecto dominó
            if (!iceCream.canBreakIce()) {
                return 0;
            }
            return -breakIceBlocks(); // Devuelve negativo para indicar ruptura
        } else {
            // NO HAY BLOQUES: Crear hilera
            if (!iceCream.canCreateIce()) {
                return 0;
            }
            return createIceBlock(); // Devuelve positivo para indicar creación
        }
    }

    /**
     * Toggle de hielo para el segundo helado (modo cooperativo)
     */
    public int toggleIceBlocksSecond() {
        if (secondIceCream == null) {
            return 0;
        }

        Direction direction = secondIceCream.getCurrentDirection();
        Position checkPos = secondIceCream.getPosition().move(direction);

        // Verificar si hay un bloque de hielo en la posición inmediata
        if (isInBounds(checkPos) && hasIceBlock(checkPos)) {
            // HAY BLOQUES: Romper en efecto dominó
            if (!secondIceCream.canBreakIce()) {
                return 0;
            }
            return -breakIceBlocksSecond(); // Devuelve negativo para indicar ruptura
        } else {
            // NO HAY BLOQUES: Crear hilera
            if (!secondIceCream.canCreateIce()) {
                return 0;
            }
            return createIceBlockSecond(); // Devuelve positivo para indicar creación
        }
    }

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
                break; // Bloque no rompible, detener
            }
        }

        return brokenBlocks;
    }

    /**
     * Rompe bloques de hielo para el segundo helado
     */
    public int breakIceBlocksSecond() {
        if (secondIceCream == null || !secondIceCream.canBreakIce()) {
            return 0;
        }

        Direction direction = secondIceCream.getCurrentDirection();
        Position currentPos = secondIceCream.getPosition().move(direction);
        int brokenBlocks = 0;

        // Romper bloques en línea recta (efecto dominó)
        while (isInBounds(currentPos) && hasIceBlock(currentPos)) {
            IceBlock block = getIceBlockAt(currentPos);
            if (block != null && block.isBreakable()) {
                iceBlocks.remove(block);
                brokenBlocks++;
                currentPos = currentPos.move(direction);
            } else {
                break; // Bloque no rompible, detener
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

    public IceCream getSecondIceCream() {
        return secondIceCream;
    }

    public void setSecondIceCream(IceCream secondIceCream) {
        this.secondIceCream = secondIceCream;
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

    public List<Position> getWalls() {
        return new ArrayList<>(walls);
    }

    public void addWall(Position pos) {
        if (!walls.contains(pos)) {
            walls.add(pos);
        }
    }
}