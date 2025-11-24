package Controller;

import Presentation.Intro;
import Presentation.MenuInicio;
import Presentation.Modos;
import java.awt.event.ActionListener;

/**
 * Controlador de Presentación - Punto de entrada principal de la aplicación
 * Gestiona la ejecución del programa y coordina las vistas
 */
public class PresentationController {

    private Intro intro;
    private MenuInicio menuInicio;
    private Modos modos;

    /**
     * Constructor del controlador
     */
    public PresentationController() {
        this.intro = new Intro();
        this.menuInicio = new MenuInicio();
        this.modos = new Modos();

        prepareElements();
        prepareActions();
    }

    /**
     * Prepara los elementos visuales de las vistas
     */
    private void prepareElements() {
        // Configurar visibilidad inicial
        this.modos.setVisible(false);
    }

    /**
     * Prepara las acciones e interacciones de los botones
     */
    private void prepareActions() {
        // Listener para el botón New_Game
        menuInicio.setOnNewGameClick(() -> {
            intro.setVisible(false);
            menuInicio.setVisible(false);
            modos.setVisible(true);
        });

        // Listener para el botón Exit
        menuInicio.setOnExitClick(() -> {
            System.exit(0);
        });

        // Listener para mostrar el menú desde Intro
        intro.setOnMenuOpen(() -> {
            menuInicio.setVisible(true);
        });
    }

    /**
     * Inicia la aplicación mostrando la secuencia de introducción
     */
    public void iniciarAplicacion() {
        intro.mostrarSecuenciaIntro();
    }

    /**
     * Punto de entrada principal de la aplicación
     */
    public static void main(String[] args) {
        PresentationController controlador = new PresentationController();
        controlador.iniciarAplicacion();
    }
}
