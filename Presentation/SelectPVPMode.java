package Presentation;

import Domain.PVPMode;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase SelectPVPMode - Muestra la pantalla de selección de tipo de PVP
 * Opciones: Helado vs Monstruo o Helado Cooperativo
 */
public class SelectPVPMode extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private Runnable onBackClick;
    private Runnable onIceCreamVsMonsterClick;
    private Runnable onIceCreamCooperativeClick;

    public SelectPVPMode() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Selecciona Modo PVP");
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
                    double escalaAncho = (double) this.getWidth() / imgWidth;
                    double escalaAlto = (double) this.getHeight() / imgHeight;
                    double escala = Math.min(escalaAncho, escalaAlto);

                    int nuevoAncho = (int) (imgWidth * escala);
                    int nuevoAlto = (int) (imgHeight * escala);

                    int x = (this.getWidth() - nuevoAncho) / 2;
                    int y = (this.getHeight() - nuevoAlto) / 2;

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    g.drawImage(imagenFondo, x, y, nuevoAncho, nuevoAlto, this);
                }
            }
        };

        panelFondo.setLayout(new BorderLayout());

        // Panel central con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Letrero "Modo PVP"
        JLabel letreromodos = crearLetrero("Modo PVP");
        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.NONE;
        gbcLetrero.weightx = 0.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(30, 0, 0, 0);
        panelCentral.add(letreromodos, gbcLetrero);

        // Panel con dos botones
        JPanel panelBotones = crearPanelBotones();
        GridBagConstraints gbcBotones = new GridBagConstraints();
        gbcBotones.gridx = 0;
        gbcBotones.gridy = 1;
        gbcBotones.anchor = GridBagConstraints.CENTER;
        gbcBotones.fill = GridBagConstraints.NONE;
        gbcBotones.weightx = 0.0;
        gbcBotones.weighty = 1.0;
        gbcBotones.insets = new Insets(0, 0, 0, 0);
        panelCentral.add(panelBotones, gbcBotones);

        // Botón Back
        JButton botonBack = crearBotonBack();
        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.gridx = 0;
        gbcBack.gridy = 2;
        gbcBack.anchor = GridBagConstraints.SOUTH;
        gbcBack.fill = GridBagConstraints.NONE;
        gbcBack.weightx = 0.0;
        gbcBack.weighty = 0.0;
        gbcBack.insets = new Insets(0, 0, 30, 0);
        panelCentral.add(botonBack, gbcBack);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);

        setVisible(false);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 0));
        panel.setOpaque(false);

        // Botón Helado vs Monstruo
        panel.add(crearBoton("Helado vs Monstruo", PVPMode.ICE_CREAM_VS_MONSTER));

        // Botón Helado Cooperativo
        panel.add(crearBoton("Helado Cooperativo", PVPMode.ICE_CREAM_COOPERATIVE));

        return panel;
    }

    private JPanel crearBoton(String textoBoton, PVPMode modo) {
        // Panel para el botón
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.Y_AXIS));
        panelBoton.setOpaque(false);

        // Crear etiqueta de texto
        JLabel labelTexto = new JLabel(textoBoton);
        labelTexto.setForeground(Color.WHITE);
        labelTexto.setFont(new Font("Arial", Font.BOLD, 16));
        labelTexto.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Crear panel con borde para simular botón
        JPanel botonVisual = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Borde
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(5, 5, this.getWidth() - 10, this.getHeight() - 10, 15, 15);

                // Fondo semitransparente
                g2.setColor(new Color(100, 100, 150, 80));
                g2.fillRoundRect(5, 5, this.getWidth() - 10, this.getHeight() - 10, 15, 15);
            }
        };
        botonVisual.setPreferredSize(new Dimension(200, 100));
        botonVisual.setOpaque(false);
        botonVisual.setLayout(new BorderLayout());
        botonVisual.add(labelTexto, BorderLayout.CENTER);

        // Efecto hover
        botonVisual.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonVisual.setBackground(new Color(150, 150, 200));
                botonVisual.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botonVisual.setBackground(new Color(100, 100, 150));
                botonVisual.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (modo == PVPMode.ICE_CREAM_VS_MONSTER && onIceCreamVsMonsterClick != null) {
                    onIceCreamVsMonsterClick.run();
                } else if (modo == PVPMode.ICE_CREAM_COOPERATIVE && onIceCreamCooperativeClick != null) {
                    onIceCreamCooperativeClick.run();
                }
            }
        });

        botonVisual.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBoton.add(botonVisual);
        return panelBoton;
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
            double escala = 0.15;
            int nuevoAncho = Math.max(1, (int) (iconoNormal.getIconWidth() * escala));
            int nuevoAlto = Math.max(1, (int) (iconoNormal.getIconHeight() * escala));

            int anchoInteraccion = nuevoAncho;
            int altoInteraccion = (int) (nuevoAlto * 0.60);
            boton.setPreferredSize(new Dimension(anchoInteraccion, altoInteraccion));

            Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenNormal);
            boton.setIcon(iconoEscalado);

            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onBackClick != null) {
                        onBackClick.run();
                    }
                }
            });
        }

        return boton;
    }

    private JLabel crearLetrero(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        return label;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public void setOnIceCreamVsMonsterClick(Runnable callback) {
        this.onIceCreamVsMonsterClick = callback;
    }

    public void setOnIceCreamCooperativeClick(Runnable callback) {
        this.onIceCreamCooperativeClick = callback;
    }
}
