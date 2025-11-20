package Presentation;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Clase Players - Muestra la pantalla con el fondo de juego
 */
public class Players extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    public Players() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Players");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel personalizado que dibuja la imagen de fondo
        JPanel panelPrincipal = new JPanel() {
            private Image imagenFondo;

            {
                File archivoFondo = new File(rutaFondo);
                if (archivoFondo.exists()) {
                    ImageIcon icon = new ImageIcon(archivoFondo.getAbsolutePath());
                    // Escalar imagen al tamaño de la ventana (1024x768)
                    imagenFondo = icon.getImage().getScaledInstance(1024, 768, Image.SCALE_SMOOTH);
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
        add(panelPrincipal);

        setVisible(true);
    }
}
