package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase ConfigLevel - Pantalla de configuración de objetos y enemigos del nivel
 * Permite al usuario seleccionar qué frutas y enemigos aparecerán en el nivel
 */
public class ConfigLevel extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    // Callbacks para las opciones
    private Runnable onStartLevel;
    private Runnable onBackClick;
    private Runnable onConfigureObstacles;

    // Opciones de configuración
    private boolean mostrarUvas = true;
    private boolean mostrarPlatanos = true;
    private boolean mostrarCerezas = true;
    private boolean mostrarPinas = true;
    private boolean mostrarEnemigos = true;
    
    // Configuración de obstáculos
    private java.util.Map<String, Integer> obstacleConfig = new java.util.LinkedHashMap<>();

    public ConfigLevel() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Configurar Nivel");
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

                Font fontTitulo = new Font("Arial", Font.BOLD, 40);
                g2d.setFont(fontTitulo);
                g2d.setColor(Color.WHITE);
                String titulo = "CONFIGURAR NIVEL";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 70);

                // Dibujar instrucciones
                Font fontDesc = new Font("Arial", Font.PLAIN, 16);
                g2d.setFont(fontDesc);
                g2d.setColor(new Color(200, 200, 200));
                String desc = "Selecciona qué objetos y enemigos deseas en este nivel";
                x = (getWidth() - fm.stringWidth(desc)) / 2;
                g2d.drawString(desc, x, 110);
            }
        };
        panelFondo.setLayout(null);

        // Crear checkboxes para las frutas
        JCheckBox cbUvas = crearCheckBox("Uvas", 100, 150, mostrarUvas);
        JCheckBox cbPlatanos = crearCheckBox("Plátanos", 300, 150, mostrarPlatanos);
        JCheckBox cbCerezas = crearCheckBox("Cerezas", 500, 150, mostrarCerezas);
        JCheckBox cbPinas = crearCheckBox("Piñas", 100, 220, mostrarPinas);
        JCheckBox cbEnemigos = crearCheckBox("Enemigos", 300, 220, mostrarEnemigos);

        panelFondo.add(cbUvas);
        panelFondo.add(cbPlatanos);
        panelFondo.add(cbCerezas);
        panelFondo.add(cbPinas);
        panelFondo.add(cbEnemigos);

        // Botón Configurar Obstáculos
        JButton btnObstaculos = crearBotonObstaculos();
        panelFondo.add(btnObstaculos);

        // Botón Iniciar Nivel
        JButton btnIniciar = crearBotonIniciar();
        panelFondo.add(btnIniciar);

        // Botón Back
        JButton btnBack = crearBotonBack();
        panelFondo.add(btnBack);

        add(panelFondo);
    }

    private JCheckBox crearCheckBox(String texto, int posX, int posY, boolean seleccionado) {
        JCheckBox cb = new JCheckBox(texto, seleccionado);
        cb.setBounds(posX, posY, 150, 40);
        cb.setOpaque(false);
        cb.setForeground(Color.BLACK);
        cb.setFont(new Font("Arial", Font.BOLD, 16));
        return cb;
    }

    private JButton crearBotonObstaculos() {
        JButton btn = new JButton("CONFIGURAR OBSTÁCULOS");
        btn.setBounds(250, 290, 300, 50);
        btn.setBackground(new Color(100, 150, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(120, 170, 220));
                btn.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(100, 150, 200));
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onConfigureObstacles != null) {
                    onConfigureObstacles.run();
                }
            }
        });

        return btn;
    }

    private JButton crearBotonIniciar() {
        JButton btn = new JButton("INICIAR NIVEL");
        btn.setBounds(250, 410, 300, 50);
        btn.setBackground(new Color(50, 150, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(70, 180, 70));
                btn.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(50, 150, 50));
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onStartLevel != null) {
                    onStartLevel.run();
                }
            }
        });

        return btn;
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
            boton.setBounds(300, 460, anchoInteraccion, altoInteraccion);

            Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenNormal);
            boton.setIcon(iconoEscalado);

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
                    if (onBackClick != null) {
                        onBackClick.run();
                    }
                }
            });
        }

        return boton;
    }

    // Setters para callbacks
    public void setOnStartLevel(Runnable callback) {
        this.onStartLevel = callback;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public void setOnConfigureObstacles(Runnable callback) {
        this.onConfigureObstacles = callback;
    }

    public java.util.Map<String, Integer> getObstacleConfiguration() {
        return new java.util.LinkedHashMap<>(obstacleConfig);
    }

    public void setObstacleConfiguration(java.util.Map<String, Integer> config) {
        this.obstacleConfig = new java.util.LinkedHashMap<>(config);
    }

    // Getters para opciones seleccionadas
    public boolean isMostrarUvas() {
        return mostrarUvas;
    }

    public void setMostrarUvas(boolean mostrar) {
        this.mostrarUvas = mostrar;
    }

    public boolean isMostrarPlatanos() {
        return mostrarPlatanos;
    }

    public void setMostrarPlatanos(boolean mostrar) {
        this.mostrarPlatanos = mostrar;
    }

    public boolean isMostrarCerezas() {
        return mostrarCerezas;
    }

    public void setMostrarCerezas(boolean mostrar) {
        this.mostrarCerezas = mostrar;
    }

    public boolean isMostrarPinas() {
        return mostrarPinas;
    }

    public void setMostrarPinas(boolean mostrar) {
        this.mostrarPinas = mostrar;
    }

    public boolean isMostrarEnemigos() {
        return mostrarEnemigos;
    }

    public void setMostrarEnemigos(boolean mostrar) {
        this.mostrarEnemigos = mostrar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfigLevel config = new ConfigLevel();
            config.setVisible(true);
        });
    }
}
