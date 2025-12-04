package Presentation;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase Intro - Gestiona la visualización de GIFs
 * Orden: Marca1.gif → Marca3.gif (bucle infinito)
 */
public class Intro extends JFrame implements KeyListener, MouseInputListener {

    private JLabel labelMedia;
    private volatile boolean continuarAnimacion = true;
    private volatile boolean menuAbierto = false;
    private String rutaRecursos = "Resources\\Marca\\";
    private Runnable onMenuOpen;
    private Timer animationTimer;

    public Intro() {
        inicializarVentana();
    }

    /**
     * Establece el listener para cuando se abre el menú
     */
    public void setOnMenuOpen(Runnable listener) {
        this.onMenuOpen = listener;
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Intro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(Color.BLACK);

        labelMedia = new JLabel();
        labelMedia.setHorizontalAlignment(JLabel.CENTER);
        labelMedia.setVerticalAlignment(JLabel.CENTER);
        labelMedia.setForeground(Color.WHITE);
        labelMedia.setFont(new Font("Arial", Font.BOLD, 18));

        panelPrincipal.add(labelMedia, BorderLayout.CENTER);
        add(panelPrincipal);

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
    }

    public void mostrarSecuenciaIntro() {
        setVisible(true);

        // Mostrar Marca1.gif una sola vez
        mostrarGif("Marca1.gif", 3000);

        // Bucle infinito de Marca3.gif
        while (continuarAnimacion) {
            mostrarGif("Marca3.gif", 3000);
        }
    }

    /**
     * Muestra un GIF
     */
    private void mostrarGif(String nombreGif, long duracionMs) {
        String ruta = rutaRecursos + nombreGif;
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            System.err.println("Archivo no encontrado: " + ruta);
            labelMedia.setText("Error: GIF no encontrado: " + ruta);
            return;
        }

        try {
            ImageIcon gifIcon = new ImageIcon(archivo.getAbsolutePath());

            while (gifIcon.getImageLoadStatus() == MediaTracker.LOADING) {
                Thread.sleep(50);
            }

            if (gifIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
                System.err.println("Error al cargar el GIF: " + ruta);
                labelMedia.setText("Error al cargar: " + nombreGif);
                return;
            }

            labelMedia.setText("");
            labelMedia.setIcon(gifIcon);
            labelMedia.revalidate();
            labelMedia.repaint();

            // Solo hacer sleep si se especifica duracion > 0
            if (duracionMs > 0) {
                Thread.sleep(duracionMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!menuAbierto && (e.getKeyCode() == KeyEvent.VK_ENTER ||
                e.getKeyCode() == KeyEvent.VK_SPACE)) {
            abrirMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!menuAbierto) {
            abrirMenu();
        }
    }

    /**
     * Abre el menú de inicio
     */
    private void abrirMenu() {
        if (onMenuOpen != null && !menuAbierto) {
            menuAbierto = true;
            onMenuOpen.run();
            menuAbierto = false;
        }
    }

    /**
     * Reinicia la pantalla de intro después de volver desde el juego
     */
    public void resetIntro() {
        continuarAnimacion = true;
        menuAbierto = false;
        
        if (animationTimer != null) {
            animationTimer.cancel();
            animationTimer = null;
        }
        
        labelMedia.setIcon(null);
        labelMedia.setText("");
        labelMedia.revalidate();
        labelMedia.repaint();
        
        mostrarSegundoGif();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    public void mostrarSegundoGif() {
        continuarAnimacion = true;
        menuAbierto = false;
        setVisible(true);

        // Detener timer anterior si existe
        if (animationTimer != null) {
            animationTimer.cancel();
        }

        // Usar Timer en lugar de while bloqueante
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (continuarAnimacion) {
                    SwingUtilities.invokeLater(() -> mostrarGif("Marca3.gif", 0));
                } else {
                    this.cancel();
                }
            }
        }, 0, 3000);
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
