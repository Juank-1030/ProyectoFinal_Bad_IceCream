package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * ImageLoader - Utility class to load and cache game sprites
 * 
 * RESPONSABILIDADES:
 * - Cargar y cachear todas las imágenes del juego (sprites de helados,
 * monstruos, frutas, hielo, mapas)
 * - Proporcionar métodos estáticos para acceder a las imágenes
 * - Manejar errores de carga de archivos con fallback
 * - Soporte para GIFs animados
 */
public class ImageLoader {

    // Cache de imágenes
    private static Map<String, Image> imageCache = new HashMap<>();

    // Flag para saber si las imágenes ya fueron cargadas
    private static boolean imagesLoaded = false;

    /**
     * Carga todas las imágenes al iniciar
     */
    public static void loadAllImages() {
        if (imagesLoaded) {
            return; // Ya se cargaron
        }

        System.out.println("🎨 Cargando recursos gráficos...");

        // Cargar mapa de fondo
        loadMapBackground();

        // Cargar sprites de helados
        loadIceCreamSprites();

        // Cargar sprites de monstruos
        loadMonsterSprites();

        // Cargar sprites de frutas
        loadFruitSprites();

        // Cargar sprites de bloques de hielo
        loadIceBlockSprites();

        // Cargar imágenes del menú de pausa
        loadPauseMenuImages();

        // ✅ NUEVO: Cargar imágenes del menú PVP
        loadPVPMenuImages();

        imagesLoaded = true;
        System.out.println("✅ Recursos gráficos cargados: " + imageCache.size() + " imágenes");
    }

    /**
     * Carga el fondo del mapa
     */
    private static void loadMapBackground() {
        loadImage("map_background", "Resources/MapasNiveles/Mapa1.jpeg");
    }

    /**
     * Carga los sprites de helados
     */
    private static void loadIceCreamSprites() {
        String[] flavors = { "Chocolate", "Strawberry", "Vainillia" };
        String[] actions = { "StandDown", "StandUp", "StandLeft", "StandRight",
                "DownWalk", "UpWalk", "LeftWalk", "RightWalk",
                "DownBreak", "UpBreak", "LeftBreak", "RightBreak",
                "DownShoot", "UpShoot", "LeftShoot", "RightShoot",
                "Die", "Win" };

        for (String flavor : flavors) {
            for (String action : actions) {
                String key = "icecream_" + flavor.toLowerCase() + "_" + action.toLowerCase();
                String path = "Resources/Helados/" + flavor + "/" + action + ".gif";
                loadImage(key, path);
            }
        }
    }

    /**
     * Carga los sprites de monstruos
     */
    private static void loadMonsterSprites() {
        // Narval
        String[] narvalActions = { "DownWalk", "UpWalk", "LeftWalk", "RightWalk",
                "DownBreak", "UpBreak", "LeftBreak", "RightBreak" };
        for (String action : narvalActions) {
            String key = "monster_narval_" + action.toLowerCase();
            String path = "Resources/Monstruos/Narval/" + action + ".gif";
            loadImage(key, path);
        }

        // Troll
        String[] trollActions = { "DownWalk", "UpWalk", "LeftWalk", "RightWalk", "Stand" };
        for (String action : trollActions) {
            String key = "monster_troll_" + action.toLowerCase();
            String path = "Resources/Monstruos/Troll/" + action + ".gif";
            loadImage(key, path);
        }

        // YellowSquid
        String[] squidActions = { "DownWalk", "UpWalk", "LeftWalk", "RightWalk",
                "DownBreak", "UpBreak", "LeftBreak", "RightBreak" };
        for (String action : squidActions) {
            String key = "monster_yellowsquid_" + action.toLowerCase();
            String path = "Resources/Monstruos/YellowSquid/" + action + ".gif";
            loadImage(key, path);
        }

        // Pot - tiene muchas animaciones, cargamos las básicas
        String[] potActions = { "DownWalk1", "UpWalk1", "LeftWalk1", "RightWalk1" };
        for (String action : potActions) {
            String key = "monster_pot_" + action.toLowerCase();
            String path = "Resources/Monstruos/Pot/" + action + ".gif";
            loadImage(key, path);
        }
    }

    /**
     * Carga los sprites de frutas
     */
    private static void loadFruitSprites() {
        String[] fruits = { "Cherry", "Banana", "Grapes", "Cactus" };
        String[] states = { "Normal", "Appear", "Collected", "Shadow" };

        for (String fruit : fruits) {
            for (String state : states) {
                String key = "fruit_" + fruit.toLowerCase() + "_" + state.toLowerCase();
                String path = "Resources/Frutas/" + fruit + "/" + state + ".gif";
                loadImage(key, path);
            }
        }

        // Pineapple is special - it has Movement.gif instead of Normal.gif
        loadImage("fruit_pineapple_movement", "Resources/Frutas/Pineapple/Movement.gif");
        loadImage("fruit_pineapple_flying", "Resources/Frutas/Pineapple/Flying.gif");
        loadImage("fruit_pineapple_appear", "Resources/Frutas/Pineapple/Appear.gif");
        loadImage("fruit_pineapple_collected", "Resources/Frutas/Pineapple/Collected.gif");
        loadImage("fruit_pineapple_shadow", "Resources/Frutas/Pineapple/Shadow.gif");
        // Also create an alias for normal -> movement
        loadImage("fruit_pineapple_normal", "Resources/Frutas/Pineapple/Movement.gif");
    }

