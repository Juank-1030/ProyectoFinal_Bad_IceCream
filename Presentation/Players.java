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
        setSize(800, 600);
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
                    // Escalar imagen para llenar la ventana manteniendo aspecto
                    double escalaAncho = (double) this.getWidth() / imgWidth;
                    double escalaAlto = (double) this.getHeight() / imgHeight;
                    double escala = Math.min(escalaAncho, escalaAlto);

                    int nuevoAncho = (int) (imgWidth * escala);
                    int nuevoAlto = (int) (imgHeight * escala);

                    // Centrar la imagen
                    int x = (this.getWidth() - nuevoAncho) / 2;
                    int y = (this.getHeight() - nuevoAlto) / 2;

                    // Dibujar fondo negro
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    // Dibujar imagen escalada
                    g.drawImage(imagenFondo, x, y, nuevoAncho, nuevoAlto, this);
                }
            }
        };

        panelPrincipal.setLayout(new BorderLayout());
        add(panelPrincipal);

        setVisible(true);
    }
}
