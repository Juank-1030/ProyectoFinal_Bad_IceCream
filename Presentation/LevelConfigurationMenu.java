package Presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;

/**
 * Clase LevelConfigurationMenu - Menú para configurar objetos y enemigos del
 * nivel
 * Permite al usuario seleccionar qué frutas y enemigos aparecerán en el nivel
 */
public class LevelConfigurationMenu extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    // Configuración seleccionada
    private boolean mostrarUvas = true;
    private boolean mostrarPlatanos = true;
    private boolean mostrarCerezas = true;
    private boolean mostrarPinas = true;

    private boolean mostrarTroll = true;
    private boolean mostrarNarval = true;
    private boolean mostrarCalamarNaranja = true;
    private boolean mostrarOlla = true;

    private boolean mostrarFogatas = true;
    private boolean mostrarBaldosasCalientes = true;
    private int cantidadFogatas = 2; // valor por defecto

    private Runnable onConfirmClick;
    private Runnable onBackClick;

    public LevelConfigurationMenu() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Configurar Nivel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel personalizado con fondo
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
                g2d.drawString(titulo, x, 60);
            }
        };
        panelFondo.setLayout(null);

        // Panel de frutas
        JLabel labelFrutas = new JLabel("FRUTAS:");
        labelFrutas.setFont(new Font("Arial", Font.BOLD, 18));
        labelFrutas.setForeground(Color.WHITE);
        labelFrutas.setBounds(50, 100, 200, 30);
        panelFondo.add(labelFrutas);

        // Checkboxes para frutas
        JCheckBox cbUvas = crearCheckbox("Uvas (50 pts)", 50, 140, mostrarUvas);
        cbUvas.addActionListener(e -> mostrarUvas = cbUvas.isSelected());
        panelFondo.add(cbUvas);

        JCheckBox cbPlatanos = crearCheckbox("Plátanos (100 pts)", 50, 180, mostrarPlatanos);
        cbPlatanos.addActionListener(e -> mostrarPlatanos = cbPlatanos.isSelected());
        panelFondo.add(cbPlatanos);

        JCheckBox cbCerezas = crearCheckbox("Cerezas (150 pts)", 50, 220, mostrarCerezas);
        cbCerezas.addActionListener(e -> mostrarCerezas = cbCerezas.isSelected());
        panelFondo.add(cbCerezas);

        JCheckBox cbPinas = crearCheckbox("Piñas (200 pts)", 50, 260, mostrarPinas);
        cbPinas.addActionListener(e -> mostrarPinas = cbPinas.isSelected());
        panelFondo.add(cbPinas);

        // Panel de enemigos
        JLabel labelEnemigos = new JLabel("ENEMIGOS:");
        labelEnemigos.setFont(new Font("Arial", Font.BOLD, 18));
        labelEnemigos.setForeground(Color.WHITE);
        labelEnemigos.setBounds(50, 320, 200, 30);
        panelFondo.add(labelEnemigos);

        // Checkboxes para enemigos
        JCheckBox cbTroll = crearCheckbox("Troll", 50, 360, mostrarTroll);
        cbTroll.addActionListener(e -> mostrarTroll = cbTroll.isSelected());
        panelFondo.add(cbTroll);

        JCheckBox cbNarval = crearCheckbox("Narval", 50, 400, mostrarNarval);
        cbNarval.addActionListener(e -> mostrarNarval = cbNarval.isSelected());
        panelFondo.add(cbNarval);

        JCheckBox cbCalamar = crearCheckbox("Calamar Naranja", 50, 440, mostrarCalamarNaranja);
        cbCalamar.addActionListener(e -> mostrarCalamarNaranja = cbCalamar.isSelected());
        panelFondo.add(cbCalamar);

        JCheckBox cbOlla = crearCheckbox("Olla", 50, 480, mostrarOlla);
        cbOlla.addActionListener(e -> mostrarOlla = cbOlla.isSelected());
        panelFondo.add(cbOlla);

        // --- OBJETOS ESPECIALES ---
        JLabel labelFogata = new JLabel("OBJETOS ESPECIALES:");
        labelFogata.setFont(new Font("Arial", Font.BOLD, 18));
        labelFogata.setForeground(Color.WHITE);
        labelFogata.setBounds(400, 100, 250, 30);
        panelFondo.add(labelFogata);


        JCheckBox cbFogatas = crearCheckbox("Fogatas (eliminan helado)", 400, 140, mostrarFogatas);
        cbFogatas.addActionListener(e -> mostrarFogatas = cbFogatas.isSelected());
        panelFondo.add(cbFogatas);

        JLabel lblCantidadFogatas = new JLabel("Cantidad de Fogatas:");
        lblCantidadFogatas.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCantidadFogatas.setForeground(Color.WHITE);
        lblCantidadFogatas.setBounds(400, 170, 150, 30);
        panelFondo.add(lblCantidadFogatas);

        JSpinner spinnerFogatas = new JSpinner(new SpinnerNumberModel(cantidadFogatas, 0, 20, 1));
        spinnerFogatas.setBounds(560, 170, 50, 30);
        spinnerFogatas.addChangeListener(e -> cantidadFogatas = (int)spinnerFogatas.getValue());
        panelFondo.add(spinnerFogatas);

        JCheckBox cbBaldosasCalientes = crearCheckbox("Baldosas Calientes", 400, 210, mostrarBaldosasCalientes);
        cbBaldosasCalientes.addActionListener(e -> mostrarBaldosasCalientes = cbBaldosasCalientes.isSelected());
        panelFondo.add(cbBaldosasCalientes);

        // Botón confirmar
        JButton btnConfirm = crearBoton("✓ CONFIRMAR", 300, 580, new Color(100, 200, 100));
        btnConfirm.addActionListener(e -> {
            if (onConfirmClick != null) {
                onConfirmClick.run();
            }
        });
        panelFondo.add(btnConfirm);

        // Botón atrás
        JButton btnBack = crearBoton("← ATRÁS", 550, 580, new Color(200, 100, 100));
        btnBack.addActionListener(e -> {
            if (onBackClick != null) {
                onBackClick.run();
            }
        });
        panelFondo.add(btnBack);

        add(panelFondo);
    }

    private JCheckBox crearCheckbox(String texto, int x, int y, boolean selected) {
        JCheckBox cb = new JCheckBox(texto);
        cb.setBounds(x, y, 300, 30);
        cb.setFont(new Font("Arial", Font.PLAIN, 14));
        cb.setForeground(Color.BLACK);
        cb.setBackground(new Color(255, 255, 255, 200));
        cb.setOpaque(true);
        cb.setSelected(selected);
        cb.setFocusPainted(false);
        return cb;
    }

    private JButton crearBoton(String texto, int x, int y, Color color) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 150, 50);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
                btn.setBorder(BorderFactory.createLoweredBevelBorder());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        return btn;
    }

    // Getters para configuración
    public boolean isMostrarUvas() {
        return mostrarUvas;
    }

    public boolean isMostrarPlatanos() {
        return mostrarPlatanos;
    }

    public boolean isMostrarCerezas() {
        return mostrarCerezas;
    }

    public boolean isMostrarPinas() {
        return mostrarPinas;
    }

    public boolean isMostrarTroll() {
        return mostrarTroll;
    }

    public boolean isMostrarNarval() {
        return mostrarNarval;
    }

    public boolean isMostrarCalamarNaranja() {
        return mostrarCalamarNaranja;
    }

    public boolean isMostrarOlla() {
        return mostrarOlla;
    }

    public boolean isMostrarFogatas() {
        return mostrarFogatas;
    }

    public boolean isMostrarBaldosasCalientes() {
        return mostrarBaldosasCalientes;
    }

    public int getCantidadFogatas() {
        return cantidadFogatas;
    }


    // Setters para callbacks
    public void setOnConfirmClick(Runnable callback) {
        this.onConfirmClick = callback;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LevelConfigurationMenu menu = new LevelConfigurationMenu();
            menu.setVisible(true);
        });
    }
}
