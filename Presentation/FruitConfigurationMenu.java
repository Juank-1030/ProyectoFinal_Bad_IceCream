package Presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import javax.swing.*;

/**
 * Clase FruitConfigurationMenu - Menú para configurar frutas personalizadas
 * del nivel
 * Permite agregar tipos diferentes de frutas con cantidad específica
 */
public class FruitConfigurationMenu extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";

    // Tipos de frutas disponibles
    private static final String[] FRUIT_TYPES = { "Uvas", "Plátanos", "Cerezas", "Piñas", "Cactus"};

    // Configuración de frutas: [tipo][cantidad]
    private Map<String, Integer> fruitConfig = new LinkedHashMap<>();
    private java.util.List<FruitSlot> fruitSlots = new ArrayList<>();

    private Runnable onConfirmClick;
    private Runnable onBackClick;

    private static class FruitSlot {
        JComboBox<String> typeCombo;
        JSpinner quantitySpinner;
        JButton removeBtn;

        FruitSlot(JComboBox<String> typeCombo, JSpinner quantitySpinner, JButton removeBtn) {
            this.typeCombo = typeCombo;
            this.quantitySpinner = quantitySpinner;
            this.removeBtn = removeBtn;
        }
    }

    public FruitConfigurationMenu() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Configurar Frutas");
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
                String titulo = "CONFIGURAR FRUTAS";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 60);

                // Instrucción
                Font fontInst = new Font("Arial", Font.PLAIN, 14);
                g2d.setFont(fontInst);
                g2d.setColor(new Color(200, 200, 200));
                String inst = "(Selecciona tipos y cantidades de frutas)";
                x = (getWidth() - fm.stringWidth(inst)) / 2;
                g2d.drawString(inst, x, 90);
            }
        };
        panelFondo.setLayout(null);

        // Crear panel scrollable para los slots de frutas
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
        slotPanel.setOpaque(false);

        // Agregar primer slot vacío
        agregarSlot(slotPanel);

        JScrollPane scrollPane = new JScrollPane(slotPanel);
        scrollPane.setBounds(50, 120, 700, 380);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelFondo.add(scrollPane);

        // Botón para agregar más frutas
        JButton addFruitBtn = new JButton("+ AGREGAR FRUTA");
        addFruitBtn.setBounds(50, 510, 700, 40);
        addFruitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addFruitBtn.setBackground(new Color(100, 200, 100));
        addFruitBtn.setForeground(Color.BLACK);
        addFruitBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        addFruitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addFruitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addFruitBtn.setBackground(new Color(120, 220, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addFruitBtn.setBackground(new Color(100, 200, 100));
            }
        });
        addFruitBtn.addActionListener(e -> {
            if (fruitSlots.size() < FRUIT_TYPES.length) {
                agregarSlot(slotPanel);
                slotPanel.revalidate();
                slotPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Máximo de tipos de frutas alcanzado");
            }
        });
        panelFondo.add(addFruitBtn);

        // Panel de botones (Confirmar / Atrás)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(50, 560, 700, 60);

        JButton confirmBtn = new JButton("CONFIRMAR");
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 14));
        confirmBtn.setBackground(new Color(70, 130, 180));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setPreferredSize(new Dimension(300, 40));
        confirmBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                confirmBtn.setBackground(new Color(100, 160, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                confirmBtn.setBackground(new Color(70, 130, 180));
            }
        });
        confirmBtn.addActionListener(e -> {
            guardarConfiguracion();
            if (onConfirmClick != null) {
                onConfirmClick.run();
            }
        });
        buttonPanel.add(confirmBtn);

        JButton backBtn = new JButton("ATRÁS");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setBackground(new Color(180, 70, 70));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(300, 40));
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(210, 100, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(new Color(180, 70, 70));
            }
        });
        backBtn.addActionListener(e -> {
            fruitConfig.clear();
            if (onBackClick != null) {
                onBackClick.run();
            }
        });
        buttonPanel.add(backBtn);

        panelFondo.add(buttonPanel);

        setContentPane(panelFondo);
    }

    private void agregarSlot(JPanel slotPanel) {
        JPanel slotContainer = new JPanel();
        slotContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        slotContainer.setOpaque(false);
        slotContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // ComboBox para tipo de fruta
        JComboBox<String> fruitCombo = new JComboBox<>(FRUIT_TYPES);
        fruitCombo.setPreferredSize(new Dimension(200, 35));
        fruitCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        slotContainer.add(fruitCombo);

        // Spinner para cantidad
        SpinnerModel model = new SpinnerNumberModel(5, 1, 50, 1);
        JSpinner quantitySpinner = new JSpinner(model);
        quantitySpinner.setPreferredSize(new Dimension(80, 35));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 12));
        slotContainer.add(quantitySpinner);

        // Botón para eliminar slot
        JButton removeBtn = new JButton("X");
        removeBtn.setPreferredSize(new Dimension(40, 35));
        removeBtn.setBackground(new Color(200, 100, 100));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            slotPanel.remove(slotContainer);
            fruitSlots.remove(new FruitSlot(fruitCombo, quantitySpinner, removeBtn));
            slotPanel.revalidate();
            slotPanel.repaint();
            guardarConfiguracion(); // Actualizar configuración al remover
        });
        slotContainer.add(removeBtn);

        fruitSlots.add(new FruitSlot(fruitCombo, quantitySpinner, removeBtn));

        // Agregar listeners para actualizar configuración en tiempo real
        fruitCombo.addItemListener(e -> guardarConfiguracion());
        quantitySpinner.addChangeListener(e -> guardarConfiguracion());

        slotPanel.add(slotContainer);
    }

    private void guardarConfiguracion() {
        fruitConfig.clear();
        for (FruitSlot slot : fruitSlots) {
            String fruitType = (String) slot.typeCombo.getSelectedItem();
            Integer quantity = (Integer) slot.quantitySpinner.getValue();
            fruitConfig.put(fruitType, quantity);
        }
    }

    public void setOnConfirmClick(Runnable callback) {
        this.onConfirmClick = callback;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public Map<String, Integer> getFruitConfiguration() {
        return new LinkedHashMap<>(fruitConfig);
    }

    public boolean isConfigurationEmpty() {
        return fruitConfig.isEmpty();
    }
}
