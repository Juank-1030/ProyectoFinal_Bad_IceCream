package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase SelectLevel - Pantalla de selección de nivel
 * Permite al usuario elegir entre 3 niveles disponibles
 */
public class SelectLevel extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    private Runnable onBackClick;
    private Runnable onLevel1Click;
    private Runnable onLevel2Click;
    private Runnable onLevel3Click;

    public SelectLevel() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Seleccionar Nivel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel personalizado que dibuja la imagen de fondo
        JPanel panelFondo = new JPanel() {
            private Image imagenFondo;

            {
                File archivoFondo = new File(rutaFondo);
                if (archivoFondo.exists()) {
                    ImageIcon icon = new ImageIcon(archivoFondo.getAbsolutePath());
                    imagenFondo = icon.getImage();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                }

                // Dibujar título
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Font fontTitulo = new Font("Arial", Font.BOLD, 48);
                g2d.setFont(fontTitulo);
                g2d.setColor(Color.WHITE);
                String titulo = "SELECCIONA UN NIVEL";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 80);

                // Dibujar descripción
                Font fontDesc = new Font("Arial", Font.PLAIN, 16);
                g2d.setFont(fontDesc);
                g2d.setColor(new Color(200, 200, 200));
                String desc = "Elige la dificultad del nivel";
                x = (getWidth() - fm.stringWidth(desc)) / 2;
                g2d.drawString(desc, x, 110);
            }
        };
        panelFondo.setLayout(null);

        // Botones de niveles
        JPanel level1 = crearBotonNivel("Nivel 1", "⭐", 150, new Color(100, 200, 255));
        JPanel level2 = crearBotonNivel("Nivel 2", "⭐⭐", 325, new Color(255, 200, 100));
        JPanel level3 = crearBotonNivel("Nivel 3", "⭐⭐⭐", 500, new Color(255, 100, 100));

        panelFondo.add(level1);
        panelFondo.add(level2);
        panelFondo.add(level3);

        // Botón Back
        JButton btnBack = crearBotonBack();
        panelFondo.add(btnBack);

        add(panelFondo);
    }

    private JPanel crearBotonNivel(String texto, String dificultad, int posX, Color colorFondo) {
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.Y_AXIS));
        panelBoton.setBounds(posX, 220, 120, 160);
        panelBoton.setOpaque(false);

        JButton boton = new JButton();
        boton.setLayout(new BoxLayout(boton, BoxLayout.Y_AXIS));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setOpaque(true);

        Font fontTexto = new Font("Arial", Font.BOLD, 20);
        Font fontDif = new Font("Arial", Font.PLAIN, 16);

        JLabel labelTexto = new JLabel(texto);
        labelTexto.setFont(fontTexto);
        labelTexto.setForeground(Color.BLACK);
        labelTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelDif = new JLabel(dificultad);
        labelDif.setFont(fontDif);
        labelDif.setForeground(Color.BLACK);
        labelDif.setAlignmentX(Component.CENTER_ALIGNMENT);

        boton.add(Box.createVerticalStrut(20));
        boton.add(labelTexto);
        boton.add(Box.createVerticalStrut(10));
        boton.add(labelDif);
        boton.add(Box.createVerticalStrut(20));

        boton.setPreferredSize(new Dimension(120, 120));
        boton.setMaximumSize(new Dimension(120, 120));
        boton.setMinimumSize(new Dimension(120, 120));

        // Eventos de ratón para efectos visuales
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorFondo.brighter());
                boton.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorFondo);
                boton.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                accionBotonNivel(texto);
            }
        });

        panelBoton.add(boton);
        return panelBoton;
    }

    private void accionBotonNivel(String nombreNivel) {
        if (nombreNivel.equals("Nivel 1") && onLevel1Click != null) {
            onLevel1Click.run();
        } else if (nombreNivel.equals("Nivel 2") && onLevel2Click != null) {
            onLevel2Click.run();
        } else if (nombreNivel.equals("Nivel 3") && onLevel3Click != null) {
            onLevel3Click.run();
        }
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
            // Posicionar a la izquierda del centro (X=300) y más abajo (Y=460)
            boton.setBounds(300, 460, anchoInteraccion, altoInteraccion);

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
        } else {
            // Fallback: mostrar botón de texto
            boton.setText("← ATRÁS");
            boton.setBackground(new Color(100, 100, 100));
            boton.setForeground(Color.WHITE);
            boton.setFont(new Font("Arial", Font.BOLD, 14));
            boton.setBorder(BorderFactory.createRaisedBevelBorder());
            boton.setOpaque(true);
            // Posicionar a la izquierda del centro (X=290) y más abajo (Y=460)
            boton.setBounds(290, 460, 120, 40);
        }

        return boton;
    }

    private void accionBotonBack() {
        if (onBackClick != null) {
            onBackClick.run();
        }
    }

    // Setters para los callbacks
    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public void setOnLevel1Click(Runnable callback) {
        this.onLevel1Click = callback;
    }

    public void setOnLevel2Click(Runnable callback) {
        this.onLevel2Click = callback;
    }

    public void setOnLevel3Click(Runnable callback) {
        this.onLevel3Click = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SelectLevel select = new SelectLevel();
            select.setVisible(true);
        });
    }
}
