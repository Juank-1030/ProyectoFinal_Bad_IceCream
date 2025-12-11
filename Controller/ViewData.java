package Controller;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ViewData - Datos para la Vista (MVC Pattern)
 * 
 * RESPONSABILIDAD:
 * - Proporcionar datos que la View necesita para renderizar
 * - NO expone tipos de Domain
 * - Solo accesibles desde Controller
 * 
 * PATRÓN: Data Transfer Object (DTO)
 * Los datos aquí son copias/referencias que la View usa SOLO para lectura
 */
public class ViewData {

    // ========== ESTADO DEL JUEGO ==========
    public String gameState; // "PLAYING", "PAUSED", "WON", "LOST"
    public int score;
    public int remainingTime;
    public int currentLevel;
    public int remainingFruits;
    public String gameMode; // "PVM", "PVP", "MVM"

    // ========== DIMENSIONES DEL TABLERO ==========
    public int boardWidth;
    public int boardHeight;

    // ========== HELADO PRIMARIO ==========
    public boolean iceCreamAlive;
    public float iceCreamX;
    public float iceCreamY;
    public String iceCreamFlavor;
    public String iceCreamAction;
    public String iceCreamDirection;

    // ========== HELADO SECUNDARIO (Cooperativo) ==========
    public boolean secondIceCreamAlive;
    public float secondIceCreamX;
    public float secondIceCreamY;
    public String secondIceCreamFlavor;
    public String secondIceCreamAction;
    public String secondIceCreamDirection;

    // ========== ENEMIGOS ==========
    public List<EnemyView> enemies = new ArrayList<>();

    // ========== FRUTAS ==========
    public List<FruitView> fruits = new ArrayList<>();

    // ========== BLOQUES DE HIELO ==========
    public List<PositionView> iceBlocks = new ArrayList<>();

    // ========== MUROS ==========
    public List<PositionView> walls = new ArrayList<>();

    // ========== CLASES INTERNAS PARA DATOS ==========

    public static class EnemyView {
        public float x;
        public float y;
        public String type;
        public String action;
        public String direction;
        public String color; // Como String para evitar dependencia en Color
        public boolean alive;

        public EnemyView() {
            // Constructor sin parámetros para inicialización flexible
        }

        public EnemyView(float x, float y, String type, String action, String direction, String color, boolean alive) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.action = action;
            this.direction = direction;
            this.color = color;
            this.alive = alive;
        }
    }

    public static class FruitView {
        public int x;
        public int y;
        public String type;
        public String visualState;
        public boolean collected;

        public FruitView() {
            // Constructor sin parámetros para inicialización flexible
        }

        public FruitView(int x, int y, String type, String visualState, boolean collected) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.visualState = visualState;
            this.collected = collected;
        }
    }

    public static class PositionView {
        public int x;
        public int y;

        public PositionView() {
            // Constructor sin parámetros para inicialización flexible
        }

        public PositionView(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
