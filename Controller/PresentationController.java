package Controller;

import Domain.GameMode;
import Domain.PVPMode;
import Domain.LevelManager;
import Domain.IceCreamAIStrategyManager;
import Presentation.Intro;
import Presentation.StartMenu;
import Presentation.Modes;
import Presentation.SelectIceCream;
import Presentation.SelectIceCreamAI;
import Presentation.SelectMonster;
import Presentation.SelectPVPMode;
import Presentation.SelectLevel;
import Presentation.LevelConfigurationMenu;
import Presentation.LevelCompletionMenu;
import Presentation.EnemyConfigurationMenu;
import Presentation.FruitConfigurationMenu;
import java.io.File;
import java.util.Map;
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
    private SelectIceCreamAI selectIceCreamAI;
    private SelectMonster selectMonster;
    private SelectLevel selectLevel;
    private LevelConfigurationMenu levelConfigMenu;
    private LevelCompletionMenu levelCompletionMenu;
    private EnemyConfigurationMenu enemyConfigMenu;
    private FruitConfigurationMenu fruitConfigMenu;
    private GameMode selectedGameMode; // Almacena el modo de juego seleccionado
    private PVPMode selectedPVPMode; // Almacena el tipo de PVP seleccionado
    private String selectedIceCream; // Almacena el helado seleccionado
    private String selectedIceCreamAIStrategy; // Almacena la estrategia de IA del helado
    private String selectedSecondIceCream; // Almacena el segundo helado seleccionado (para PVP cooperativo)
    private String selectedMonster; // Almacena el monstruo seleccionado (para PVP Vs Monstruo)
    private int selectedLevelNumber; // Almacena el número del nivel seleccionado (1, 2 o 3)
    private Map<String, Integer> selectedEnemyConfig; // Configuración de enemigos seleccionada
    private Map<String, Integer> selectedFruitConfig; // Configuración de frutas seleccionada
    private LevelManager levelManager; // Gestor de niveles
    private GameController gameController; // Controlador del juego
    private JFrame gameFrame; // Ventana para mostrar el GamePanel
    private int nextLevelAfterCompletion; // Siguiente nivel cuando se completa uno (para configuración)
    private boolean inAutoProgressionMode; // Indica si estamos en modo de progresión automática de niveles

    /**
     * Constructor del controlador
     */
    public PresentationController() {
        this.intro = new Intro();
        this.menuInicio = new StartMenu();
        this.modos = new Modes();
        this.selectPVPMode = new SelectPVPMode();
        this.pvp = new SelectIceCream();
        this.selectIceCreamAI = new SelectIceCreamAI();
        this.selectMonster = new SelectMonster();
        this.selectLevel = new SelectLevel();
        this.levelConfigMenu = new LevelConfigurationMenu();
        this.enemyConfigMenu = new EnemyConfigurationMenu();
        this.fruitConfigMenu = new FruitConfigurationMenu();
        this.levelManager = new LevelManager();
        this.selectedLevelNumber = 1; // Nivel por defecto

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
        this.selectLevel.setVisible(false);
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
            } else if (selectedGameMode == GameMode.MVM) {
                // MVM: Volver a Modos
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
                pvp.setVisible(false);

                if (selectedGameMode == GameMode.MVM) {
                    // MVM: mostrar selección de estrategia IA SOLO en MVM
                    mostrarSeleccionIAHelado();
                } else if (selectedGameMode == GameMode.PVP) {
                    // PVP: Vs Monstruo ir directo a selección de monstruo
                    if (selectedPVPMode == PVPMode.ICE_CREAM_VS_MONSTER) {
                        registrarCallbacksMonstruos();
                        selectMonster.setVisible(true);
                    } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                        // Si es Cooperativo, mostrar selección del segundo helado
                        pvp.setVisible(true);
                    }
                } else if (selectedGameMode == GameMode.PVM) {
                    // PVM: mostrar selección de nivel
                    mostrarSeleccionNivel();
                }
            } else {
                // Segundo helado en modo cooperativo
                selectedSecondIceCream = "Chocolate";
                pvp.setVisible(false);
                mostrarSeleccionNivel();
            }
        });

        // Listener para Vainilla en PVP
        pvp.setOnVainillaClick(() -> {
            if (selectedIceCream == null) {
                selectedIceCream = "Vainilla";
                pvp.setVisible(false);

                if (selectedGameMode == GameMode.MVM) {
                    // MVM: mostrar selección de estrategia IA SOLO en MVM
                    mostrarSeleccionIAHelado();
                } else if (selectedGameMode == GameMode.PVP) {
                    // PVP: Vs Monstruo ir directo a selección de monstruo
                    if (selectedPVPMode == PVPMode.ICE_CREAM_VS_MONSTER) {
                        registrarCallbacksMonstruos();
                        selectMonster.setVisible(true);
                    } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                        // Si es Cooperativo, mostrar selección del segundo helado
                        pvp.setVisible(true);
                    }
                } else if (selectedGameMode == GameMode.PVM) {
                    // PVM: mostrar selección de nivel
                    mostrarSeleccionNivel();
                }
            } else {
                // Segundo helado en modo cooperativo
                selectedSecondIceCream = "Vainilla";
                pvp.setVisible(false);
                mostrarSeleccionNivel();
            }
        });

        // Listener para Fresa en PVP
        pvp.setOnFresaClick(() -> {
            if (selectedIceCream == null) {
                selectedIceCream = "Fresa";
                pvp.setVisible(false);

                if (selectedGameMode == GameMode.MVM) {
                    // MVM: mostrar selección de estrategia IA SOLO en MVM
                    mostrarSeleccionIAHelado();
                } else if (selectedGameMode == GameMode.PVP) {
                    // PVP: Vs Monstruo ir directo a selección de monstruo
                    if (selectedPVPMode == PVPMode.ICE_CREAM_VS_MONSTER) {
                        registrarCallbacksMonstruos();
                        selectMonster.setVisible(true);
                    } else if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                        // Si es Cooperativo, mostrar selección del segundo helado
                        pvp.setVisible(true);
                    }
                } else if (selectedGameMode == GameMode.PVM) {
                    // PVM: mostrar selección de nivel
                    mostrarSeleccionNivel();
                }
            } else {
                // Segundo helado en modo cooperativo
                selectedSecondIceCream = "Fresa";
                pvp.setVisible(false);
                mostrarSeleccionNivel();
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
            selectedGameMode = GameMode.MVM;
            System.out.println("✅ Modo MVM seleccionado");
            System.out.println("   Selecciona el helado que será controlado por IA");

            modos.setVisible(false);
            pvp.setVisible(true); // Mostrar pantalla de selección de helado
        });
    }

    /**
     * Limpia recursos antes de iniciar un nuevo juego
     */
    private void cleanupBeforeNewGame() {
        System.out.println("🧹 Limpiando recursos antes de nuevo juego...");

        // Detener el juego anterior si existe
        if (gameController != null) {
            try {
                gameController.stopGame();
                gameController = null;
            } catch (Exception e) {
                System.err.println("Error deteniendo juego anterior: " + e.getMessage());
                gameController = null;
            }
        }

        // Cerrar la ventana del juego anterior si existe
        if (gameFrame != null) {
            try {
                gameFrame.setVisible(false);
                gameFrame.getContentPane().removeAll();
                gameFrame.dispose();
                gameFrame = null;
                System.out.println("✅ Recursos del juego anterior limpiados");
            } catch (Exception e) {
                System.err.println("Error limpiando ventana anterior: " + e.getMessage());
                gameFrame = null;
            }
        }
    }

    /**
     * Inicia el juego con el modo, helado y monstruo seleccionados
     */
    private void iniciarJuego(GameMode gameMode, String iceCreamFlavor, String monsterType) {
        if (gameMode == null) {
            System.err.println("Error: Modo de juego no seleccionado");
            return;
        }

        // Limpiar recursos del juego anterior
        cleanupBeforeNewGame();

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego
        gameController = new GameController(gameMode, iceCreamFlavor, monsterType);

        // Registrar callbacks
        gameController.setOnReturnToMenuClick(createReturnToMenuCallback());
        gameController.setOnSaveGameClick(createSaveGameCallback());
        gameController.setOnContinueGameClick(createContinueGameCallback());
        gameController.setOnLevelComplete(createLevelCompleteCallback());

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
        // Limpiar callbacks anteriores para evitar duplicados
        selectMonster.limpiarCallbacks();

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

            // Crear variable final para capturar el valor actual en el lambda
            final String monstruoActual = nombreMonstruo;

            // Registrar callback para este monstruo
            selectMonster.setOnMonstruoClick(monstruoActual, () -> {
                System.out.println("✅ Monstruo seleccionado: " + monstruoActual);
                selectMonster.setVisible(false);
                // Guardar el monstruo y luego mostrar selección de nivel
                mostrarSeleccionNivelConMonstruo(monstruoActual);
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
        if (inAutoProgressionMode) {
            System.out.println("  📊 Modo progresión automática: iniciando nivel " + selectedLevelNumber);
        }

        // Limpiar recursos del juego anterior
        cleanupBeforeNewGame();

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego con ambos helados
        gameController = new GameController(selectedGameMode, helado1, helado2, null, selectedEnemyConfig,
                selectedFruitConfig);

        // Registrar callbacks
        gameController.setOnReturnToMenuClick(createReturnToMenuCallback());
        gameController.setOnSaveGameClick(createSaveGameCallback());
        gameController.setOnContinueGameClick(createContinueGameCallback());
        gameController.setOnLevelComplete(createLevelCompleteCallback());
        gameController.setOnLevelFailed(createLevelFailedCallback());

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

        // Iniciar el nivel (si estamos en progresión automática, usar
        // selectedLevelNumber; si no, nivel 1)
        int levelToStart = inAutoProgressionMode ? selectedLevelNumber : 1;
        gameController.startLevel(levelToStart);
    }

    /**
     * Muestra la selección de nivel cuando ya se ha seleccionado un monstruo para
     * PVP Vs Monstruo
     */
    private void mostrarSeleccionNivelConMonstruo(String monsterType) {
        System.out.println("✅ Guardando monstruo seleccionado: " + monsterType);
        selectedMonster = monsterType;
        mostrarSeleccionNivel();
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
        if (inAutoProgressionMode) {
            System.out.println("  📊 Modo progresión automática: iniciando nivel " + selectedLevelNumber);
        }

        // Limpiar recursos del juego anterior
        cleanupBeforeNewGame();

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego con PVM (sin monstruo específico, se asigna
        // por nivel)
        gameController = new GameController(selectedGameMode, helado, null, null, selectedEnemyConfig,
                selectedFruitConfig);

        // Registrar callbacks
        gameController.setOnReturnToMenuClick(createReturnToMenuCallback());
        gameController.setOnSaveGameClick(createSaveGameCallback());
        gameController.setOnContinueGameClick(createContinueGameCallback());
        gameController.setOnLevelComplete(createLevelCompleteCallback());
        gameController.setOnLevelFailed(createLevelFailedCallback());

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

        // Iniciar el nivel (si estamos en progresión automática, usar
        // selectedLevelNumber; si no, nivel 1)
        int levelToStart = inAutoProgressionMode ? selectedLevelNumber : 1;
        gameController.startLevel(levelToStart);
    }

    /**
     * Inicia el juego en modo PVP Vs Monstruo (helado vs monstruo específico)
     */
    private void iniciarJuegoVSMonstruo(String helado, String monstruo, String aiStrategy) {
        System.out.println("✅ Modo PVP Vs Monstruo seleccionado:");
        System.out.println("  Modo: " + selectedGameMode);
        System.out.println("  Helado: " + helado);
        System.out.println("  Monstruo: " + monstruo);
        System.out.println("  IA Helado: " + aiStrategy);
        if (inAutoProgressionMode) {
            System.out.println("  📊 Modo progresión automática: iniciando nivel " + selectedLevelNumber);
        }

        // Limpiar recursos del juego anterior
        cleanupBeforeNewGame();

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectIceCreamAI.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);

        // Crear el controlador del juego con el helado y monstruo específico
        gameController = new GameController(selectedGameMode, helado, null, monstruo, selectedEnemyConfig,
                selectedFruitConfig);

        // Establecer la estrategia de IA del helado si es especificada
        if (aiStrategy != null && !aiStrategy.isEmpty()) {
            gameController.setIceCreamAIStrategy(aiStrategy);
        }

        // Registrar callbacks
        gameController.setOnReturnToMenuClick(createReturnToMenuCallback());
        gameController.setOnSaveGameClick(createSaveGameCallback());
        gameController.setOnContinueGameClick(createContinueGameCallback());
        gameController.setOnLevelComplete(createLevelCompleteCallback());
        gameController.setOnLevelFailed(createLevelFailedCallback());

        // Crear una ventana para el juego si no existe
        if (gameFrame == null) {
            gameFrame = new JFrame("Bad Ice Cream - PVP vs " + monstruo);
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

        // Iniciar el nivel (si estamos en progresión automática, usar
        // selectedLevelNumber; si no, nivel 1)
        int levelToStart = inAutoProgressionMode ? selectedLevelNumber : 1;
        gameController.startLevel(levelToStart);
    }

    /**
     * Inicia el juego en modo MVM (Machine vs Machine)
     * El helado es controlado por IA intentando recoger frutas y evitar monstruos
     */
    private void iniciarJuegoMVM() {
        System.out.println("✅ Modo MVM iniciando:");
        System.out.println("  Helado controlado por IA: " + selectedIceCream);
        System.out.println("  Nivel seleccionado: " + selectedLevelNumber);
        System.out.println("  Los monstruos se asignarán según el nivel");
        System.out.println("  🤖 La IA controla el helado automáticamente");
        if (inAutoProgressionMode) {
            System.out.println("  📊 Modo progresión automática: iniciando nivel " + selectedLevelNumber);
        }

        // Limpiar recursos del juego anterior
        cleanupBeforeNewGame();

        // Ocultar pantallas de selección
        pvp.setVisible(false);
        selectMonster.setVisible(false);
        selectPVPMode.setVisible(false);
        modos.setVisible(false);
        menuInicio.setVisible(false);
        intro.setVisible(false);
        selectLevel.setVisible(false);

        // Crear el controlador del juego con MVM (sin monstruo específico, se asigna
        // por nivel)
        gameController = new GameController(selectedGameMode, selectedIceCream, null, null, selectedEnemyConfig,
                selectedFruitConfig);

        // Registrar callbacks
        gameController.setOnReturnToMenuClick(createReturnToMenuCallback());
        gameController.setOnSaveGameClick(createSaveGameCallback());
        gameController.setOnContinueGameClick(createContinueGameCallback());
        gameController.setOnLevelComplete(createLevelCompleteCallback());
        gameController.setOnLevelFailed(createLevelFailedCallback());

        // Crear una ventana para el juego si no existe
        if (gameFrame == null) {
            gameFrame = new JFrame("Bad Ice Cream - Modo MVM (IA Activa)");
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

        // Iniciar el nivel (si estamos en progresión automática, usar
        // selectedLevelNumber; si no, usar el seleccionado)
        int levelToStart = inAutoProgressionMode ? selectedLevelNumber : selectedLevelNumber;
        gameController.startLevel(levelToStart);
    }

    /**
     * Reseta el estado del juego y vuelve al menú
     */
    private void resetGameState() {
        System.out.println("🔄 Reseteando estado del juego...");

        selectedGameMode = null;
        selectedPVPMode = null;
        selectedIceCream = null;
        selectedSecondIceCream = null;
        selectedMonster = null;
        inAutoProgressionMode = false; // Resetear modo de progresión automática

        // Detener el juego
        if (gameController != null) {
            try {
                gameController.stopGame();
            } catch (Exception e) {
                System.err.println("Error deteniendo juego: " + e.getMessage());
            }
            gameController = null;
        }

        // Cerrar la ventana del juego correctamente
        if (gameFrame != null) {
            try {
                gameFrame.setVisible(false);
                gameFrame.getContentPane().removeAll();
                gameFrame.dispose();
                gameFrame = null;
                System.out.println("✅ Ventana del juego cerrada");
            } catch (Exception e) {
                System.err.println("Error cerrando ventana: " + e.getMessage());
                gameFrame = null;
            }
        }

        System.out.println("✅ Estado reseteado exitosamente");
    }

    /**
     * Crea un callback para volver al menú desde la pantalla del juego
     */
    /**
     * Crea un callback para guardar la partida
     */
    private Runnable createSaveGameCallback() {
        return () -> {
            System.out.println("✅ Partida guardada");
            // TODO: Implementar lógica de guardado de partida
        };
    }

    /**
     * Crea un callback para continuar la partida desde pausa
     */
    private Runnable createContinueGameCallback() {
        return () -> {
            if (gameController != null) {
                System.out.println("✅ Reanudando juego desde botón...");
                gameController.resume(); // Reanuda el juego
                gameController.getGamePanel().requestFocusInWindow(); // Devuelve el foco
            }
        };
    }

    private java.util.function.Consumer<Integer> createLevelCompleteCallback() {
        return (nextLevel) -> {
            System.out.println("✅ Nivel completado, preparando siguiente nivel automáticamente: " + nextLevel);

            // Guardar el número del siguiente nivel
            nextLevelAfterCompletion = nextLevel;
            selectedLevelNumber = nextLevel;

            // Resetear configuraciones para permitir nueva selección
            selectedEnemyConfig = null;
            selectedFruitConfig = null;

            // Indicar que estamos en modo de progresión automática
            inAutoProgressionMode = true;

            // Ocultar todas las pantallas
            selectLevel.setVisible(false);
            selectMonster.setVisible(false);
            selectPVPMode.setVisible(false);
            pvp.setVisible(false);
            menuInicio.setVisible(false);
            intro.setVisible(false);

            // Mostrar directamente la configuración de enemigos para el siguiente nivel
            mostrarConfiguracionEnemigos();
        };
    }

    private java.util.function.Consumer<Integer> createLevelFailedCallback() {
        return (previousLevel) -> {
            System.out.println("❌ Nivel fallido, volviendo al inicio desde nivel 1...");

            // Volver al nivel 1
            selectedLevelNumber = 1;

            // Resetear configuraciones
            selectedEnemyConfig = null;
            selectedFruitConfig = null;

            // Indicar que estamos en modo de progresión automática
            inAutoProgressionMode = true;

            // Ocultar todas las pantallas
            selectLevel.setVisible(false);
            selectMonster.setVisible(false);
            selectPVPMode.setVisible(false);
            pvp.setVisible(false);
            menuInicio.setVisible(false);
            intro.setVisible(false);

            // Mostrar directamente la configuración de enemigos para el nivel 1
            mostrarConfiguracionEnemigos();
        };
    }

    private Runnable createReturnToMenuCallback() {
        return () -> {
            System.out.println("✅ Volviendo al menú desde juego...");

            try {
                // Ocultar y cerrar ventana del juego inmediatamente
                if (gameFrame != null) {
                    gameFrame.setVisible(false);
                    gameFrame.getContentPane().removeAll();
                    gameFrame.dispose();
                }

                // Detener el juego
                if (gameController != null) {
                    gameController.stopGame();
                }

                // Limpiar estado
                resetGameState();

                // Mostrar intro en primer plano y reiniciar su estado
                intro.setVisible(true);
                intro.toFront(); // Traer ventana al frente
                intro.requestFocus(); // Dar foco a la ventana
                intro.resetIntro(); // Reiniciar intro con Timer no-bloqueante

                System.out.println("✅ Vuelto al menú exitosamente");
            } catch (Exception e) {
                System.err.println("❌ Error al volver al menú: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Muestra la pantalla de selección de nivel
     * Se llama después de seleccionar el helado en modo PVM
     */
    /**
     * Muestra el menú para seleccionar la IA del helado en modo MVM
     */
    private void mostrarSeleccionIAHelado() {
        selectIceCreamAI.setVisible(true);

        // Registrar callbacks para cada estrategia
        String[] strategies = Domain.IceCreamAIStrategyManager.getAvailableStrategies();
        for (String strategy : strategies) {
            selectIceCreamAI.setOnStrategyClick(strategy, () -> {
                selectedIceCreamAIStrategy = strategy;
                selectIceCreamAI.setVisible(false);

                // En MVM: ir directamente a selección de nivel
                mostrarSeleccionNivel();
            });
        }

        // Callback para atrás
        selectIceCreamAI.setOnBackClick(() -> {
            selectIceCreamAI.setVisible(false);
            pvp.setVisible(true);
            selectedIceCream = null;
            selectedIceCreamAIStrategy = null;
        });
    }

    private void mostrarSeleccionNivel() {
        selectLevel.setVisible(true);

        // Callback para nivel 1
        selectLevel.setOnLevel1Click(() -> {
            selectedLevelNumber = 1;
            selectLevel.setVisible(false);
            mostrarConfiguracionEnemigos();
        });

        // Callback para nivel 2
        selectLevel.setOnLevel2Click(() -> {
            selectedLevelNumber = 2;
            selectLevel.setVisible(false);
            mostrarConfiguracionEnemigos();
        });

        // Callback para nivel 3
        selectLevel.setOnLevel3Click(() -> {
            selectedLevelNumber = 3;
            selectLevel.setVisible(false);
            mostrarConfiguracionEnemigos();
        });

        // Callback para atrás
        selectLevel.setOnBackClick(() -> {
            selectLevel.setVisible(false);
            enemyConfigMenu.setVisible(false);
            fruitConfigMenu.setVisible(false);
            selectPVPMode.setVisible(false);
            selectMonster.setVisible(false);
            pvp.setVisible(true);
            selectedIceCream = null;
        });
    }

    /**
     * Muestra el menú de configuración de enemigos
     */
    private void mostrarConfiguracionEnemigos() {
        // Si es PVP Vs Monstruo, recrear el menú excluyendo el monstruo principal
        if (selectedGameMode == GameMode.PVP && selectedPVPMode == PVPMode.ICE_CREAM_VS_MONSTER) {
            enemyConfigMenu = new EnemyConfigurationMenu(selectedMonster);
        } else {
            // Para otros modos, usar el menú sin exclusiones
            enemyConfigMenu = new EnemyConfigurationMenu();
        }

        enemyConfigMenu.setVisible(true);

        // Callback para confirmar configuración de enemigos
        enemyConfigMenu.setOnConfirmClick(() -> {
            selectedEnemyConfig = enemyConfigMenu.getEnemyConfiguration();
            enemyConfigMenu.setVisible(false);

            // Si no hay configuración personalizada, usar la del nivel predeterminado
            if (selectedEnemyConfig.isEmpty()) {
                selectedEnemyConfig = null; // null indica usar configuración predeterminada
            }

            // Mostrar menú de configuración de frutas
            mostrarConfiguracionFrutas();
        });

        // Callback para atrás (solo si no estamos en modo de progresión automática)
        enemyConfigMenu.setOnBackClick(() -> {
            if (!inAutoProgressionMode) {
                enemyConfigMenu.setVisible(false);
                selectLevel.setVisible(true);
            }
            // Si estamos en modo de progresión automática, no permitir ir atrás
        });
    }

    /**
     * Muestra el menú de configuración de frutas
     */
    private void mostrarConfiguracionFrutas() {
        fruitConfigMenu.setVisible(true);

        // Callback para confirmar configuración de frutas
        fruitConfigMenu.setOnConfirmClick(() -> {
            selectedFruitConfig = fruitConfigMenu.getFruitConfiguration();
            System.out.println("📋 Configuración de frutas recibida:");
            if (selectedFruitConfig != null) {
                for (String fruit : selectedFruitConfig.keySet()) {
                    System.out.println("  - " + fruit + ": " + selectedFruitConfig.get(fruit));
                }
            }
            fruitConfigMenu.setVisible(false);

            // Si no hay configuración personalizada, usar la del nivel predeterminado
            if (selectedFruitConfig.isEmpty()) {
                System.out.println("⚠️ Configuración de frutas vacía, usaremos frutas predeterminadas");
                selectedFruitConfig = null; // null indica usar configuración predeterminada
            }

            iniciarJuegoSegunModo(selectedIceCream);
        });

        // Callback para atrás
        fruitConfigMenu.setOnBackClick(() -> {
            fruitConfigMenu.setVisible(false);
            enemyConfigMenu.setVisible(true);
        });
    }

    /**
     * Inicia el juego según el modo seleccionado
     */
    private void iniciarJuegoSegunModo(String helado) {
        if (selectedGameMode == GameMode.PVM) {
            iniciarJuegoPVM(helado);
        } else if (selectedGameMode == GameMode.PVP) {
            if (selectedPVPMode == PVPMode.ICE_CREAM_COOPERATIVE) {
                iniciarJuegoCooperativo(helado, selectedSecondIceCream);
            } else if (selectedPVPMode == PVPMode.ICE_CREAM_VS_MONSTER) {
                // PVP Vs Monstruo: ya tenemos el monstruo seleccionado en selectedMonster
                if (selectedMonster != null) {
                    iniciarJuegoVSMonstruo(helado, selectedMonster, selectedIceCreamAIStrategy);
                } else {
                    System.err.println("❌ Error: No se ha seleccionado monstruo para modo PVP Vs Monstruo");
                }
            }
        } else if (selectedGameMode == GameMode.MVM) {
            iniciarJuegoMVM();
        }
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
