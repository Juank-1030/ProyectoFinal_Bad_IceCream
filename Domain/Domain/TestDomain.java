package Domain;

public class TestDomain {
    public static void main(String[] args) {
        System.out.println("=== PROBANDO DOMAIN BAD DOPO-CREAM ===\n");
        
        // 1. Crear un juego en modo PvsM
        Game game = new Game(GameMode.PLAYER_VS_MACHINE, "fresa");
        System.out.println("✓ Juego creado en modo PvsM");
        System.out.println("✓ Helado: " + game.getIceCreamFlavor());
        
        // 2. Iniciar el nivel 1
        game.startLevel(1);
        System.out.println("\n=== NIVEL 1 INICIADO ===");
        System.out.println("Tiempo límite: " + game.getCurrentLevel().getTimeLimit() + " segundos");
        System.out.println("Frutas objetivo: " + game.getCurrentLevel().getFruitConfigs().size() + " tipos");
        
        // 3. Verificar el tablero
        Board board = game.getBoard();
        System.out.println("\n=== TABLERO ===");
        System.out.println("Dimensiones: " + board.getWidth() + "x" + board.getHeight());
        System.out.println("Enemigos: " + board.getEnemies().size());
        System.out.println("Frutas: " + board.getFruits().size());
        System.out.println("Frutas restantes: " + board.getRemainingFruits());
        
        // 4. Probar movimiento del helado
        System.out.println("\n=== PROBANDO MOVIMIENTO ===");
        IceCream iceCream = board.getIceCream();
        Position posInicial = iceCream.getPosition();
        System.out.println("Posición inicial helado: " + posInicial);
        
        // Intentar mover derecha
        boolean movido = game.moveIceCream(Direction.RIGHT);
        System.out.println("Movimiento DERECHA: " + (movido ? "✓ Exitoso" : "✗ Bloqueado"));
        if (movido) {
            System.out.println("Nueva posición: " + iceCream.getPosition());
        }
        
        // Intentar mover abajo
        movido = game.moveIceCream(Direction.DOWN);
        System.out.println("Movimiento ABAJO: " + (movido ? "✓ Exitoso" : "✗ Bloqueado"));
        if (movido) {
            System.out.println("Nueva posición: " + iceCream.getPosition());
        }
        
        // 5. Probar crear bloque de hielo
        System.out.println("\n=== PROBANDO BLOQUES DE HIELO ===");
        int bloquesAntes = board.getIceBlocks().size();
        boolean creado = game.createIceBlock();
        System.out.println("Crear bloque: " + (creado ? "✓ Creado" : "✗ No se pudo crear"));
        System.out.println("Bloques antes: " + bloquesAntes + ", Bloques después: " + board.getIceBlocks().size());
        
        // 6. Probar romper bloques
        if (creado) {
            int bloqueRotos = game.breakIceBlocks();
            System.out.println("Bloques rotos: " + bloqueRotos);
        }
        
        // 7. Ver información de enemigos
        System.out.println("\n=== ENEMIGOS ===");
        for (Enemy enemy : board.getEnemies()) {
            System.out.println("- " + enemy.getEnemyType() + " en " + enemy.getPosition());
            System.out.println("  Puede romper hielo: " + enemy.canBreakIce());
            System.out.println("  Comportamiento: " + enemy.getMovementBehavior().getClass().getSimpleName());
        }
        
        // 8. Ver información de frutas
        System.out.println("\n=== FRUTAS ===");
        for (Fruit fruit : board.getFruits()) {
            if (!fruit.isCollected()) {
                System.out.println("- " + fruit.getType() + " en " + fruit.getPosition());
                System.out.println("  Puntos: " + fruit.getPoints());
            }
        }
        
        // 9. Simular algunos updates del juego
        System.out.println("\n=== SIMULANDO 5 UPDATES ===");
        for (int i = 0; i < 5; i++) {
            game.update();
            System.out.println("Update " + (i+1) + " - Estado: " + game.getGameState() + 
                             ", Tiempo restante: " + game.getRemainingTime() + "s");
            
            // Mover helado aleatoriamente
            Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
            Direction randomDir = dirs[(int)(Math.random() * 4)];
            game.moveIceCream(randomDir);
            
            try {
                Thread.sleep(100); // Pausa de 100ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 10. Estado final
        System.out.println("\n=== ESTADO FINAL ===");
        System.out.println("Estado del juego: " + game.getGameState());
        System.out.println("Puntuación: " + game.getScore());
        System.out.println("Vidas restantes: " + game.getLivesRemaining());
        System.out.println("Frutas restantes: " + board.getRemainingFruits());
        System.out.println("Helado vivo: " + iceCream.isAlive());
        System.out.println("Frutas recolectadas por helado: " + iceCream.getFruitsCollected());
        
        System.out.println("\n=== PRUEBA COMPLETADA ✓ ===");
    }
}