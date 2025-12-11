package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * SelectPVPMode - Pantalla de selección de tipo de PVP
 * Opciones: Helado vs Monstruo o Helado Cooperativo
 * 
 * RESTRICCIONES MVC:
 * - ✅ NO importa Domain - Usa Strings para representar modos
 * - ❌ NUNCA puede MODIFICAR Domain
 * - Solo notifica selección a través de callbacks
 */
public class SelectPVPMode extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private Runnable onBackClick;
    private Runnable onIceCreamVsMonsterClick;
    private Runnable onIceCreamCooperativeClick;

    public SelectPVPMode() {
        ImageLoader.loadAllImages();
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

        // Panel central con GridBagLayout para organizar elementos verticalmente
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Letrero "Modo PVP" en la parte superior
        JLabel letreromodos = crearLetrero("Modo PVP");
        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.NONE;
        gbcLetrero.weightx = 0.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(10, 0, 0, 0);
        panelCentral.add(letreromodos, gbcLetrero);

        // Panel con dos botones
        JPanel panelBotones = crearPanelBotones();
        GridBagConstraints gbcBotones = new GridBagConstraints();
        gbcBotones.gridx = 0;
        gbcBotones.gridy = 1;
        gbcBotones.anchor = GridBagConstraints.NORTH;
        gbcBotones.fill = GridBagConstraints.NONE;
        gbcBotones.weightx = 0.0;
        gbcBotones.weighty = 0.0;
        gbcBotones.insets = new Insets(30, 0, 0, 0);
        panelCentral.add(panelBotones, gbcBotones);

        // Botón Back en la parte inferior
        JButton botonBack = crearBotonBack();
        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.gridx = 0;
        gbcBack.gridy = 2;
        gbcBack.anchor = GridBagConstraints.SOUTH;
        gbcBack.fill = GridBagConstraints.NONE;
        gbcBack.weightx = 0.0;
        gbcBack.weighty = 1.0;
        gbcBack.insets = new Insets(0, 0, 50, 0);
        panelCentral.add(botonBack, gbcBack);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);
        setVisible(false);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(600, 220));

        // Botón Helado vs Monstruo (IZQUIERDA)
        JPanel botonVsMonstruo = crearBotonConImagen("vs_monster", "ICE_CREAM_VS_MONSTER");
        botonVsMonstruo.setBounds(20, 6, 280, 200);
        panel.add(botonVsMonstruo);

        // Botón Helado Cooperativo (DERECHA)
        JPanel botonCoop = crearBotonConImagen("coop", "ICE_CREAM_COOPERATIVE");
        botonCoop.setBounds(280, 25, 280, 200);
        panel.add(botonCoop);

        return panel;
    }

    private JPanel crearBotonConImagen(String imagenKey, String modo) {
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BorderLayout());
        panelBoton.setOpaque(false);

        // Determinar la ruta de la imagen según el botón
        String rutaImagen = "";
        if (imagenKey.equals("vs_monster")) {
            rutaImagen = "Resources/HELADO VS MOUNSTRUO/Helado vs mousntruo.png";
        } else if (imagenKey.equals("coop")) {
            rutaImagen = "Resources/HELADOVSHELADO/Cooperativo.png";
        }

        // Cargar la imagen desde el archivo
        File archivoImagen = new File(rutaImagen);

        if (archivoImagen.exists()) {
            ImageIcon icono = new ImageIcon(archivoImagen.getAbsolutePath());

            // Calcular escala manteniendo proporción
            int imgWidth = icono.getIconWidth();
            int imgHeight = icono.getIconHeight();

            // Tamaño objetivo
            int targetWidth = 400;
            int targetHeight = 250;

            // Calcular escala para que quepa en el contenedor
            double scaleX = (double) targetWidth / imgWidth;
            double scaleY = (double) targetHeight / imgHeight;
            double scale = Math.min(scaleX, scaleY);

            int anchoFinal = (int) (imgWidth * scale);
            int altoFinal = (int) (imgHeight * scale);

            Image imagenEscalada = icono.getImage().getScaledInstance(anchoFinal, altoFinal, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

            JLabel labelImagen = new JLabel(iconoEscalado);
            labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
            labelImagen.setVerticalAlignment(SwingConstants.CENTER);
            labelImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Listener para clicks
            labelImagen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if ("ICE_CREAM_VS_MONSTER".equals(modo) && onIceCreamVsMonsterClick != null) {
                        onIceCreamVsMonsterClick.run();
                    } else if ("ICE_CREAM_COOPERATIVE".equals(modo) && onIceCreamCooperativeClick != null) {
                        onIceCreamCooperativeClick.run();
                    }
                }
            });

            panelBoton.add(labelImagen, BorderLayout.CENTER);
        } else {
            // Fallback: mostrar texto si no encuentra la imagen
            JLabel labelTexto = new JLabel(
                    "ICE_CREAM_VS_MONSTER".equals(modo) ? "Helado vs Monstruo" : "Helado Cooperativo");
            labelTexto.setForeground(Color.WHITE);
            labelTexto.setFont(new Font("Arial", Font.BOLD, 16));
            labelTexto.setHorizontalAlignment(SwingConstants.CENTER);
            panelBoton.add(labelTexto, BorderLayout.CENTER);
        }

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

            Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
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
        label.setFont(new Font("Arial", Font.BOLD, 36));
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