    /**
     * Carga los sprites de bloques de hielo
     */
    private static void loadIceBlockSprites() {
        String[] states = { "Static", "Appear", "BrokenByIC", "FuitInside", "Hiting", "Melting", "Shine" };

        for (String state : states) {
            String key = "ice_" + state.toLowerCase();
            String path = "Resources/Obstaculos/Hielo/" + state + ".gif";
            loadImage(key, path);
        }
    }

    /**
     * Carga las imágenes del menú de pausa con transparencia
     */
    private static void loadPauseMenuImages() {
        // Panel de fondo
        loadImagePNG("pause_panel", "Resources/Pausa/Panel de madera.png");

        // Botón Continuar
        loadImagePNG("pause_continue_normal", "Resources/Pausa/CONTINUAR.png");
        loadImagePNG("pause_continue_hover", "Resources/Pausa/continuar hover.png");
        loadImagePNG("pause_continue_pressed", "Resources/Pausa/continuar pressed.png");

        // Botón Guardar Juego
        loadImagePNG("pause_save_normal", "Resources/Pausa/GUARDAR JUEGO.png");
        loadImagePNG("pause_save_hover", "Resources/Pausa/Guardar juego hover.png");
        loadImagePNG("pause_save_pressed", "Resources/Pausa/Guardar juego pressed.png");

        // Botón Menú Principal
        loadImagePNG("pause_menu_normal", "Resources/Pausa/MENU PRINCIPAL.png");
        loadImagePNG("pause_menu_hover", "Resources/Pausa/menu principal hover.png");
        loadImagePNG("pause_menu_pressed", "Resources/Pausa/menu principal pressed.png");
    }

    /**
     * ✅ NUEVO: Carga las imágenes del menú PVP
     */
    private static void loadPVPMenuImages() {
        // Botón Helado vs Monstruo
        loadImagePNG("pvp_icecream_vs_monster", "Resources/HELADO VS MOUNSTRUO/Helado vs mousntruo.png");

        // Botón Helado vs Helado (Cooperativo)
        loadImagePNG("pvp_coop", "Resources/HELADOVSHELADO/Cooperativo.png");
    }

    /**
     * Carga una imagen desde un archivo y la cachea
     * 
     * @param key  Clave para identificar la imagen en el cache
     * @param path Ruta del archivo de imagen
     */
    private static void loadImage(String key, String path) {
        try {
            // Try to load from file system
            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                System.err.println("⚠️  No se pudo cargar: " + path);
                return;
            }

            // Usar ImageIcon para soportar GIFs animados
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());

