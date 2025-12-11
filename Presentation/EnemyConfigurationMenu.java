package Presentation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * Clase EnemyConfigurationMenu - Menú para configurar enemigos personalizados
 * del nivel
 * Permite agregar hasta 3 tipos diferentes de enemigos con cantidad específica
 */
public class EnemyConfigurationMenu extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    // Tipos de enemigos disponibles (nombres amigables para el usuario)
    private static final String[] ENEMY_TYPES = { "Troll", "Narval", "Calamar Naranja", "Olla" };

    // Configuración de enemigos: [tipo][cantidad]
    private Map<String, Integer> enemyConfig = new LinkedHashMap<>();
    private java.util.List<EnemySlot> enemySlots = new ArrayList<>();

    // Enemigo a excluir (para PVP Vs Monstruo)
    private String excludedEnemyType = null;

    private Runnable onConfirmClick;
    private Runnable onBackClick;

    private static class EnemySlot {
        JComboBox<String> typeCombo;
        JSpinner quantitySpinner;
        JButton removeBtn;

        EnemySlot(JComboBox<String> typeCombo, JSpinner quantitySpinner, JButton removeBtn) {
            this.typeCombo = typeCombo;
            this.quantitySpinner = quantitySpinner;
            this.removeBtn = removeBtn;
        }
    }

    public EnemyConfigurationMenu() {
        this(null);
    }

    public EnemyConfigurationMenu(String excludedEnemyType) {
        this.excludedEnemyType = excludedEnemyType;
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Configurar Enemigos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
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

                Font fontTitulo = new Font("Arial", Font.BOLD, 40);
                g2d.setFont(fontTitulo);
                g2d.setColor(Color.WHITE);
                String titulo = "CONFIGURAR ENEMIGOS";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 60);

                // Instrucción
                Font fontInst = new Font("Arial", Font.PLAIN, 14);
                g2d.setFont(fontInst);
                g2d.setColor(new Color(200, 200, 200));
                String inst = "(Máximo 3 tipos diferentes de enemigos)";
                x = (getWidth() - fm.stringWidth(inst)) / 2;
                g2d.drawString(inst, x, 90);
            }
        };
        panelFondo.setLayout(null);

        // Crear panel scrollable para los slots de enemigos
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
        slotPanel.setOpaque(false);

        // Agregar primer slot vacío
        agregarSlot(slotPanel);

        JScrollPane scrollPane = new JScrollPane(slotPanel);
        scrollPane.setBounds(50, 120, 700, 350);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panelFondo.add(scrollPane);

        // Botón agregar más enemigos
        JButton btnAddEnemy = crearBoton("+ AGREGAR ENEMIGO", 50, 490, new Color(100, 150, 200));
        btnAddEnemy.addActionListener(e -> {
            if (enemySlots.size() < 3) {
                agregarSlot(slotPanel);
                slotPanel.revalidate();
                slotPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Máximo 3 tipos de enemigos permitidos",
                        "Límite alcanzado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelFondo.add(btnAddEnemy);

        // Botón confirmar
        JButton btnConfirm = crearBoton("✓ CONFIRMAR", 300, 600, new Color(100, 200, 100));
        btnConfirm.addActionListener(e -> {
            guardarConfiguracion();
            if (onConfirmClick != null) {
                onConfirmClick.run();
            }
        });
        panelFondo.add(btnConfirm);

        // Botón atrás
        JButton btnBack = crearBoton("← ATRÁS", 550, 600, new Color(200, 100, 100));
        btnBack.addActionListener(e -> {
            if (onBackClick != null) {
                onBackClick.run();
            }
        });
        panelFondo.add(btnBack);

        add(panelFondo);
    }

    private void agregarSlot(JPanel slotPanel) {
        JPanel slotContainer = new JPanel();
        slotContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        slotContainer.setOpaque(false);
        slotContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // ComboBox de tipo de enemigo
        JComboBox<String> typeCombo = new JComboBox<>(getAvailableEnemyTypes());
        typeCombo.setPreferredSize(new Dimension(200, 35));
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        slotContainer.add(typeCombo);

        // Spinner para cantidad
        SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner quantitySpinner = new JSpinner(model);
        quantitySpinner.setPreferredSize(new Dimension(80, 35));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 12));
        slotContainer.add(quantitySpinner);

        // Botón remover
        JButton removeBtn = new JButton("X");
        removeBtn.setPreferredSize(new Dimension(40, 35));
        removeBtn.setBackground(new Color(200, 100, 100));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        EnemySlot slot = new EnemySlot(typeCombo, quantitySpinner, removeBtn);
        enemySlots.add(slot);

        // Agregar listeners para actualizar configuración en tiempo real
        typeCombo.addItemListener(e -> guardarConfiguracion());
        quantitySpinner.addChangeListener(e -> guardarConfiguracion());

        removeBtn.addActionListener(e -> {
            slotPanel.remove(slotContainer);
            enemySlots.remove(slot);
            slotPanel.revalidate();
            slotPanel.repaint();
            guardarConfiguracion(); // Actualizar configuración al remover
        });

        slotContainer.add(removeBtn);
        slotPanel.add(slotContainer);
    }

    private void guardarConfiguracion() {
        enemyConfig.clear();
        for (EnemySlot slot : enemySlots) {
            String type = (String) slot.typeCombo.getSelectedItem();
            int quantity = (Integer) slot.quantitySpinner.getValue();
            if (type != null && quantity > 0) {
                // Normalizar el nombre antes de guardar
                String normalizedType = normalizarNombreEnemigo(type);
                enemyConfig.put(normalizedType, quantity);
            }
        }
    }

    private String normalizarNombreEnemigo(String nombre) {
        // Mapear nombres amigables a nombres de Game/SelectMonster
        switch (nombre.toLowerCase()) {
            case "calamar naranja":
                return "calamar naranja"; // Usa el nombre normalizado que Game.java reconoce
            case "olla":
                return "olla"; // Usa el nombre normalizado
            default:
                return nombre;
        }
    }

    private JButton crearBoton(String texto, int x, int y, Color color) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 150, 50);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
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
    public Map<String, Integer> getEnemyConfiguration() {
        return new LinkedHashMap<>(enemyConfig);
    }

    public boolean isConfigurationEmpty() {
        return enemyConfig.isEmpty();
    }

    private String[] getAvailableEnemyTypes() {
        if (excludedEnemyType == null) {
            return ENEMY_TYPES;
        }

        java.util.List<String> available = new ArrayList<>();
        for (String type : ENEMY_TYPES) {
            if (!type.equalsIgnoreCase(excludedEnemyType)) {
                available.add(type);
            }
        }
        return available.toArray(new String[0]);
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
            EnemyConfigurationMenu menu = new EnemyConfigurationMenu();
            menu.setVisible(true);
        });
    }
}
