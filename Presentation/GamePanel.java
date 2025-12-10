package Presentation;

import Controller.GameController;
import Domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * GamePanel - Panel que renderiza el juego
 * 
 * RESPONSABILIDADES:
 * - Dibujar el tablero
 * - Dibujar entidades (helado, enemigos, frutas, bloques)
 * - Mostrar UI (puntos, tiempo, vidas)
 * - Mostrar mensajes (pausa, victoria, derrota)
 * 
 * RESTRICCIONES MVC:
 * - ✅ Puede LEER de Domain (Game, Board, etc.) para renderizar
 * - ❌ NUNCA puede MODIFICAR Domain
 * - ❌ NO tiene lógica de juego (eso está en GameController)
 * - ❌ NO captura eventos de teclado (eso está en GameController)
 * 
 * PATRÓN: View Read-Only
 * GamePanel importa Domain solo para lectura de datos necesarios
 * para el renderizado. Toda modificación de estado pasa por Controller.
 */
public class GamePanel extends JPanel {

    private GameController controller;
    private Runnable onReturnToMenuClick;
    private Runnable onSaveGameClick;
    private Runnable onContinueGameClick;

    // Variables para tracking del mouse
    private Point mousePosition = new Point(0, 0);
    private boolean mousePressed = false;

    // ✅ Hitboxes para botones de pausa (zonas de interacción)
    private Rectangle pauseContinueBtnHitbox;
    private Rectangle pauseSaveBtnHitbox;
    private Rectangle pauseMenuBtnHitbox;

    // Configuración visual
    private static final int CELL_SIZE = 40;
    private static final int UI_HEIGHT = 100;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_MARGIN = 10;
    private static final int PAUSE_BUTTON_WIDTH = 150;
    private static final int PAUSE_BUTTON_HEIGHT = 50;
    private static final int PAUSE_BUTTON_MARGIN = 20;

    // Panel de frutas
    private static final int FRUIT_PANEL_HEIGHT = 80;
    private static final int FRUIT_ICON_SIZE = 28;
    private static final int FRUIT_ICON_SPACING = 6;
    private static final Color FRUIT_PANEL_BG = new Color(255, 248, 220);
    private static final Color FRUIT_PANEL_BORDER = new Color(139, 69, 19);

    // Colores
    private static final Color COLOR_BACKGROUND = new Color(230, 230, 250);
    private static final Color COLOR_WALL = new Color(128, 128, 128); // Gris para muros
    private static final Color COLOR_ICE_BLOCK = new Color(173, 216, 230); // Azul claro para hielo
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

        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(COLOR_BACKGROUND);

