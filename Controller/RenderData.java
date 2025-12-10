package Controller;

import java.util.List;

/**
 * RenderData - Objeto de transferencia de datos (DTO) para renderizado
 * Contiene SOLO datos necesarios para dibujar, sin lógica
 * Permite que Presentation NO importe de Domain
 */
public class RenderData {

    // Estado del juego
    public String gameState; // "PLAYING", "PAUSED", "WON", "LOST", "LOADING"

    // Dimensiones
    public int boardWidth;
    public int boardHeight;

    // UI
    public int score;
    public int remainingTime;
    public int currentLevelNumber;
    public int remainingFruits;
    public String gameMode; // "PVM", "PVP", "MVM"

    // Datos del helado principal
    public float iceCreamX;
    public float iceCreamY;
    public String iceCreamFlavor; // "vainilla", "fresa", "chocolate"
    public String iceCreamDirection; // "up", "down", "left", "right"
    public String iceCreamAction; // "moving", "creating_ice", "idle"
    public boolean iceCreamAlive;

    // Datos del segundo helado (si existe)
    public float secondIceCreamX;
    public float secondIceCreamY;
    public String secondIceCreamFlavor;
    public String secondIceCreamDirection;
    public String secondIceCreamAction;
    public boolean secondIceCreamAlive;

    // Enemigos
    public List<EnemyData> enemies;

    // Frutas
    public List<FruitData> fruits;

    // Bloques de hielo
    public List<IceBlockData> iceBlocks;

    // Muros
    public List<PositionData> walls;

    /**
     * DTO para datos de enemigo
     */
    public static class EnemyData {
        public float x;
        public float y;
        public String type; // "troll", "pot", "yellowsquid", "narval"
        public String direction; // "up", "down", "left", "right"
        public String action; // "moving", "idle"
        public String color; // hexadecimal o nombre
        public boolean alive;
    }

    /**
     * DTO para datos de fruta
     */
    public static class FruitData {
        public int x;
        public int y;
        public String type; // "grape", "banana", "pineapple", "cherry"
        public boolean collected;
        public String visualState; // "normal", "appearing", "disappearing"
    }

    /**
     * DTO para datos de bloque de hielo
     */
    public static class IceBlockData {
        public int x;
        public int y;
    }

    /**
     * DTO para coordenadas
     */
    public static class PositionData {
        public int x;
        public int y;

        public PositionData(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
