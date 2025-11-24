package Presentation;

import Controller.GameController;
import Domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * GamePanel - Panel que renderiza el juego
 * 
 * RESPONSABILIDADES:
 * - Dibujar el tablero
 * - Dibujar entidades (helado, enemigos, frutas, bloques)
 * - Mostrar UI (puntos, tiempo, vidas)
 * - Mostrar mensajes (pausa, victoria, derrota)
 * 
 * NO TIENE:
 * - Lógica del juego (eso está en Game)
 * - Captura de eventos (eso está en GameController)
 */
public class GamePanel extends JPanel {
    
    private GameController controller;
    
    // Configuración visual
    private static final int CELL_SIZE = 40;  // Tamaño de cada celda en píxeles
    private static final int UI_HEIGHT = 100;  // Altura del panel de UI
    
    // Colores
    private static final Color COLOR_BACKGROUND = new Color(230, 230, 250);
    private static final Color COLOR_WALL = new Color(80, 80, 80);
    private static final Color COLOR_ICE_BLOCK = new Color(173, 216, 230);
    private static final Color COLOR_GRID = new Color(200, 200, 200);
    
    // Colores de helados
    private static final Color COLOR_VANILLA = new Color(255, 255, 224);
    private static final Color COLOR_STRAWBERRY = new Color(255, 182, 193);
    private static final Color COLOR_CHOCOLATE = new Color(139, 69, 19);
    
    // Colores de enemigos
    private static final Color COLOR_TROLL = new Color(34, 139, 34);
    private static final Color COLOR_POT = new Color(160, 82, 45);
    private static final Color COLOR_SQUID = new Color(255, 140, 0);
    
    // Colores de frutas
    private static final Color COLOR_GRAPE = new Color(128, 0, 128);
    private static final Color COLOR_BANANA = new Color(255, 255, 0);
    private static final Color COLOR_PINEAPPLE = new Color(255, 215, 0);
    private static final Color COLOR_CHERRY = new Color(220, 20, 60);
    
    /**
     * Constructor
     */
    public GamePanel(GameController controller) {
        this.controller = controller;
        
        // Configurar el panel
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(COLOR_BACKGROUND);
        
        // Calcular tamaño del panel
        updatePanelSize();
    }
    