        ImageLoader.loadAllImages();
        updatePanelSize();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleMouseClick(e);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                mousePressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                mousePressed = false;
                repaint();
            }
        });

        addMouseMotionListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mousePosition = e.getPoint();
                repaint();
            }
        });
    }

    /**
     * Maneja los clics del mouse para detectar botones
     */
    private void handleMouseClick(java.awt.event.MouseEvent e) {
        Game game = controller.getGame();

        if (game != null && (game.getGameState() == GameState.PLAYING)) {
            int buttonX = getWidth() - BUTTON_WIDTH - BUTTON_MARGIN;
            int buttonY = BUTTON_MARGIN;

            if (e.getX() >= buttonX && e.getX() <= buttonX + BUTTON_WIDTH &&
                    e.getY() >= buttonY && e.getY() <= buttonY + BUTTON_HEIGHT) {
                if (onReturnToMenuClick != null) {
                    onReturnToMenuClick.run();
                }
            }
        }

        if (game != null && game.getGameState() == GameState.PAUSED) {
            detectPauseButtonClick(e);
        }
    }

    /**
     * Detecta clics en los botones de pausa
     */
    private void detectPauseButtonClick(java.awt.event.MouseEvent e) {
        Point p = e.getPoint();

        // ✅ Usar las hitboxes guardadas en el último dibujo
        if (pauseContinueBtnHitbox != null && pauseContinueBtnHitbox.contains(p) && onContinueGameClick != null) {
            onContinueGameClick.run();
        } else if (pauseSaveBtnHitbox != null && pauseSaveBtnHitbox.contains(p) && onSaveGameClick != null) {
            onSaveGameClick.run();
        } else if (pauseMenuBtnHitbox != null && pauseMenuBtnHitbox.contains(p) && onReturnToMenuClick != null) {
            onReturnToMenuClick.run();
        }
    }

    /**
     * Método anterior de detección de clics (fallback)
     */
    private void detectPauseButtonClickOld(java.awt.event.MouseEvent e) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int button1X = centerX - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        int button1Y = centerY + 50;

        int button2X = centerX;
        int button2Y = centerY + 50;

        int button3X = centerX + PAUSE_BUTTON_WIDTH + PAUSE_BUTTON_MARGIN;
        int button3Y = centerY + 50;

        if (e.getX() >= button1X && e.getX() <= button1X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button1Y && e.getY() <= button1Y + PAUSE_BUTTON_HEIGHT) {
            if (onSaveGameClick != null) {
                onSaveGameClick.run();
            }
        } else if (e.getX() >= button2X && e.getX() <= button2X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button2Y && e.getY() <= button2Y + PAUSE_BUTTON_HEIGHT) {
            if (onReturnToMenuClick != null) {
                onReturnToMenuClick.run();
            }
        } else if (e.getX() >= button3X && e.getX() <= button3X + PAUSE_BUTTON_WIDTH &&
                e.getY() >= button3Y && e.getY() <= button3Y + PAUSE_BUTTON_HEIGHT) {
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
            int height = board.getHeight() * CELL_SIZE + UI_HEIGHT + FRUIT_PANEL_HEIGHT;
            setPreferredSize(new Dimension(width, height));
        } else {
            setPreferredSize(new Dimension(600, 620));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Game game = controller.getGame();
        if (game == null) {
            drawMessage(g, "Cargando.. .");
            return;
        }

        updateAllVisualPositions(game);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GameState state = game.getGameState();

        if (state == GameState.PLAYING || state == GameState.PAUSED) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            drawFruitPanel(g2d, game);

            if (state == GameState.PAUSED) {
                drawPauseOverlay(g2d);
            }
        } else if (state == GameState.WON) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            drawFruitPanel(g2d, game);
            drawVictoryOverlay(g2d, game);
        } else if (state == GameState.LOST) {
            drawGame(g2d, game);
            drawUI(g2d, game);
            drawFruitPanel(g2d, game);
            drawGameOverOverlay(g2d, game);
        }
    }

    /**
     * Actualiza las posiciones visuales de todos los objetos
     */
    private void updateAllVisualPositions(Game game) {
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
     * Dibuja el juego completo (tablero y entidades)
     */
    private void drawGame(Graphics2D g, Game game) {
        Board board = game.getBoard();

        drawGrid(g, board);
        drawFruits(g, board);
        drawWalls(g, board);
        drawIceBlocks(g, board);
        drawEnemies(g, board);
        drawIceCream(g, board);
        drawSecondIceCream(g, board);
    }

    /**
     * Dibuja la cuadrícula del tablero
     */
    private void drawGrid(Graphics2D g, Board board) {
        Image mapBackground = ImageLoader.getMapBackground();

        if (mapBackground != null) {
            int width = board.getWidth() * CELL_SIZE;
            int height = board.getHeight() * CELL_SIZE;
            g.drawImage(mapBackground, 0, UI_HEIGHT, width, height, null);
        }
    }

    /**
     * Dibuja los bloques de hielo
     */
    private void drawIceBlocks(Graphics2D g, Board board) {
        List<IceBlock> iceBlocks = board.getIceBlocks();

        for (IceBlock block : iceBlocks) {
            Position pos = block.getPosition();
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

            Image iceSprite = ImageLoader.getIceBlockSprite("static");

            if (iceSprite != null) {
                g.drawImage(iceSprite, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                g.setColor(COLOR_ICE_BLOCK);
                g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                g.setColor(COLOR_ICE_BLOCK.darker());
                g.setStroke(new BasicStroke(2));
                g.drawRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                g.setColor(COLOR_ICE_BLOCK);
            }
        }
    }

    /**
     * Dibuja los muros indestructibles (bordes del nivel)
     */
    private void drawWalls(Graphics2D g, Board board) {
        List<Position> walls = board.getWalls();

        for (Position pos : walls) {
            int x = pos.getX() * CELL_SIZE;
            int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

            // Dibujar muro gris
            g.setColor(COLOR_WALL);
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            g.setColor(COLOR_WALL.darker());
            g.setStroke(new BasicStroke(2));
            g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
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
                int x = pos.getX() * CELL_SIZE;
                int y = pos.getY() * CELL_SIZE + UI_HEIGHT;

                String fruitType = fruit.getFruitType().toLowerCase();
                String spriteType;
                switch (fruitType) {
                    case "uvas":
                    case "grape":
                        spriteType = "grapes";
                        break;
                    case "plátano":
                    case "platano":
                    case "banana":
                        spriteType = "banana";
                        break;
                    case "piña":
                    case "pina":
                    case "pineapple":
                        spriteType = "pineapple";
                        break;
                    case "cereza":
                    case "cherry":
                        spriteType = "cherry";
                        break;
                    default:
                        spriteType = fruitType;
                }

                String visualState = fruit.getVisualState();
                Image fruitSprite = ImageLoader.getFruitSprite(spriteType, visualState);

                if (fruitSprite != null) {
                    g.drawImage(fruitSprite, x, y, CELL_SIZE, CELL_SIZE, null);
                } else {
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
                    g.setColor(color.darker());
                    g.drawOval(centerX - 10, centerY - 10, 20, 20);
                }
            }
        }
    }

    /**
     * Dibuja los enemigos
     */
    private void drawEnemies(Graphics2D g, Board board) {
        List<Enemy> enemies = board.getEnemies();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isAlive()) {
                float visualX = enemy.getVisualX();
                float visualY = enemy.getVisualY();

                int x = (int) (visualX * CELL_SIZE);
                int y = (int) (visualY * CELL_SIZE) + UI_HEIGHT;

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
                        spriteType = "troll";
                }

                String action = enemy.getCurrentAction();
                Direction direction = enemy.getCurrentDirection();
                String dirStr = direction.toString().toLowerCase();

                Image enemySprite = ImageLoader.getMonsterSprite(spriteType, action, dirStr);

                if (enemySprite != null) {
                    g.drawImage(enemySprite, x, y, CELL_SIZE, CELL_SIZE, null);
                } else {
                    int rectX = x + 5;
                    int rectY = y + 5;
                    Color color = enemy.getColor() != null ? enemy.getColor() : Color.RED;
                    g.setColor(color);
                    g.fillRect(rectX, rectY, CELL_SIZE - 10, CELL_SIZE - 10);
                    g.setColor(color.darker());
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(rectX, rectY, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }
    }

    /**
     * Dibuja el helado
     */
    private void drawIceCream(Graphics2D g, Board board) {
        IceCream iceCream = board.getIceCream();

        if (iceCream != null && iceCream.isAlive()) {
            float visualX = iceCream.getVisualX();
            float visualY = iceCream.getVisualY();

            int x = (int) (visualX * CELL_SIZE);
            int y = (int) (visualY * CELL_SIZE) + UI_HEIGHT;

            String flavor = iceCream.getFlavor().toLowerCase();
            String spriteFlavor;
            switch (flavor) {
                case "vainilla":
                    spriteFlavor = "vainillia";
                    break;
                case "fresa":
                    spriteFlavor = "strawberry";
                    break;
                case "chocolate":
                    spriteFlavor = "chocolate";
                    break;
                default:
                    spriteFlavor = "vainillia";
            }

            String action = iceCream.getCurrentAction();
            Direction direction = iceCream.getCurrentDirection();
            String dirStr = direction.toString().toLowerCase();

            Image iceCreamSprite = ImageLoader.getIceCreamSprite(spriteFlavor, action, dirStr);

            if (iceCreamSprite != null) {
                int iceCreamSize = (int) (CELL_SIZE * 3.0);
                int offset = (CELL_SIZE - iceCreamSize) / 2;
                g.drawImage(iceCreamSprite, x + offset, y + offset, iceCreamSize, iceCreamSize, null);
            } else {
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
                g.fillOval(centerX - 22, centerY - 22, 44, 44);
                g.setColor(color.darker());
                g.setStroke(new BasicStroke(4));
                g.drawOval(centerX - 22, centerY - 22, 44, 44);
            }
        }
    }

    /**
     * Dibuja el segundo helado (modo cooperativo)
     */
    private void drawSecondIceCream(Graphics2D g, Board board) {
        IceCream secondIceCream = board.getSecondIceCream();

        if (secondIceCream != null && secondIceCream.isAlive()) {
            float visualX = secondIceCream.getVisualX();
            float visualY = secondIceCream.getVisualY();

            int x = (int) (visualX * CELL_SIZE);
            int y = (int) (visualY * CELL_SIZE) + UI_HEIGHT;

            String flavor = secondIceCream.getFlavor().toLowerCase();
            String spriteFlavor;
            switch (flavor) {
                case "vainilla":
                    spriteFlavor = "vainillia";
                    break;
                case "fresa":
                    spriteFlavor = "strawberry";
                    break;
                case "chocolate":
                    spriteFlavor = "chocolate";
                    break;
                default:
                    spriteFlavor = "vainillia";
            }

            String action = secondIceCream.getCurrentAction();
            Direction direction = secondIceCream.getCurrentDirection();
            String dirStr = direction.toString().toLowerCase();

            Image iceCreamSprite = ImageLoader.getIceCreamSprite(spriteFlavor, action, dirStr);

            if (iceCreamSprite != null) {
                int iceCreamSize = (int) (CELL_SIZE * 3.0);
                int offset = (CELL_SIZE - iceCreamSize) / 2;
                g.drawImage(iceCreamSprite, x + offset, y + offset, iceCreamSize, iceCreamSize, null);
            } else {
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
                g.fillOval(centerX - 22, centerY - 22, 44, 44);
                g.setColor(color.darker());
                g.setStroke(new BasicStroke(4));
                g.drawOval(centerX - 22, centerY - 22, 44, 44);
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

        g.drawString("PUNTOS: " + game.getScore(), 20, 35);

        int minutes = game.getRemainingTime() / 60;
        int seconds = game.getRemainingTime() % 60;
        String timeStr = String.format("TIEMPO: %02d:%02d", minutes, seconds);
        g.drawString(timeStr, 20, 65);

        if (game.getCurrentLevel() != null) {
            g.drawString("NIVEL: " + game.getCurrentLevel().getLevelNumber(), 250, 65);
        }

        g.drawString("FRUTAS: " + game.getBoard().getRemainingFruits(), 450, 35);

        if (game.getGameMode() == GameMode.PVP) {
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(173, 216, 230));
            g.drawString("P1 (Helado): W/A/S/D", 650, 35);

            g.setColor(new Color(255, 140, 0));
            g.drawString("P2 (Monstruo): ↑←↓→", 650, 65);
        }

        drawReturnToMenuButton(g);
    }

    /**
     * Dibuja el panel inferior de frutas
     */
    private void drawFruitPanel(Graphics2D g, Game game) {
        Board board = game.getBoard();
        List<Fruit> fruits = board.getFruits();

        int panelY = board.getHeight() * CELL_SIZE + UI_HEIGHT;
        int panelWidth = board.getWidth() * CELL_SIZE;

        // Fondo
        g.setColor(FRUIT_PANEL_BG);
        g.fillRect(0, panelY, panelWidth, FRUIT_PANEL_HEIGHT);

        // Bordes
        g.setColor(FRUIT_PANEL_BORDER);
        g.setStroke(new BasicStroke(4));
        g.drawLine(0, panelY, panelWidth, panelY);
        g.drawLine(0, panelY + FRUIT_PANEL_HEIGHT - 1, panelWidth, panelY + FRUIT_PANEL_HEIGHT - 1);

        // Título
        g.setColor(new Color(139, 69, 19));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("FRUTAS DEL NIVEL:", 10, panelY + 22);

        // Agrupar frutas por tipo
        java.util.Map<String, java.util.List<Fruit>> fruitsByType = new java.util.LinkedHashMap<>();
        for (Fruit fruit : fruits) {
            String type = fruit.getFruitType();
            fruitsByType.putIfAbsent(type, new java.util.ArrayList<>());
            fruitsByType.get(type).add(fruit);
        }

        // Dibujar iconos
        int startX = 10;
        int currentX = startX;
        int iconY = panelY + 35;

        for (java.util.Map.Entry<String, java.util.List<Fruit>> entry : fruitsByType.entrySet()) {
            String fruitType = entry.getKey();
            java.util.List<Fruit> fruitsOfType = entry.getValue();

            String spriteType;
            switch (fruitType.toLowerCase()) {
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
                    spriteType = fruitType;
            }

            for (Fruit fruit : fruitsOfType) {
                if (currentX + FRUIT_ICON_SIZE > panelWidth - 200) {
                    break;
                }

                Image fruitSprite = ImageLoader.getFruitSprite(spriteType, "normal");

                if (fruitSprite != null) {
                    if (fruit.isCollected()) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3F));
                        g.drawImage(fruitSprite, currentX, iconY, FRUIT_ICON_SIZE, FRUIT_ICON_SIZE, null);
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));

                        g.setColor(new Color(0, 200, 0));
                        g.setFont(new Font("Arial", Font.BOLD, 20));
                        g.drawString("✓", currentX + 6, iconY + 20);
                    } else {
                        g.drawImage(fruitSprite, currentX, iconY, FRUIT_ICON_SIZE, FRUIT_ICON_SIZE, null);
                    }
                } else {
                    Color color;
                    switch (fruitType.toLowerCase()) {
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

                    if (fruit.isCollected()) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3F));
                    }

                    g.setColor(color);
                    g.fillOval(currentX + 6, iconY + 6, 16, 16);

                    if (fruit.isCollected()) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
                    }
                }

                currentX += FRUIT_ICON_SIZE + FRUIT_ICON_SPACING;
            }

            currentX += FRUIT_ICON_SPACING * 2;
        }

        // Contador de progreso
        int totalFruits = fruits.size();
        int collectedFruits = 0;
        for (Fruit fruit : fruits) {
            if (fruit.isCollected()) {
                collectedFruits++;
            }
        }

        int progressBarX = panelWidth - 190;
        int progressBarY = panelY + 30;
        int progressBarWidth = 180;
        int progressBarHeight = 25;

        g.setColor(new Color(200, 200, 200));
        g.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        float progress = totalFruits > 0 ? (float) collectedFruits / totalFruits : 0;
        int filledWidth = (int) (progressBarWidth * progress);

        Color progressColor;
        if (progress < 0.33F) {
            progressColor = new Color(255, 100, 100);
        } else if (progress < 0.66F) {
            progressColor = new Color(255, 200, 100);
        } else {
            progressColor = new Color(100, 255, 100);
        }

        g.setColor(progressColor);
        g.fillRect(progressBarX, progressBarY, filledWidth, progressBarHeight);

        g.setColor(FRUIT_PANEL_BORDER);
        g.setStroke(new BasicStroke(2));
        g.drawRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String progressText = collectedFruits + " / " + totalFruits;
        FontMetrics fm = g.getFontMetrics();
        int textX = progressBarX + (progressBarWidth - fm.stringWidth(progressText)) / 2;
        int textY = progressBarY + ((progressBarHeight - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(progressText, textX, textY);
    }

    /**
     * Dibuja el botón de volver al menú
     */
    private void drawReturnToMenuButton(Graphics2D g) {
        int buttonX = getWidth() - BUTTON_WIDTH - BUTTON_MARGIN;
        int buttonY = BUTTON_MARGIN;

        g.setColor(new Color(200, 0, 0));
        g.fillRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String buttonText = "Menú";
        FontMetrics fm = g.getFontMetrics();
        int textX = buttonX + (BUTTON_WIDTH - fm.stringWidth(buttonText)) / 2;
        int textY = buttonY + ((BUTTON_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(buttonText, textX, textY);
    }

    /**
     * Dibuja overlay de pausa con imágenes
     */
    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        Image panelImage = ImageLoader.getPauseMenuImage("panel", "normal");
        if (panelImage == null) {
            drawPauseOverlayFallback(g);
            return;
        }

        int panelSize = 650;
        int panelX = (getWidth() - panelSize) / 2;
        int panelY = (getHeight() - panelSize) / 2;
        g.drawImage(panelImage, panelX, panelY, panelSize, panelSize, null);

        // ✅ CADA BOTÓN CON SU PROPIO TAMAÑO
        int spacing = 18;
        int startY = panelY + 245;

        // CONTINUAR (más grande - 80% del ancho, más alto, bajado y a la derecha)
        int continueW = 430;
        int continueH = 95;
        int continueOffset = 60; // Desplazamiento a la derecha
        int continueOffsetY = 30; // Desplazamiento hacia abajo
        Rectangle continueBtn = new Rectangle(
                panelX + (panelSize - continueW) / 2 + continueOffset,
                startY + continueOffsetY,
                continueW,
                continueH);

        // ✅ Zona de interacción reducida para el botón continuar
        Rectangle continueBtnHitbox = new Rectangle(
                panelX + (panelSize - continueW) / 2 + continueOffset,
                startY + continueOffsetY,
                continueW - 60, // Reducir ancho
                continueH - 20); // Reducir alto

        // GUARDAR JUEGO (ancho completo - 100%)
        int saveW = 520;
        int saveH = 85;
        Rectangle saveBtn = new Rectangle(
                panelX + (panelSize - saveW) / 2,
                startY + continueH + spacing - 20,
                saveW,
                saveH);

        // MENÚ PRINCIPAL (mediano - 85%)
        int menuW = 450;
        int menuH = 80;
        Rectangle menuBtn = new Rectangle(
                panelX + (panelSize - menuW) / 2,
                startY + continueH + saveH + spacing * 2 - 20,
                menuW,
                menuH);

        drawPauseButton(g, continueBtn, "continue");
        drawPauseButton(g, saveBtn, "save");
        drawPauseButton(g, menuBtn, "menu");

        // ✅ Guardar hitboxes para detección de clics
        this.pauseContinueBtnHitbox = continueBtnHitbox;
        this.pauseSaveBtnHitbox = saveBtn;
        this.pauseMenuBtnHitbox = menuBtn;
    }

    /**
     * Dibuja un botón del menú de pausa con estados hover/pressed
     */
    /**
     * Dibuja un botón del menú de pausa con estados hover/pressed
     */
    private void drawPauseButton(Graphics2D g, Rectangle bounds, String buttonType) {
        boolean isHover = bounds.contains(mousePosition);
        boolean isPressed = isHover && mousePressed;

        String state;
        if (isPressed) {
            state = "pressed";
        } else if (isHover) {
            state = "hover";
        } else {
            state = "normal";
        }

        Image buttonImage = ImageLoader.getPauseMenuImage(buttonType, state);

        if (buttonImage != null) {
            // ✅ Configurar renderizado de alta calidad con transparencia
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // ✅ Dibujar con transparencia preservada
            g.drawImage(buttonImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
        } else {
            System.err.println("⚠️ No se pudo cargar: pause_" + buttonType + "_" + state);
            // Fallback: dibujar botón simple
            Color color = buttonType.equals("continue") ? new Color(0, 150, 0)
                    : buttonType.equals("save") ? new Color(200, 200, 0) : new Color(200, 0, 0);
            g.setColor(color);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setColor(Color.WHITE);
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    /**
     * Fallback al menú de pausa original (sin imágenes)
     */
    private void drawPauseOverlayFallback(Graphics2D g) {
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

        drawPauseButtons(g);
    }

    /**
     * Dibuja los botones del overlay de pausa (método anterior)
     */
    private void drawPauseButtons(Graphics2D g) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int button1X = centerX - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        int button1Y = centerY + 50;

        int button2X = centerX;
        int button2Y = centerY + 50;

        int button3X = centerX + PAUSE_BUTTON_WIDTH + PAUSE_BUTTON_MARGIN;
        int button3Y = centerY + 50;

        drawButton(g, button1X, button1Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Guardar Partida",
                new Color(100, 100, 255));

        drawButton(g, button2X, button2Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Volver al Menú",
                new Color(200, 0, 0));

        drawButton(g, button3X, button3Y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, "Continuar", new Color(0, 150, 0));
    }

    /**
     * Dibuja un botón genérico
     */
    private void drawButton(Graphics2D g, int x, int y, int width, int height, String text, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(x, y, width, height);

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
     * Convierte una imagen a formato compatible con transparencia
     */
    private Image makeTransparent(Image img) {
        if (img == null)
            return null;

        // Crear BufferedImage con soporte de transparencia
        java.awt.image.BufferedImage buffered = new java.awt.image.BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                java.awt.image.BufferedImage.TYPE_INT_ARGB // ← ARGB = Alpha + RGB
        );

        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return buffered;
    }

    // ========================================
    // CALLBACKS
    // ========================================

    public void setOnReturnToMenuClick(Runnable callback) {
        this.onReturnToMenuClick = callback;
    }

    public void setOnSaveGameClick(Runnable callback) {
        this.onSaveGameClick = callback;
    }

    public void setOnContinueGameClick(Runnable callback) {
        this.onContinueGameClick = callback;
    }
}