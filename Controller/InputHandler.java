package Controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;
import java.util.HashSet;

/**
 * InputHandler - Detector de entrada del teclado
 * 
 * PROPÓSITO:
 * Capturar y mantener el estado actual de las teclas presionadas
 * 
 * FUNCIÓN:
 * - Implementa KeyListener para escuchar eventos del teclado
 * - Almacena qué teclas están activas en cada momento
 * - Proporciona métodos para consultar si una acción está siendo realizada
 * 
 * PATRÓN: Input Buffering
 * Permite detectar inputs sin perderlos entre frames del juego
 */
public class InputHandler implements KeyListener {

    private Set<Integer> keysPressed = new HashSet<>();
    private static final int KEY_UP = KeyEvent.VK_UP;
    private static final int KEY_DOWN = KeyEvent.VK_DOWN;
    private static final int KEY_LEFT = KeyEvent.VK_LEFT;
    private static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    private static final int KEY_SPACE = KeyEvent.VK_SPACE;
    private static final int KEY_P = KeyEvent.VK_P;
    private static final int KEY_ESC = KeyEvent.VK_ESCAPE;

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No necesitamos implementar
    }

    /**
     * Verifica si una tecla está presionada
     */
    public boolean isKeyPressed(int keyCode) {
        return keysPressed.contains(keyCode);
    }

    /**
     * Verifica si el jugador intenta moverse arriba
     */
    public boolean isMoveUp() {
        return isKeyPressed(KEY_UP) || isKeyPressed(KeyEvent.VK_W);
    }

    /**
     * Verifica si el jugador intenta moverse abajo
     */
    public boolean isMoveDown() {
        return isKeyPressed(KEY_DOWN) || isKeyPressed(KeyEvent.VK_S);
    }

    /**
     * Verifica si el jugador intenta moverse izquierda
     */
    public boolean isMoveLeft() {
        return isKeyPressed(KEY_LEFT) || isKeyPressed(KeyEvent.VK_A);
    }

    /**
     * Verifica si el jugador intenta moverse derecha
     */
    public boolean isMoveRight() {
        return isKeyPressed(KEY_RIGHT) || isKeyPressed(KeyEvent.VK_D);
    }

    /**
     * Verifica si el jugador intenta crear/romper hielo
     */
    public boolean isToggleIce() {
        return isKeyPressed(KEY_SPACE);
    }

    /**
     * Verifica si el jugador intenta pausar
     */
    public boolean isPauseRequested() {
        return isKeyPressed(KEY_P);
    }

    /**
     * Verifica si el jugador intenta salir
     */
    public boolean isExitRequested() {
        return isKeyPressed(KEY_ESC);
    }

    /**
     * Limpia el estado de todas las teclas
     * Útil cuando pierdes foco de ventana
     */
    public void clearAllKeys() {
        keysPressed.clear();
    }

    /**
     * Obtiene el conjunto de teclas presionadas (solo lectura)
     */
    public Set<Integer> getPressedKeys() {
        return new HashSet<>(keysPressed);
    }
}