    /**
     * Actualiza el tamaño del panel según el tamaño del tablero
     */
    private void updatePanelSize() {
        Game game = controller.getGame();
        if (game != null && game.getBoard() != null) {
            Board board = game.getBoard();
            int width = board.getWidth() * CELL_SIZE;
            int height = board.getHeight() * CELL_SIZE + UI_HEIGHT;
            setPreferredSize(new Dimension(width, height));
        } else {
            // Tamaño por defecto
            setPreferredSize(new Dimension(600, 540));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Game game = controller.getGame();
        if (game == null) {
            drawMessage(g, "Cargando...");
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar según el estado del juego
        GameState state = game.getGameState();
        
        if (state == GameState.PLAYING || state == GameState.PAUSED) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            
            if (state == GameState.PAUSED) {
                drawPauseOverlay(g2d);
            }
        } else if (state == GameState.WON) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            drawVictoryOverlay(g2d, game);
        } else if (state == GameState.LOST) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            drawGameOverOverlay(g2d, game);
        }
    }
    
    /**
     * Dibuja el juego completo (tablero y entidades)
     */
    private void drawGame(Graphics2D g, Game game) {
        Board board = game.getBoard();
        
        // Dibujar grid
        drawGrid(g, board);
        
        // Dibujar paredes
        drawWalls(g, board);
        
        // Dibujar bloques de hielo
        drawIceBlocks(g, board);
        
        // Dibujar frutas
        drawFruits(g, board);
        
        // Dibujar enemigos
        drawEnemies(g, board);
        
        // Dibujar helado
        drawIceCream(g, board);
    }
    
    /**
     * Dibuja la cuadrícula del tablero
     */
    private void drawGrid(Graphics2D g, Board board) {
        g.setColor(COLOR_GRID);
        g.setStroke(new BasicStroke(1));
        
        // Líneas verticales
        for (int x = 0; x <= board.getWidth(); x++) {
            g.drawLine(x * CELL_SIZE, UI_HEIGHT, x * CELL_SIZE, 
                      board.getHeight() * CELL_SIZE + UI_HEIGHT);
        }
        
        // Líneas horizontales
        for (int y = 0; y <= board.getHeight(); y++) {
            g.drawLine(0, y * CELL_SIZE + UI_HEIGHT, 
                      board.getWidth() * CELL_SIZE, y * CELL_SIZE + UI_HEIGHT);
        }
    }
    
    /**
     * Dibuja las paredes
     */
    private void drawWalls(Graphics2D g, Board board) {
        g.setColor(COLOR_WALL);
        
        List<Position> walls = board.getWalls();
        for (Position wall : walls) {
            int x = wall.getX() * CELL_SIZE;
            int y = wall.getY() * CELL_SIZE + UI_HEIGHT;
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            
            // Borde
            g.setColor(COLOR_WALL.darker());
            g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            g.setColor(COLOR_WALL);
        }
    }
    
    /**
     * Dibuja los bloques de hielo
     */
    private void drawIceBlocks(Graphics2D g, Board board) {
        g.setColor(COLOR_ICE_BLOCK);
        
        List<IceBlock> iceBlocks = board.getIceBlocks();
        for (IceBlock block : iceBlocks) {
            Position pos = block.getPosition();
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;
            
            g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            
            // Borde
            g.setColor(COLOR_ICE_BLOCK.darker());
            g.setStroke(new BasicStroke(2));
            g.drawRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            g.setColor(COLOR_ICE_BLOCK);
        }
    }
    
    /**
     * Dibuja las frutas
     */
    private void drawFruits(Graphics2D g, Board board) {
        List<Fruit> fruits = board.getFruits();
        
        for (Fruit fruit : fruits) {
            if (!fruit.isCollected()) {
                Position pos = fruit.getPosition();
                int x = pos.getX() * CELL_SIZE + CELL_SIZE / 2;
                int y = pos.getY() * CELL_SIZE + CELL_SIZE / 2 + UI_HEIGHT;
                
                // Color según tipo
                Color color;
                switch (fruit.getFruitType().toLowerCase()) {
                    case "uvas": color = COLOR_GRAPE; break;
                    case "plátano": color = COLOR_BANANA; break;
                    case "piña": color = COLOR_PINEAPPLE; break;
                    case "cereza": color = COLOR_CHERRY; break;
                    default: color = Color.MAGENTA;
                }
                
                g.setColor(color);
                g.fillOval(x - 10, y - 10, 20, 20);
                
                // Borde
                g.setColor(color.darker());
                g.drawOval(x - 10, y - 10, 20, 20);
            }
        }
    }
    
    /**
     * Dibuja los enemigos
     */
    private void drawEnemies(Graphics2D g, Board board) {
        List<Enemy> enemies = board.getEnemies();
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                Position pos = enemy.getPosition();
                int x = pos.getX() * CELL_SIZE + 5;
                int y = pos.getY() * CELL_SIZE + 5 + UI_HEIGHT;
                
                // Color según tipo
                Color color;
                switch (enemy.getEnemyType().toLowerCase()) {
                    case "troll": color = COLOR_TROLL; break;
                    case "maceta": color = COLOR_POT; break;
                    case "calamar naranja": color = COLOR_SQUID; break;
                    default: color = Color.RED;
                }
                
                g.setColor(color);
                g.fillRect(x, y, CELL_SIZE - 10, CELL_SIZE - 10);
                
                // Borde
                g.setColor(color.darker());
                g.setStroke(new BasicStroke(2));
                g.drawRect(x, y, CELL_SIZE - 10, CELL_SIZE - 10);
            }
        }
    }
    
    /**
     * Dibuja el helado
     */
    private void drawIceCream(Graphics2D g, Board board) {
        IceCream iceCream = board.getIceCream();
        
        if (iceCream != null && iceCream.isAlive()) {
            Position pos = iceCream.getPosition();
            int x = pos.getX() * CELL_SIZE + CELL_SIZE / 2;
            int y = pos.getY() * CELL_SIZE + CELL_SIZE / 2 + UI_HEIGHT;
            
            // Color según sabor
            Color color;
            switch (iceCream.getFlavor().toLowerCase()) {
                case "vainilla": color = COLOR_VANILLA; break;
                case "fresa": color = COLOR_STRAWBERRY; break;
                case "chocolate": color = COLOR_CHOCOLATE; break;
                default: color = Color.WHITE;
            }
            
            g.setColor(color);
            g.fillOval(x - 15, y - 15, 30, 30);
            
            // Borde
            g.setColor(color.darker());
            g.setStroke(new BasicStroke(3));
            g.drawOval(x - 15, y - 15, 30, 30);
        }
    }
    
    /**
     * Dibuja la interfaz de usuario (puntos, tiempo, vidas)
     */
    private void drawUI(Graphics2D g, Game game) {
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, getWidth(), UI_HEIGHT);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Puntos
        g.drawString("PUNTOS: " + game.getScore(), 20, 35);
        
        // Tiempo
        int minutes = game.getRemainingTime() / 60;
        int seconds = game.getRemainingTime() % 60;
        String timeStr = String.format("TIEMPO: %02d:%02d", minutes, seconds);
        g.drawString(timeStr, 20, 65);
        
        // Vidas
        g.drawString("VIDAS: " + game.getLivesRemaining(), 250, 35);
        
        // Nivel
        if (game.getCurrentLevel() != null) {
            g.drawString("NIVEL: " + game.getCurrentLevel().getLevelNumber(), 250, 65);
        }
        
        // Frutas restantes
        g.drawString("FRUTAS: " + game.getBoard().getRemainingFruits(), 450, 35);
    }
    
    /**
     * Dibuja overlay de pausa
     */
    private void drawPauseOverlay(Graphics2D g) {
        // Semi-transparente
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        
        String msg = "PAUSADO";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        
        g.drawString(msg, x, y);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        msg = "Presiona P o ESC para continuar";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, y + 40);
    }
    
    /**
     * Dibuja overlay de victoria
     */
    private void drawVictoryOverlay(Graphics2D g, Game game) {
        g.setColor(new Color(0, 100, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 56));
        
        String msg = "¡VICTORIA!";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2 - 50;
        
        g.drawString(msg, x, y);
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        msg = "Puntuación: " + game.getScore();
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, y + 60);
    }
    
    /**
     * Dibuja overlay de game over
     */
    private void drawGameOverOverlay(Graphics2D g, Game game) {
        g.setColor(new Color(100, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 56));
        
        String msg = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2 - 50;
        
        g.drawString(msg, x, y);
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        msg = "Puntuación final: " + game.getScore();
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, y + 60);
    }
    
    /**
     * Dibuja un mensaje centrado
     */
    private void drawMessage(Graphics g, String message) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        
        g.drawString(message, x, y);
    }
}
