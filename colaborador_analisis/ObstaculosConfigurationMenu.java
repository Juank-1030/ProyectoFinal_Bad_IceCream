package Presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import javax.swing.*;

/**
 * Menu para configurar obstáculos (Fogatas y Baldosas Calientes)
 * Permite agregar hasta 2 tipos diferentes de obstáculos y especificar cantidad.
 */
public class ObstaculosConfigurationMenu extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    // Tipos de obstáculos disponibles:
    private static final String[] OBSTACLE_TYPES = { "Fogata", "Baldosa Caliente" };

    // Map tipo → cantidad
    private Map<String, Integer> obstacleConfig = new LinkedHashMap<>();
    private java.util.List<ObstacleSlot> obstacleSlots = new ArrayList<>();

    private Runnable onConfirmClick;
    private Runnable onBackClick;

    private static class ObstacleSlot {
        JComboBox<String> typeCombo;
        JSpinner quantitySpinner;
        JButton removeBtn;

        ObstacleSlot(JComboBox<String> typeCombo, JSpinner quantitySpinner, JButton removeBtn) {
            this.typeCombo = typeCombo;
            this.quantitySpinner = quantitySpinner;
            this.removeBtn = removeBtn;
        }
    }

    public ObstaculosConfigurationMenu() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Configurar Obstáculos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo
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
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Font fontTitulo = new Font("Arial", Font.BOLD, 36);
                g2d.setFont(fontTitulo);
                g2d.setColor(Color.WHITE);
                String titulo = "CONFIGURAR OBSTÁCULOS";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 60);
            }
        };
        panelFondo.setLayout(null);

        // Panel de slots scrollable
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
        slotPanel.setOpaque(false);

        // Agrega un primer slot
        agregarSlot(slotPanel);

        JScrollPane scrollPane = new JScrollPane(slotPanel);
        scrollPane.setBounds(60, 120, 620, 240);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150,150,150)),
                "Tipo y Cantidad",
                0, 0,
                new Font("Arial", Font.BOLD, 18), Color.DARK_GRAY));
        panelFondo.add(scrollPane);

        // Botón agregar
        JButton btnAdd = crearBoton("+ AGREGAR OBSTÁCULO", 60, 375, new Color(130, 180, 255));
        btnAdd.addActionListener(e -> {
            if (obstacleSlots.size() < OBSTACLE_TYPES.length) {
                agregarSlot(slotPanel);
                slotPanel.revalidate();
                slotPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Sólo puedes agregar cada tipo de obstáculo una vez.",
                        "Límite alcanzado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelFondo.add(btnAdd);

        // Confirmar
        JButton btnConfirm = crearBoton("✓ CONFIRMAR", 170, 440, new Color(100, 200, 100));
        btnConfirm.addActionListener(e -> {
            guardarConfiguracion();
            if (onConfirmClick != null) onConfirmClick.run();
        });
        panelFondo.add(btnConfirm);

        // Atrás
        JButton btnBack = crearBoton("← ATRÁS", 380, 440, new Color(200, 100, 100));
        btnBack.addActionListener(e -> {
            if (onBackClick != null) onBackClick.run();
        });
        panelFondo.add(btnBack);

        add(panelFondo);
    }

    private void agregarSlot(JPanel slotPanel) {
        JPanel slotContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        slotContainer.setOpaque(false);
        slotContainer.setMaximumSize(new Dimension(580, 56));

        JComboBox<String> typeCombo = new JComboBox<>(getAvailableObstacleTypes());
        typeCombo.setPreferredSize(new Dimension(210, 36));
        slotContainer.add(typeCombo);

        SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner quantitySpinner = new JSpinner(model);
        quantitySpinner.setPreferredSize(new Dimension(90, 36));
        slotContainer.add(quantitySpinner);

        JButton removeBtn = new JButton("X");
        removeBtn.setPreferredSize(new Dimension(40, 36));
        removeBtn.setBackground(new Color(200, 100, 100));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        removeBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ObstacleSlot slot = new ObstacleSlot(typeCombo, quantitySpinner, removeBtn);
        obstacleSlots.add(slot);

        // Listeners
        typeCombo.addItemListener(e -> guardarConfiguracion());
        quantitySpinner.addChangeListener(e -> guardarConfiguracion());

        removeBtn.addActionListener(e -> {
            slotPanel.remove(slotContainer);
            obstacleSlots.remove(slot);
            slotPanel.revalidate();
            slotPanel.repaint();
            guardarConfiguracion();
        });

        slotContainer.add(removeBtn);
        slotPanel.add(slotContainer);
    }

    private String[] getAvailableObstacleTypes() {
        Set<String> usados = new HashSet<>();
        for (ObstacleSlot slot : obstacleSlots) {
            String selected = (String) slot.typeCombo.getSelectedItem();
            if (selected != null) usados.add(selected);
        }
        java.util.List<String> disponibles = new ArrayList<>();
        for (String type : OBSTACLE_TYPES) {
            if (!usados.contains(type))
                disponibles.add(type);
        }
        return disponibles.isEmpty() ? OBSTACLE_TYPES : disponibles.toArray(new String[0]);
    }

    private void guardarConfiguracion() {
        obstacleConfig.clear();
        for (ObstacleSlot slot : obstacleSlots) {
            String type = (String) slot.typeCombo.getSelectedItem();
            int quantity = (Integer) slot.quantitySpinner.getValue();
            if (type != null && quantity > 0) {
                obstacleConfig.put(type, quantity);
            }
        }
    }

    private JButton crearBoton(String texto, int x, int y, Color color) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 180, 48);
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

    // Getters
    public Map<String, Integer> getObstacleConfiguration() {
        guardarConfiguracion(); // actualiza el map antes de pedirlo
        return new LinkedHashMap<>(obstacleConfig);
    }

    public void setOnConfirmClick(Runnable callback) {
        this.onConfirmClick = callback;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ObstaculosConfigurationMenu menu = new ObstaculosConfigurationMenu();
            menu.setVisible(true);
        });
    }
}