package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase LevelCompletionMenu - Menú que aparece al completar un nivel
 * Permite continuar al siguiente nivel, reintentar, o volver al menú
 */
public class LevelCompletionMenu extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private int nivelActual;
    private int puntuacion;

    private Runnable onNextLevelClick;
    private Runnable onRetryClick;
    private Runnable onMenuClick;

    public LevelCompletionMenu(int nivelActual, int puntuacion) {
        this.nivelActual = nivelActual;
        this.puntuacion = puntuacion;
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Nivel Completado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Título
                Font fontTitulo = new Font("Arial", Font.BOLD, 48);
                g2d.setFont(fontTitulo);
                g2d.setColor(new Color(100, 200, 100));
                String titulo = "¡NIVEL COMPLETADO!";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(titulo)) / 2;
                g2d.drawString(titulo, x, 80);

                // Información
                Font fontInfo = new Font("Arial", Font.PLAIN, 24);
                g2d.setFont(fontInfo);
                g2d.setColor(Color.WHITE);

                String nivelInfo = "Nivel " + nivelActual;
                x = (getWidth() - fm.stringWidth(nivelInfo)) / 2;
                g2d.drawString(nivelInfo, x, 150);

                String puntosInfo = "Puntuación: " + puntuacion;
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(puntosInfo)) / 2;
                g2d.drawString(puntosInfo, x, 200);
            }
        };
        panelFondo.setLayout(null);

        // Botón siguiente nivel
        JButton btnNext = crearBoton("→ SIGUIENTE NIVEL", 150, 300, new Color(100, 200, 100));
        btnNext.addActionListener(e -> {
            if (onNextLevelClick != null) {
                onNextLevelClick.run();
            }
        });
        panelFondo.add(btnNext);

        // Botón reintentar
        JButton btnRetry = crearBoton("🔄 REINTENTAR", 50, 400, new Color(255, 200, 100));
        btnRetry.addActionListener(e -> {
            if (onRetryClick != null) {
                onRetryClick.run();
            }
        });
        panelFondo.add(btnRetry);

        // Botón menú
        JButton btnMenu = crearBoton("🏠 MENÚ", 600, 400, new Color(100, 150, 200));
        btnMenu.addActionListener(e -> {
            if (onMenuClick != null) {
                onMenuClick.run();
            }
        });
        panelFondo.add(btnMenu);

        add(panelFondo);
    }

    private JButton crearBoton(String texto, int x, int y, Color color) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 150, 60);
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

    // Setters para callbacks
    public void setOnNextLevelClick(Runnable callback) {
        this.onNextLevelClick = callback;
    }

    public void setOnRetryClick(Runnable callback) {
        this.onRetryClick = callback;
    }

    public void setOnMenuClick(Runnable callback) {
        this.onMenuClick = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LevelCompletionMenu menu = new LevelCompletionMenu(1, 1500);
            menu.setVisible(true);
        });
    }
}
