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

        // Panel personalizado que dibuja la imagen de fondo sin estirar
        JPanel panelPrincipal = new JPanel() {
            private Image imagenFondo;
            private int imgWidth;
            private int imgHeight;

            {
                File archivoFondo = new File(rutaFondo);
                if (archivoFondo.exists()) {
                    ImageIcon icon = new ImageIcon(archivoFondo.getAbsolutePath());
                    imgWidth = icon.getIconWidth();
                    imgHeight = icon.getIconHeight();
                    imagenFondo = icon.getImage();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    // Dibujar imagen con aspecto original sin estirar
                    g.drawImage(imagenFondo, 0, 0, imgWidth, imgHeight, this);

                    // Llenar el resto del fondo con color si es necesario
                    if (imgWidth < this.getWidth()) {
                        g.setColor(Color.BLACK);
                        g.fillRect(imgWidth, 0, this.getWidth() - imgWidth, this.getHeight());
                    }
                    if (imgHeight < this.getHeight()) {
                        g.setColor(Color.BLACK);
                        g.fillRect(0, imgHeight, this.getWidth(), this.getHeight() - imgHeight);
                    }
                }
            }
        };

        panelPrincipal.setLayout(new BorderLayout());
        add(panelPrincipal);

        setVisible(true);
    }
}
