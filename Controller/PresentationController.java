package Controller;

import Domain.GameMode;
import Domain.PVPMode;
import Presentation.Intro;
import Presentation.StartMenu;
import Presentation.Modes;
import Presentation.SelectIceCream;
import Presentation.SelectMonster;
import Presentation.SelectPVPMode;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Controlador de Presentación - Punto de entrada principal de la aplicación
 * Gestiona la ejecución del programa y coordina las vistas
 */
public class PresentationController {

    private Intro intro;
    private StartMenu menuInicio;
    private Modes modos;
    private SelectPVPMode selectPVPMode;
    private SelectIceCream pvp;
    private SelectMonster selectMonster;
    private GameMode selectedGameMode; // Almacena el modo de juego seleccionado
    private PVPMode selectedPVPMode; // Almacena el tipo de PVP seleccionado
    private String selectedIceCream; // Almacena el helado seleccionado
    private String selectedSecondIceCream; // Almacena el segundo helado seleccionado (para PVP cooperativo)
    private GameController gameController; // Controlador del juego
    private JFrame gameFrame; // Ventana para mostrar el GamePanel

    /**
     * Constructor del controlador
     */
    public PresentationController() {
        this.intro = new Intro();
        this.menuInicio = new StartMenu();
        this.modos = new Modes();
        this.selectPVPMode = new SelectPVPMode();
        this.pvp = new SelectIceCream();
        this.selectMonster = new SelectMonster();

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
        this.selectPVPMode.setVisible(false);
        this.pvp.setVisible(false);
        this.selectMonster.setVisible(false);
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
            selectedGameMode = GameMode.PVP; // Guardar el modo seleccionado
            modos.setVisible(false);
            selectPVPMode.setVisible(true); // Mostrar selector de tipo de PVP
        });

        // Listener para el botón Back en SelectPVPMode
        selectPVPMode.setOnBackClick(() -> {
            selectPVPMode.setVisible(false);
            modos.setVisible(true);
            selectedGameMode = null;
            selectedPVPMode = null;
        });

        // Listener para Helado vs Monstruo
        selectPVPMode.setOnIceCreamVsMonsterClick(() -> {
            selectedPVPMode = PVPMode.ICE_CREAM_VS_MONSTER;
            selectPVPMode.setVisible(false);
            pvp.setVisible(true); // Mostrar selección de helado
        });

        // Listener para Helado Cooperativo
        selectPVPMode.setOnIceCreamCooperativeClick(() -> {
            selectedPVPMode = PVPMode.ICE_CREAM_COOPERATIVE;
            selectPVPMode.setVisible(false);
            pvp.setVisible(true); // Mostrar selección del primer helado
        });

        // Listener para el botón Back en PVP
        pvp.setOnBackClick(() -> {
            pvp.setVisible(false);
            if (selectedGameMode == GameMode.PVM) {
                // PVM: Volver a Modos
                modos.setVisible(true);
                selectedIceCream = null;
            } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE && selectedIceCream != null
                    && selectedSecondIceCream == null) {
                // Si es cooperativo y ya seleccionamos el primer helado, mostrar de nuevo para
                // el segundo
                selectedIceCream = null;
                pvp.setVisible(true);
            } else {
                selectPVPMode.setVisible(true);
                selectedIceCream = null;
                selectedSecondIceCream = null;
            }
        });

        // Listener para Chocolate en PVP
        pvp.setOnChocolateClick(() -> {
            if (selectedIceCream == null) {
                selectedIceCream = "Chocolate";
                if (selectedGameMode == GameMode.PVM) {
                    // PVM: Iniciar directamente sin seleccionar monstruo (se asigna según nivel)
                    pvp.setVisible(false);
                    iniciarJuegoPVM(selectedIceCream);
                } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                    // Mostrar pantalla para seleccionar segundo helado
                    pvp.setVisible(false);
                    pvp.setVisible(true); // Mostrar nuevamente para segundo helado
                } else {
                    // Helado vs Monstruo
                    pvp.setVisible(false);
                    registrarCallbacksMonstruos();
                    selectMonster.setVisible(true);
                }
            } else {
                // Segundo helado en modo cooperativo
                selectedSecondIceCream = "Chocolate";
                pvp.setVisible(false);
                iniciarJuegoCooperativo(selectedIceCream, selectedSecondIceCream);
            }
        });

        // Listener para Vainilla en PVP
        pvp.setOnVainillaClick(() -> {
            if (selectedIceCream == null) {
                selectedIceCream = "Vainilla";
                if (selectedGameMode == GameMode.PVM) {
                    // PVM: Iniciar directamente sin seleccionar monstruo
                    pvp.setVisible(false);
                    iniciarJuegoPVM(selectedIceCream);
                } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                    pvp.setVisible(false);
                    pvp.setVisible(true);
                } else {
                    pvp.setVisible(false);
                    registrarCallbacksMonstruos();
                    selectMonster.setVisible(true);
                }
            } else {
                selectedSecondIceCream = "Vainilla";
                pvp.setVisible(false);
                iniciarJuegoCooperativo(selectedIceCream, selectedSecondIceCream);
            }
        });

        // Listener para Fresa en PVP
        pvp.setOnFresaClick(() -> {
            if (selectedIceCream == null) {
                selectedIceCream = "Fresa";
                if (selectedGameMode == GameMode.PVM) {
                    // PVM: Iniciar directamente sin seleccionar monstruo
                    pvp.setVisible(false);
                    iniciarJuegoPVM(selectedIceCream);
                } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                    pvp.setVisible(false);
                    pvp.setVisible(true);
                } else {
                    pvp.setVisible(false);
                    registrarCallbacksMonstruos();
                    selectMonster.setVisible(true);
                }
            } else {
                selectedSecondIceCream = "Fresa";
                pvp.setVisible(false);
                iniciarJuegoCooperativo(selectedIceCream, selectedSecondIceCream);
            }
        });

        // Listener para el botón Back en SelectMonster
        selectMonster.setOnBackClick(() -> {
            selectMonster.setVisible(false);
            if (selectedGameMode == GameMode.PVM) {
                // PVM: Volver a selección de helado
                pvp.setVisible(true);
                selectedIceCream = null;
            } else {
                pvp.setVisible(true);
                selectedIceCream = null;
            }
        });

        // Listener para el botón PVM en Modos
        modos.setOnPVMClick(() -> {
            selectedGameMode = GameMode.PVM; // Guardar el modo seleccionado
            modos.setVisible(false);
            pvp.setVisible(true); // Mostrar selector de helado
        });

        // Listener para el botón MVM en Modos
        modos.setOnMVMClick(() -> {
            // Aquí irá la lógica para MVM cuando esté lista
            System.out.println("MVM seleccionado");
        });
    }

    /**
     * Inicia el juego con el modo, helado y monstruo seleccionados
     */
    private void iniciarJuego(GameMode gameMode, String iceCreamFlavor, String monsterType) {
        if (gameMode == null) {
            System.err.println("Error: Modo de juego no seleccionado");
            return;
        }

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego
        gameController = new GameController(gameMode, iceCreamFlavor, monsterType);

        // Crear una ventana para el juego si no existe
        if (gameFrame == null) {
            gameFrame = new JFrame("Bad Ice Cream - Juego");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(true);
        }

        // Agregar el GamePanel a la ventana
        gameFrame.getContentPane().removeAll(); // Limpiar contenido anterior
        JPanel gamePanel = gameController.getGamePanel();
        gameFrame.getContentPane().add(gamePanel);

        // Configurar y mostrar la ventana con tamaño apropiado
        gameFrame.pack();
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar ventana
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        // Iniciar el primer nivel
        gameController.startLevel(1);

        System.out.println("Juego iniciado - Modo: " + gameMode + ", Helado: " + iceCreamFlavor);
    }

    /**
     * Registra callbacks dinámicamente para todos los monstruos disponibles
     */
    private void registrarCallbacksMonstruos() {
        // Buscar todos los monstruos disponibles
        File carpetaBotones = new File("Resources/Botones/Monstruos/");
        if (!carpetaBotones.exists()) {
            System.err.println("❌ Carpeta de monstruos no encontrada: " + carpetaBotones.getAbsolutePath());
            return;
        }

        // Buscar archivos Selected.gif para identificar monstruos disponibles
        File[] archivos = carpetaBotones.listFiles((d, n) -> n.endsWith("Selected.gif"));
        if (archivos == null || archivos.length == 0) {
            System.err.println("❌ No se encontraron monstruos (Selected.gif)");
            return;
        }

        System.out.println("🔄 Registrando callbacks para " + archivos.length + " monstruos...");

        for (File archivo : archivos) {
            String nombreMonstruo = archivo.getName()
                    .replace("Selected.gif", "")
                    .trim();

            // Verificar que exista Normal.png
            String rutaNormal = carpetaBotones.getAbsolutePath() + File.separator + nombreMonstruo + "Normal.png";
            if (!new File(rutaNormal).exists()) {
                System.out.println("⚠️ No se encontró Normal.png para: " + nombreMonstruo);
                continue;
            }

            // Registrar callback para este monstruo
            selectMonster.setOnMonstruoClick(nombreMonstruo, () -> {
                System.out.println("✅ Configuración seleccionada:");
                System.out.println("  Modo: " + selectedGameMode);
                System.out.println("  Helado (J1): " + selectedIceCream);
                System.out.println("  Monstruo (J2): " + nombreMonstruo);
                iniciarJuego(selectedGameMode, selectedIceCream, nombreMonstruo);
            });
        }
    }

    /**
     * Inicia el juego en modo cooperativo (dos helados)
     */
    private void iniciarJuegoCooperativo(String helado1, String helado2) {
        System.out.println("✅ Configuración Cooperativa seleccionada:");
        System.out.println("  Modo: " + selectedGameMode);
        System.out.println("  Helado (J1): " + helado1);
        System.out.println("  Helado (J2): " + helado2);

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego con ambos helados
        gameController = new GameController(selectedGameMode, helado1, helado2, null);

        // Crear una ventana para el juego si no existe
        if (gameFrame == null) {
            gameFrame = new JFrame("Bad Ice Cream - Juego Cooperativo");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(true);
        }

        // Agregar el GamePanel a la ventana
        gameFrame.getContentPane().removeAll();
        JPanel gamePanel = gameController.getGamePanel();
        gameFrame.getContentPane().add(gamePanel);

        // Configurar y mostrar la ventana con tamaño apropiado
        gameFrame.pack();
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        // Iniciar el primer nivel
        gameController.startLevel(1);
    }

    /**
     * Inicia el juego en modo PVM (el monstruo se asigna automáticamente según el
     * nivel)
     */
    private void iniciarJuegoPVM(String helado) {
        System.out.println("✅ Modo PVM seleccionado:");
        System.out.println("  Modo: " + selectedGameMode);
        System.out.println("  Helado: " + helado);
        System.out.println("  Monstruo: Se asignará automáticamente según el nivel");

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego con PVM (sin monstruo específico, se asigna
        // por nivel)
        gameController = new GameController(selectedGameMode, helado, null);

        // Crear una ventana para el juego si no existe
        if (gameFrame == null) {
            gameFrame = new JFrame("Bad Ice Cream - Juego PVM");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(true);
        }

        // Agregar el GamePanel a la ventana
        gameFrame.getContentPane().removeAll();
        JPanel gamePanel = gameController.getGamePanel();
        gameFrame.getContentPane().add(gamePanel);

        // Configurar y mostrar la ventana con tamaño apropiado
        gameFrame.pack();
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        // Iniciar el primer nivel
        gameController.startLevel(1);
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
