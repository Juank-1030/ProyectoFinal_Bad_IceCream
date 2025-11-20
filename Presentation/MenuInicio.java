package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MenuInicio extends JFrame {
    private String rutaPanel = "Resources/Opciones_Menu/Panel_menu.png";
    private String rutaBotones = "Resources/Botones/Inicio/";

    private JButton btnNewGame;
    private JButton btnContinueGame;
    private JButton btnExit;

    // Listeners para acciones de botones
    private Runnable onNewGameClick;
    private Runnable onExitClick;

    public MenuInicio() {
        setTitle("Menú de Inicio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        inicializarVentana();
    }

    /**
     * Establece el listener para el clic en New_Game
     */
    public void setOnNewGameClick(Runnable listener) {
        this.onNewGameClick = listener;
    }

    /**
     * Establece el listener para el clic en Exit
     */
    public void setOnExitClick(Runnable listener) {
        this.onExitClick = listener;
    }

    private void inicializarVentana() {
        // Panel personalizado que dibuja la imagen de fondo
        JPanel panelPrincipal = new JPanel() {
            private Image imagenFondo;

            {
                File archivoPanelMenu = new File(rutaPanel);
                if (archivoPanelMenu.exists()) {
                    ImageIcon icon = new ImageIcon(archivoPanelMenu.getAbsolutePath());
                    // Escalar imagen al 40%
                    int anchoNuevo = (int) (icon.getIconWidth() * 0.4);
                    int altoNuevo = (int) (icon.getIconHeight() * 0.4);
                    imagenFondo = icon.getImage().getScaledInstance(anchoNuevo, altoNuevo, Image.SCALE_SMOOTH);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };

        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setFocusable(true);
        panelPrincipal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cerrarMenu();
                }
            }
        });

        // Panel para los botones - GridLayout vertical sin espacios
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 0, 0));
        panelBotones.setOpaque(false);

        // Crear botones
        btnNewGame = crearBoton("New_Game");
        btnContinueGame = crearBoton("Continue_Game");
        btnExit = crearBoton("Exit");

        // Agregar botones sin espacios
        panelBotones.add(btnNewGame);
        panelBotones.add(btnContinueGame);
        panelBotones.add(btnExit);

        // Panel centrador
        JPanel panelCentrado = new JPanel();
        panelCentrado.setLayout(new BorderLayout());
        panelCentrado.setOpaque(false);
        panelCentrado.add(panelBotones, BorderLayout.CENTER);

        panelPrincipal.add(panelCentrado, BorderLayout.CENTER);

        setContentPane(panelPrincipal);

        // Configurar tamaño de la ventana
        File archivoPanelMenu = new File(rutaPanel);
        if (archivoPanelMenu.exists()) {
            ImageIcon icon = new ImageIcon(archivoPanelMenu.getAbsolutePath());
            int anchoNuevo = (int) (icon.getIconWidth() * 0.4);
            int altoNuevo = (int) (icon.getIconHeight() * 0.4);
            setSize(anchoNuevo, altoNuevo);
        } else {
            setSize(240, 180);
        }

        setLocationRelativeTo(null);
    }

    private JButton crearBoton(String nombreBoton) {
        String rutaNormal = rutaBotones + nombreBoton + ".png";
        String rutaPresionado = rutaBotones + nombreBoton + "_Pushed.png";

        File archivoNormal = new File(rutaNormal);
        File archivoPresionado = new File(rutaPresionado);

        if (!archivoNormal.exists() || !archivoPresionado.exists()) {
            System.err.println("Error: No se encontró " + (archivoNormal.exists() ? rutaPresionado : rutaNormal));
            return new JButton("Error");
        }

        ImageIcon iconoNormal = new ImageIcon(rutaNormal);
        ImageIcon iconoPresionado = new ImageIcon(rutaPresionado);

        // Escalar botones al 20% de su tamaño original para mantener proporción
        int nuevoAncho = Math.max(1, (int) (iconoNormal.getIconWidth() * 0.2));
        int nuevoAlto = Math.max(1, (int) (iconoNormal.getIconHeight() * 0.2));

        Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        Image imagenPresionada = iconoPresionado.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                Image.SCALE_SMOOTH);

        ImageIcon iconoNormalEscalado = new ImageIcon(imagenNormal);
        ImageIcon iconoPresionadoEscalado = new ImageIcon(imagenPresionada);

        JButton boton = new JButton(iconoNormalEscalado);
        boton.setPressedIcon(iconoPresionadoEscalado);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.addActionListener(e -> accionBoton(nombreBoton));

        return boton;
    }

    private void accionBoton(String nombreBoton) {
        switch (nombreBoton) {
            case "New_Game":
                System.out.println("Nuevo Juego");
                if (onNewGameClick != null) {
                    onNewGameClick.run();
                }
                break;
            case "Continue_Game":
                System.out.println("Continuar Juego");
                break;
            case "Exit":
                if (onExitClick != null) {
                    onExitClick.run();
                }
                break;
        }
    }

    public void mostrarMenu() {
        setVisible(true);
    }

    public void cerrarMenu() {
        setVisible(false);
        dispose();
    }
}
