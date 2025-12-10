package Presentation;

import Domain.IceCreamAIStrategyManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Menú para seleccionar la estrategia de IA del helado en modo MVM
 * 
 * RESTRICCIONES MVC:
 * - ✅ Puede LEER de Domain (IceCreamAIStrategyManager) para obtener estrategias
 * disponibles
 * - ❌ NUNCA puede MODIFICAR Domain
 * - Solo obtiene lista de estrategias y notifica selección a través de
 * callbacks
 */
public class SelectIceCreamAI extends JFrame {
    private Runnable onBackClick;
    private java.util.Map<String, Runnable> strategyCallbacks = new java.util.HashMap<>();

    public SelectIceCreamAI() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Seleccionar IA del Helado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel personalizado que dibuja la imagen de fondo
        JPanel panelFondo = new JPanel() {
            private Image imagenFondo;

            {
                String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
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
            }
        };

        panelFondo.setLayout(new BoxLayout(panelFondo, BoxLayout.Y_AXIS));

        // Título
        JLabel titulo = new JLabel("Selecciona la IA del Helado");
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFondo.add(Box.createVerticalStrut(50));
        panelFondo.add(titulo);
        panelFondo.add(Box.createVerticalStrut(40));

        // Obtener estrategias disponibles
        String[] strategies = IceCreamAIStrategyManager.getAvailableStrategies();

        // Crear botones para cada estrategia
        for (String strategy : strategies) {
            JButton btnStrategy = crearBotonEstrategia(strategy);
            panelFondo.add(btnStrategy);
            panelFondo.add(Box.createVerticalStrut(15));
        }

        panelFondo.add(Box.createVerticalStrut(30));

        // Botón Volver
        JButton btnBack = new JButton("Volver");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setPreferredSize(new Dimension(200, 40));
        btnBack.setMaximumSize(new Dimension(200, 40));
        btnBack.addActionListener(e -> {
            if (onBackClick != null) {
                onBackClick.run();
            }
        });
        panelFondo.add(btnBack);
        panelFondo.add(Box.createVerticalStrut(20));

        setContentPane(panelFondo);
        setVisible(false);
    }

    private JButton crearBotonEstrategia(String strategyName) {
        JButton btn = new JButton(strategyName);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setMaximumSize(new Dimension(300, 50));
        btn.addActionListener(e -> {
            Runnable callback = strategyCallbacks.get(strategyName);
            if (callback != null) {
                callback.run();
            }
        });
        return btn;
    }

    public void setOnStrategyClick(String strategy, Runnable callback) {
        strategyCallbacks.put(strategy, callback);
    }

    public void setOnBackClick(Runnable callback) {
        onBackClick = callback;
    }
}
