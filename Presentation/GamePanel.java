package Presentation;

import Controller.GameController;
import Domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private Runnable onReturnToMenuClick; // Callback para volver al menú
    private Runnable onSaveGameClick; // Callback para guardar partida
    private Runnable onContinueGameClick; // Callback para continuar partida

    // Configuración visual
    private static final int CELL_SIZE = 40; // Tamaño de cada celda en píxeles
    private static final int UI_HEIGHT = 100; // Altura del panel de UI
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_MARGIN = 10;
    private static final int PAUSE_BUTTON_WIDTH = 150;
    private static final int PAUSE_BUTTON_HEIGHT = 50;
    private static final int PAUSE_BUTTON_MARGIN = 20;

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

        // Cargar todos los sprites
        ImageLoader.loadAllImages();

        // Calcular tamaño del panel
        updatePanelSize();

        // Agregar mouse listener para el botón de volver al menú
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    /**
     * Maneja los clics del mouse para detectar botones
     */
    private void handleMouseClick(java.awt.event.MouseEvent e) {
        Game game = controller.getGame();

        // En modo juego normal: botón "Volver al Menú" (esquina superior derecha)
        if (game != null && (game.getGameState() == GameState.PLAYING)) {
            int buttonX = getWidth() - BUTTON_WIDTH - BUTTON_MARGIN;
            int buttonY = BUTTON_MARGIN;

            // Verificar si se hizo clic en el botón
            if (e.getX() >= buttonX && e.getX() <= buttonX + BUTTON_WIDTH &&
                    e.getY() >= buttonY && e.getY() <= buttonY + BUTTON_HEIGHT) {
                if (onReturnToMenuClick != null) {
                    onReturnToMenuClick.run();
                }
            }
        }

        // En modo pausa: detectar botones de pausa
        if (game != null && game.getGameState() == GameState.PAUSED) {
            detectPauseButtonClick(e);
        }
    }

    /**
     * Detecta clics en los botones de pausa
     */
    private void detectPauseButtonClick(java.awt.event.MouseEvent e) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Posición del primer botón (Guardar Partida)
        int button1X = centerX - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        int button1Y = centerY + 50;

        // Posición del segundo botón (Volver al Menú)
        int button2X = centerX;
        int button2Y = centerY + 50;

        // Posición del tercer botón (Continuar)
        int button3X = centerX + PAUSE_BUTTON_WIDTH + PAUSE_BUTTON_MARGIN;
        int button3Y = centerY + 50;

        System.out.println("Click detectado en pausa. Posición: (" + e.getX() + ", " + e.getY() + ")");
        System.out.println("Botón 1 (Guardar): (" + button1X + ", " + button1Y + ") - (" + (button1X + PAUSE_BUTTON_WIDTH) + ", " + (button1Y + PAUSE_BUTTON_HEIGHT) + ")");
        System.out.println("Botón 2 (Menú): (" + button2X + ", " + button2Y + ") - (" + (button2X + PAUSE_BUTTON_WIDTH) + ", " + (button2Y + PAUSE_BUTTON_HEIGHT) + ")");
        System.out.println("Botón 3 (Continuar): (" + button3X + ", " + button3Y + ") - (" + (button3X + PAUSE_BUTTON_WIDTH) + ", " + (button3Y + PAUSE_BUTTON_HEIGHT) + ")");

        // Verificar botón "Guardar Partida"
        if (e.getX() >= button1X && e.getX() <= button1X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button1Y && e.getY() <= button1Y + PAUSE_BUTTON_HEIGHT) {
            System.out.println("✅ Botón Guardar Partida presionado");
            if (onSaveGameClick != null) {
                onSaveGameClick.run();
            }
        }
        // Verificar botón "Volver al Menú"
        else if (e.getX() >= button2X && e.getX() <= button2X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button2Y && e.getY() <= button2Y + PAUSE_BUTTON_HEIGHT) {
            System.out.println("✅ Botón Volver al Menú presionado");
            if (onReturnToMenuClick != null) {
                onReturnToMenuClick.run();
            }
        }
        // Verificar botón "Continuar"
        else if (e.getX() >= button3X && e.getX() <= button3X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button3Y && e.getY() <= button3Y + PAUSE_BUTTON_HEIGHT) {
            System.out.println("✅ Botón Continuar presionado");
            if (onContinueGameClick != null) {
                onContinueGameClick.run();
            }
        }
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

        // CAMBIO: Dibujar frutas ANTES que bloques de hielo
        // Así las frutas aparecen debajo y el hielo encima
        drawFruits(g, board);

        // Dibujar bloques de hielo (encima de frutas)
        drawIceBlocks(g, board);

        // Dibujar enemigos
        drawEnemies(g, board);

        // Dibujar helados
        drawIceCream(g, board);
        drawSecondIceCream(g, board);
    }

    /**
     * Dibuja la cuadrícula del tablero
     * ACTUALIZADO: Ahora dibuja el fondo del mapa en lugar de la cuadrícula
     */
    private void drawGrid(Graphics2D g, Board board) {
        // Dibujar imagen de fondo del mapa
        Image mapBackground = ImageLoader.getMapBackground();
        
        if (mapBackground != null) {
            // Escalar la imagen al tamaño del tablero
            int width = board.getWidth() * CELL_SIZE;
            int height = board.getHeight() * CELL_SIZE;
            g.drawImage(mapBackground, 0, UI_HEIGHT, width, height, null);
        }
        
        // Ya NO dibujamos las líneas de la cuadrícula
        // Las siguientes líneas están comentadas para eliminar la cuadrícula
        /*
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
        */
    }

    /**
     * Dibuja las paredes
     * ACTUALIZADO: Sin borde grueso
     */
    private void drawWalls(Graphics2D g, Board board) {
        g.setColor(COLOR_WALL);

        List<Position> walls = board.getWalls();
        for (Position wall : walls) {
            int x = wall.getX() * CELL_SIZE;
            int y = wall.getY() * CELL_SIZE + UI_HEIGHT;
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            // Borde sutil (ya no grueso)
            // Comentado para un aspecto más limpio
            // g.setColor(COLOR_WALL.darker());
            // g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            // g.setColor(COLOR_WALL);
        }
    }

    /**
     * Dibuja los bloques de hielo
     * ACTUALIZADO: Usa sprites en lugar de rectángulos
     */
    private void drawIceBlocks(Graphics2D g, Board board) {
        List<IceBlock> iceBlocks = board.getIceBlocks();
        
        for (IceBlock block : iceBlocks) {
            Position pos = block.getPosition();
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

            // Intentar usar sprite
            Image iceSprite = ImageLoader.getIceBlockSprite("static");
            
            if (iceSprite != null) {
                // Dibujar sprite escalado al tamaño de la celda
                g.drawImage(iceSprite, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                // Fallback: dibujar rectángulo azul como antes
                g.setColor(COLOR_ICE_BLOCK);
                g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);

                // Borde
                g.setColor(COLOR_ICE_BLOCK.darker());
                g.setStroke(new BasicStroke(2));
                g.drawRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                g.setColor(COLOR_ICE_BLOCK);
            }
        }
    }

    /**
     * Dibuja las frutas
     * ACTUALIZADO: Usa sprites en lugar de círculos de colores
     */
    private void drawFruits(Graphics2D g, Board board) {
        List<Fruit> fruits = board.getFruits();

        for (Fruit fruit : fruits) {
            if (!fruit.isCollected()) {
                Position pos = fruit.getPosition();
                int x = pos.getX() * CELL_SIZE;
                int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

                // Mapear nombre español a nombre inglés del sprite
                String fruitType = fruit.getFruitType().toLowerCase();
                String spriteType;
                switch (fruitType) {
                    case "uvas":
                        spriteType = "grapes";
                        break;
                    case "plátano":
                        spriteType = "banana";
                        break;
                    case "piña":
                        spriteType = "pineapple";
                        break;
                    case "cereza":
                        spriteType = "cherry";
                        break;
                    default:
                        spriteType = fruitType; // usar el nombre tal cual
                }

                // Intentar usar sprite
                Image fruitSprite = ImageLoader.getFruitSprite(spriteType, "normal");
                
                if (fruitSprite != null) {
                    // Dibujar sprite escalado al tamaño de la celda
                    g.drawImage(fruitSprite, x, y, CELL_SIZE, CELL_SIZE, null);
                } else {
                    // Fallback: dibujar círculo de color como antes
                    int centerX = x + CELL_SIZE / 2;
                    int centerY = y + CELL_SIZE / 2;
                    
                    Color color;
                    switch (fruitType) {
                        case "uvas":
                            color = COLOR_GRAPE;
                            break;
                        case "plátano":
                            color = COLOR_BANANA;
                            break;
                        case "piña":
                            color = COLOR_PINEAPPLE;
                            break;
                        case "cereza":
                            color = COLOR_CHERRY;
                            break;
                        default:
                            color = Color.MAGENTA;
                    }

                    g.setColor(color);
                    g.fillOval(centerX - 10, centerY - 10, 20, 20);

                    // Borde
                    g.setColor(color.darker());
                    g.drawOval(centerX - 10, centerY - 10, 20, 20);
                }
            }
        }
    }

    /**
     * Dibuja los enemigos
     * ACTUALIZADO: Usa sprites en lugar de rectángulos de colores
     */
    private void drawEnemies(Graphics2D g, Board board) {
        List<Enemy> enemies = board.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isAlive()) {
                Position pos = enemy.getPosition();
                int x = pos.getX() * CELL_SIZE;
                int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

                // Determinar tipo de monstruo
                String enemyType = enemy.getEnemyType().toLowerCase();
                String spriteType;
                switch (enemyType) {
                    case "troll":
                        spriteType = "troll";
                        break;
                    case "maceta":
                    case "pot":
                        spriteType = "pot";
                        break;
                    case "calamar":
                    case "yellowsquid":
                        spriteType = "yellowsquid";
                        break;
                    case "narval":
                        spriteType = "narval";
                        break;
                    default:
                        spriteType = "troll"; // fallback
                }

                // Intentar usar sprite (por ahora stand/down como default)
                Image enemySprite = ImageLoader.getMonsterSprite(spriteType, "walk", "down");
                
                if (enemySprite != null) {
                    // Dibujar sprite escalado al tamaño de la celda
                    g.drawImage(enemySprite, x, y, CELL_SIZE, CELL_SIZE, null);
                } else {
                    // Fallback: dibujar rectángulo de color como antes
                    int rectX = x + 5;
                    int rectY = y + 5;
                    
                    // Usar el color asignado al enemigo
                    Color color = enemy.getColor() != null ? enemy.getColor() : Color.RED;

                    g.setColor(color);
                    g.fillRect(rectX, rectY, CELL_SIZE - 10, CELL_SIZE - 10);

                    // Borde más grueso
                    g.setColor(color.darker());
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(rectX, rectY, CELL_SIZE - 10, CELL_SIZE - 10);
                }

                // Etiqueta en PVP: mostrar P2 para el primer monstruo
                Game game = controller.getGame();
                if (game != null && game.getGameMode() == GameMode.PVP && i == 0) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    g.drawString("P2", x + 5, y + 20);
                }
            }
        }
    }

    /**
     * Dibuja el helado
     * ACTUALIZADO: Usa sprites en lugar de círculos de colores
     */
    private void drawIceCream(Graphics2D g, Board board) {
        IceCream iceCream = board.getIceCream();

        if (iceCream != null && iceCream.isAlive()) {
            Position pos = iceCream.getPosition();
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

            // Mapear sabor español a inglés
            String flavor = iceCream.getFlavor().toLowerCase();
            String spriteFlavor;
            switch (flavor) {
                case "vainilla":
                    spriteFlavor = "vainillia"; // Nota: con doble 'l'
                    break;
                case "fresa":
                    spriteFlavor = "strawberry";
                    break;
                case "chocolate":
                    spriteFlavor = "chocolate";
                    break;
                default:
                    spriteFlavor = "vainillia"; // fallback
            }

            // Intentar usar sprite (StandDown por defecto)
            Image iceCreamSprite = ImageLoader.getIceCreamSprite(spriteFlavor, "stand", "down");
            
            if (iceCreamSprite != null) {
                // Dibujar sprite escalado al tamaño de la celda
                g.drawImage(iceCreamSprite, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                // Fallback: dibujar círculo de color como antes
                int centerX = x + CELL_SIZE / 2;
                int centerY = y + CELL_SIZE / 2;
                
                Color color;
                switch (flavor) {
                    case "vainilla":
                        color = COLOR_VANILLA;
                        break;
                    case "fresa":
                        color = COLOR_STRAWBERRY;
                        break;
                    case "chocolate":
                        color = COLOR_CHOCOLATE;
                        break;
                    default:
                        color = Color.WHITE;
                }

                g.setColor(color);
                g.fillOval(centerX - 15, centerY - 15, 30, 30);

                // Borde más grueso
                g.setColor(color.darker());
                g.setStroke(new BasicStroke(4));
                g.drawOval(centerX - 15, centerY - 15, 30, 30);
            }

            // Etiqueta en PVP
            Game game = controller.getGame();
            if (game != null && game.getGameMode() == GameMode.PVP) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("P1", x + CELL_SIZE / 2 - 8, y + CELL_SIZE / 2 + 5);
            }
        }
    }

    /**
     * Dibuja el segundo helado (modo cooperativo)
     * ACTUALIZADO: Usa sprites en lugar de círculos de colores
     */
    private void drawSecondIceCream(Graphics2D g, Board board) {
        IceCream secondIceCream = board.getSecondIceCream();

        if (secondIceCream != null && secondIceCream.isAlive()) {
            Position pos = secondIceCream.getPosition();
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

            // Mapear sabor español a inglés
            String flavor = secondIceCream.getFlavor().toLowerCase();
            String spriteFlavor;
            switch (flavor) {
                case "vainilla":
                    spriteFlavor = "vainillia"; // Nota: con doble 'l'
                    break;
                case "fresa":
                    spriteFlavor = "strawberry";
                    break;
                case "chocolate":
                    spriteFlavor = "chocolate";
                    break;
                default:
                    spriteFlavor = "vainillia"; // fallback
            }

            // Intentar usar sprite (StandDown por defecto)
            Image iceCreamSprite = ImageLoader.getIceCreamSprite(spriteFlavor, "stand", "down");
            
            if (iceCreamSprite != null) {
                // Dibujar sprite escalado al tamaño de la celda
                g.drawImage(iceCreamSprite, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                // Fallback: dibujar círculo de color como antes
                int centerX = x + CELL_SIZE / 2;
                int centerY = y + CELL_SIZE / 2;
                
                Color color;
                switch (flavor) {
                    case "vainilla":
                        color = COLOR_VANILLA;
                        break;
                    case "fresa":
                        color = COLOR_STRAWBERRY;
                        break;
                    case "chocolate":
                        color = COLOR_CHOCOLATE;
                        break;
                    default:
                        color = Color.WHITE;
                }

                g.setColor(color);
                g.fillOval(centerX - 15, centerY - 15, 30, 30);

                // Borde más grueso
                g.setColor(color.darker());
                g.setStroke(new BasicStroke(4));
                g.drawOval(centerX - 15, centerY - 15, 30, 30);
            }

            // Etiqueta para P2 (Cooperativo)
            Game game = controller.getGame();
            if (game != null && game.getSecondIceCreamFlavor() != null) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("P2", x + CELL_SIZE / 2 - 8, y + CELL_SIZE / 2 + 5);
            }
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

        // Nivel
        if (game.getCurrentLevel() != null) {
            g.drawString("NIVEL: " + game.getCurrentLevel().getLevelNumber(), 250, 65);
        }

        // Frutas restantes
        g.drawString("FRUTAS: " + game.getBoard().getRemainingFruits(), 450, 35);

        // Modo PVP: mostrar controles
        if (game.getGameMode() == GameMode.PVP) {
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(173, 216, 230)); // Helado (azul claro)
            g.drawString("P1 (Helado): W/A/S/D", 650, 35);

            g.setColor(new Color(255, 140, 0)); // Monstruo (naranja)
            g.drawString("P2 (Monstruo): ↑←↓→", 650, 65);
        }

        // Dibujar botón "Volver al Menú" en la esquina superior derecha
        drawReturnToMenuButton(g);
    }

    /**
     * Dibuja el botón de volver al menú
     */
    private void drawReturnToMenuButton(Graphics2D g) {
        int buttonX = getWidth() - BUTTON_WIDTH - BUTTON_MARGIN;
        int buttonY = BUTTON_MARGIN;

        // Fondo del botón
        g.setColor(new Color(200, 0, 0)); // Rojo
        g.fillRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Borde del botón
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Texto del botón
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String buttonText = "Menú";
        FontMetrics fm = g.getFontMetrics();
        int textX = buttonX + (BUTTON_WIDTH - fm.stringWidth(buttonText)) / 2;
        int textY = buttonY + ((BUTTON_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(buttonText, textX, textY);
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
        int y = getHeight() / 2 - 100;

        g.drawString(msg, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        msg = "Presiona ESPACIO para continuar";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, y + 50);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        msg = "Presiona M para volver al menú";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        g.drawString(msg, x, y + 85);

        // Dibujar botones de pausa
        drawPauseButtons(g);
    }

    /**
     * Dibuja los botones del overlay de pausa
     */
    private void drawPauseButtons(Graphics2D g) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Posiciones de los botones
        int button1X = centerX - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        int button1Y = centerY + 50;

        int button2X = centerX;
        int button2Y = centerY + 50;

        int button3X = centerX + PAUSE_BUTTON_WIDTH + PAUSE_BUTTON_MARGIN;
        int button3Y = centerY + 50;

        // Dibujar botón "Guardar Partida"
        drawButton(g, button1X, button1Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Guardar Partida",
                new Color(100, 100, 255));

        // Dibujar botón "Volver al Menú"
        drawButton(g, button2X, button2Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Volver al Menú",
                new Color(200, 0, 0));

        // Dibujar botón "Continuar"
        drawButton(g, button3X, button3Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Continuar", new Color(0, 150, 0));
    }

    /**
     * Dibuja un botón genérico
     */
    private void drawButton(Graphics2D g, int x, int y, int width, int height, String text, Color color) {
        // Fondo del botón
        g.setColor(color);
        g.fillRect(x, y, width, height);

        // Borde del botón
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, width, height);

        // Texto del botón
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, textX, textY);
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

    /**
     * Establece el callback para volver al menú
     */
    public void setOnReturnToMenuClick(Runnable callback) {
        this.onReturnToMenuClick = callback;
    }

    /**
     * Establece el callback para guardar la partida
     */
    public void setOnSaveGameClick(Runnable callback) {
        this.onSaveGameClick = callback;
    }

    /**
     * Establece el callback para continuar la partida
     */
    public void setOnContinueGameClick(Runnable callback) {
        this.onContinueGameClick = callback;
    }
}
