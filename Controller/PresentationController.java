package Controller;

import Presentation.Intro;
import Presentation.MenuInicio;
import Presentation.Modos;
import Presentation.PVP;
import java.awt.event.ActionListener;

/**
 * Controlador de Presentación - Punto de entrada principal de la aplicación
 * Gestiona la ejecución del programa y coordina las vistas
 */
public class PresentationController {

    private Intro intro;
    private MenuInicio menuInicio;
    private Modos modos;
    private PVP pvp;

    /**
     * Constructor del controlador
     */
    public PresentationController() {
        this.intro = new Intro();
        this.menuInicio = new MenuInicio();
        this.modos = new Modos();
        this.pvp = new PVP();

        prepareElements();
        prepareActions();
    }

    /**
     * Prepara los elementos visuales de las vistas
     */
    private void prepareElements() {
        // Configurar visibilidad inicial: solo Intro visible
        this.intro.setVisible(true); // MOSTRAR Intro primero
        this.menuInicio.setVisible(false);
        this.modos.setVisible(false);
        this.pvp.setVisible(false);
    }

    /**
     * Prepara las acciones e interacciones de los botones
     */
    private void prepareActions() {
        // Listener para mostrar el menú desde Intro
        intro.setOnMenuOpen(() -> {
            menuInicio.setVisible(true);
        });

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

        // Listener para el botón Back en Modos
        modos.setOnBackClick(() -> {
            modos.setVisible(false);
            intro.setVisible(true);
            intro.mostrarSegundoGif();
        });

        // Listener para el botón PVP en Modos
        modos.setOnPVPClick(() -> {
            modos.setVisible(false);
            pvp.setVisible(true);
        });

        // Listener para el botón Back en PVP
        pvp.setOnBackClick(() -> {
            pvp.setVisible(false);
            modos.setVisible(true);
        });

        // Listener para el botón PVM en Modos
        modos.setOnPVMClick(() -> {
            // Aquí irá la lógica para PVM cuando esté lista
            System.out.println("PVM seleccionado");
        });

        // Listener para el botón MVM en Modos
        modos.setOnMVMClick(() -> {
            // Aquí irá la lógica para MVM cuando esté lista
            System.out.println("MVM seleccionado");
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