            // En entorno headless, no podemos verificar con MediaTracker
            // Simplemente verificamos que el archivo existe y la imagen no es null
            Image image = icon.getImage();
            if (image != null) {
                imageCache.put(key, image);
            } else {
                System.err.println("⚠️  Error al cargar imagen: " + path);
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando " + path + ": " + e.getMessage());
        }
    }

    /**
     * Carga imágenes PNG con transparencia (solo para menú de pausa y PVP)
     * 
     * @param key  Clave para identificar la imagen en el cache
     * @param path Ruta del archivo PNG
     */
    private static void loadImagePNG(String key, String path) {
        try {
            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                System.err.println("⚠️  No se pudo cargar PNG: " + path);
                return;
            }

            // Usar ImageIO para preservar transparencia
            BufferedImage bufferedImage = javax.imageio.ImageIO.read(file);

            if (bufferedImage != null) {
                boolean hasAlpha = bufferedImage.getColorModel().hasAlpha();

                if (hasAlpha) {
                    // Crear imagen compatible con transparencia ARGB
                    BufferedImage compatibleImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2d = compatibleImage.createGraphics();
                    g2d.setComposite(AlphaComposite.Src);
                    g2d.drawImage(bufferedImage, 0, 0, null);
                    g2d.dispose();

                    imageCache.put(key, compatibleImage);
                } else {
                    imageCache.put(key, bufferedImage);
                }
            } else {
                System.err.println("⚠️  Error al cargar PNG: " + path);
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando PNG " + path + ": " + e.getMessage());
        }
    }

    /**
     * Obtiene el fondo del mapa
     * 
     * @return Imagen del fondo del mapa, o null si no se pudo cargar
     */
    public static Image getMapBackground() {
        return imageCache.get("map_background");
    }

    /**
     * Obtiene un sprite de helado
     * 
     * @param flavor    Sabor del helado: "chocolate", "strawberry", "vainillia"
     * @param action    Acción: "stand", "walk", "break", "shoot", "die", "win"
     * @param direction Dirección: "down", "up", "left", "right" (solo para stand,
     *                  walk, break, shoot)
     * @return Imagen del sprite, o null si no se pudo cargar
     */
    public static Image getIceCreamSprite(String flavor, String action, String direction) {
        // Normalizar nombres
        flavor = flavor.toLowerCase();
        action = action.toLowerCase();
        direction = direction.toLowerCase();

        // Construir el nombre del archivo según la acción
        // Formato real: StandDown, DownWalk, etc.
        String fileName;
        if (action.equals("die") || action.equals("win")) {
            // Die y Win no tienen dirección
            fileName = action.substring(0, 1).toUpperCase() + action.substring(1);
        } else {
            // Para stand: StandDown, StandUp, etc.
            // Para walk/break/shoot: DownWalk, UpBreak, etc.
            String directionCap = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            String actionCap = action.substring(0, 1).toUpperCase() + action.substring(1);

            if (action.equals("stand")) {
                fileName = actionCap + directionCap; // StandDown
            } else {
                fileName = directionCap + actionCap; // DownWalk
            }
        }

        String key = "icecream_" + flavor + "_" + fileName.toLowerCase();
        return imageCache.get(key);
    }

    /**
     * Obtiene un sprite de monstruo
     * 
     * @param type      Tipo de monstruo: "narval", "troll", "pot", "yellowsquid"
     * @param action    Acción: "walk", "break", "stand"
     * @param direction Dirección: "down", "up", "left", "right"
     * @return Imagen del sprite, o null si no se pudo cargar
     */
    public static Image getMonsterSprite(String type, String action, String direction) {
        // Normalizar nombres
        type = type.toLowerCase();
        action = action.toLowerCase();
        direction = direction.toLowerCase();

        // Construir el nombre del archivo
        String fileName;
        if (action.equals("stand") && type.equals("troll")) {
            fileName = "stand";
        } else if (type.equals("pot")) {
            // Pot tiene sufijos numéricos, usar Walk1 por defecto
            String directionCap = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            String actionCap = action.substring(0, 1).toUpperCase() + action.substring(1);
            fileName = directionCap + actionCap + "1";
        } else {
            String directionCap = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            String actionCap = action.substring(0, 1).toUpperCase() + action.substring(1);
            fileName = directionCap + actionCap;
        }

        String key = "monster_" + type + "_" + fileName.toLowerCase();
        return imageCache.get(key);
    }

    /**
     * Obtiene un sprite de fruta
     * 
     * @param type  Tipo de fruta: "cherry", "banana", "grapes", "pineapple",
     *              "cactus"
     * @param state Estado: "normal", "appear", "collected", "shadow"
     * @return Imagen del sprite, o null si no se pudo cargar
     */
    public static Image getFruitSprite(String type, String state) {
        // Normalizar nombres
        type = type.toLowerCase();
        state = state.toLowerCase();

        String key = "fruit_" + type + "_" + state;
        return imageCache.get(key);
    }

    /**
     * Obtiene un sprite de bloque de hielo
     * 
     * @param state Estado: "static", "appear", "brokenbyic", "fuitinside",
     *              "hiting", "melting", "shine"
     *              Note: "fuitinside" and "hiting" match actual filenames (contain
     *              typos)
     * @return Imagen del sprite, o null si no se pudo cargar
     */
    public static Image getIceBlockSprite(String state) {
        // Normalizar nombre
        state = state.toLowerCase();

        String key = "ice_" + state;
        return imageCache.get(key);
    }

    /**
     * Obtiene una imagen del menú de pausa
     * 
     * @param element Elemento: "panel", "continue", "save", "menu"
     * @param state   Estado: "normal", "hover", "pressed" (no aplica para panel)
     * @return Imagen del elemento, o null si no se pudo cargar
     */
    public static Image getPauseMenuImage(String element, String state) {
        element = element.toLowerCase();
        state = state.toLowerCase();

        if (element.equals("panel")) {
            return imageCache.get("pause_panel");
        }

        String key = "pause_" + element + "_" + state;
        return imageCache.get(key);
    }

    /**
     * ✅ NUEVO: Obtiene una imagen del menú PVP
     * 
     * @param buttonType Tipo de botón: "icecream_vs_monster", "vs_monster", "coop",
     *                   "cooperativo"
     * @return Imagen del botón, o null si no se pudo cargar
     */
    public static Image getPVPMenuImage(String buttonType) {
        buttonType = buttonType.toLowerCase();

        if (buttonType.equals("icecream_vs_monster") || buttonType.equals("vs_monster")) {
            return imageCache.get("pvp_icecream_vs_monster");
        } else if (buttonType.equals("coop") || buttonType.equals("cooperativo")) {
            return imageCache.get("pvp_coop");
        }

        return null;
    }
}