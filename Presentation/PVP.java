package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase PVP - Pantalla del modo Player vs Player
 */
public class PVP extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private Runnable onBackClick;

    public PVP() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - PVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel personalizado que dibuja la imagen de fondo
        JPanel panelFondo = new JPanel() {
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

        panelFondo.setLayout(new BorderLayout());

        // Agregar letrero "Helados" en la parte superior
        JLabel letrero = agregarLetrero();

        // Agregar botón Back en la parte inferior
        JButton botonBack = crearBotonBack();
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Agregar letrero en la parte superior
        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.NONE;
        gbcLetrero.weightx = 0.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(5, 0, 0, 0);
        panelCentral.add(letrero, gbcLetrero);

        // Agregar botón Back en la parte inferior
        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.gridx = 0;
        gbcBack.gridy = 1;
        gbcBack.anchor = GridBagConstraints.SOUTH;
        gbcBack.fill = GridBagConstraints.NONE;
        gbcBack.weightx = 0.0;
        gbcBack.weighty = 1.0;
        gbcBack.insets = new Insets(0, 0, 50, 0);
        panelCentral.add(botonBack, gbcBack);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);

        setVisible(false); // Oculta inicialmente, se mostrará desde el controlador
    }

    private JLabel agregarLetrero() {
        String rutaLetrero = "Resources/Letreros/Modo/helados.png";
        File archivoLetrero = new File(rutaLetrero);

        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 12% de su tamaño original
            double escalaLetrero = 0.12;
            int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
            int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));
            Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);
            JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
            return labelLetrero;
        }

        return new JLabel();
    }

    private JButton crearBotonBack() {
        String rutaBack = "Resources/Letreros/Modo/back.png";
        File archivoBack = new File(rutaBack);
        JButton boton = new JButton();
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);

        if (archivoBack.exists()) {
            ImageIcon iconoNormal = new ImageIcon(rutaBack);
            // Escalar botón back al 15%
            double escala = 0.15;
            int nuevoAncho = Math.max(1, (int) (iconoNormal.getIconWidth() * escala));
            int nuevoAlto = Math.max(1, (int) (iconoNormal.getIconHeight() * escala));

            // Zona de interacción: reducida en altura (60%), ancho completo
            int anchoInteraccion = nuevoAncho;
            int altoInteraccion = (int) (nuevoAlto * 0.60);
            boton.setPreferredSize(new Dimension(anchoInteraccion, altoInteraccion));

            Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenNormal);
            boton.setIcon(iconoEscalado);

            // Agregar efecto hover
            boton.addMouseListener(new MouseAdapter() {
                private ImageIcon iconoHover;

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (iconoHover == null) {
                        Image imagenHover = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                                Image.SCALE_SMOOTH);
                        iconoHover = new ImageIcon(imagenHover);
                    }
                    boton.setIcon(iconoHover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    boton.setIcon(iconoEscalado);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    accionBotonBack();
                }
            });
        }

        return boton;
    }

    private void accionBotonBack() {
        if (onBackClick != null) {
            onBackClick.run();
        }
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PVP());
    }
}
