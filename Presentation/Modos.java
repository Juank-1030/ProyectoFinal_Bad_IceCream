package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase Modos - Muestra la pantalla de selección de modos de juego (PVP, PVM,
 * MVM)
 */
public class Modos extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private String rutaBotones = "Resources/Botones/modes/";

    public Modos() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Modos de Juego");
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
                    // Escalar imagen para llenar la ventana manteniendo aspecto
                    double escalaAncho = (double) this.getWidth() / imgWidth;
                    double escalaAlto = (double) this.getHeight() / imgHeight;
                    double escala = Math.min(escalaAncho, escalaAlto);

                    int nuevoAncho = (int) (imgWidth * escala);
                    int nuevoAlto = (int) (imgHeight * escala);

                    // Centrar la imagen
                    int x = (this.getWidth() - nuevoAncho) / 2;
                    int y = (this.getHeight() - nuevoAlto) / 2;

                    // Dibujar fondo negro
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    // Dibujar imagen escalada
                    g.drawImage(imagenFondo, x, y, nuevoAncho, nuevoAlto, this);
                }
            }
        };

        panelFondo.setLayout(new BorderLayout());

        // Crear botones
        JPanel botonPVP = crearBoton("PVP", "Player vs Player", true); // true = aumentar 15%
        JPanel botonPVM = crearBoton("PVM", "Player vs Machine", false);
        JPanel botonMVM = crearBoton("MVM", "Machine vs Machine", false);

        // Panel contenedor horizontal para los tres botones centrado
        JPanel panelBotonesFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        panelBotonesFila.setOpaque(false);
        panelBotonesFila.add(botonPVP);
        panelBotonesFila.add(botonPVM);
        panelBotonesFila.add(botonMVM);

        // Panel para los letreros
        JPanel panelLetresFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 66, 0));
        panelLetresFila.setOpaque(false);

        // Agregar letreros
        agregarLetrero(panelLetresFila, "PVP");
        agregarLetrero(panelLetresFila, "PVM");
        agregarLetrero(panelLetresFila, "MVM");

        // Panel contenedor vertical para letreros y botones
        JPanel panelConLetrero = new JPanel();
        panelConLetrero.setLayout(new BoxLayout(panelConLetrero, BoxLayout.Y_AXIS));
        panelConLetrero.setOpaque(false);
        panelConLetrero.add(panelLetresFila);
        panelConLetrero.add(Box.createVerticalStrut(10));
        panelConLetrero.add(panelBotonesFila);

        // Panel para centrar verticalmente y mover con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(100, 0, 0, 0);
        panelCentral.add(panelLetresFila, gbc);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.anchor = GridBagConstraints.NORTH;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.weightx = 1.0;
        gbc2.insets = new Insets(150, 0, 0, 0);
        panelCentral.add(panelBotonesFila, gbc2);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);

        setVisible(true);
    }

    private JPanel crearBoton(String nombreBoton, String textoLabel, boolean aumentarTamaño) {
        String rutaNormal = rutaBotones + nombreBoton + ".png";
        String rutaSeleccionado = rutaBotones + nombreBoton + "_Selected.png";

        File archivoNormal = new File(rutaNormal);
        File archivoSeleccionado = new File(rutaSeleccionado);

        if (!archivoNormal.exists() || !archivoSeleccionado.exists()) {
            System.err.println("Error: No se encontró " + (archivoNormal.exists() ? rutaSeleccionado : rutaNormal));
            JPanel panelError = new JPanel();
            panelError.add(new JLabel("Error"));
            return panelError;
        }

        ImageIcon iconoNormal = new ImageIcon(rutaNormal);
        ImageIcon iconoSeleccionado = new ImageIcon(rutaSeleccionado);

        // Escalar botones al 18% de su tamaño original, o 23.8% si es PVP (+15%)
        double escala = aumentarTamaño ? 0.238 : 0.18; // 0.207 * 1.15 = 0.238
        int nuevoAncho = Math.max(1, (int) (iconoNormal.getIconWidth() * escala));
        int nuevoAlto = Math.max(1, (int) (iconoNormal.getIconHeight() * escala));

        Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        Image imagenSeleccionada = iconoSeleccionado.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                Image.SCALE_SMOOTH);

        ImageIcon iconoNormalEscalado = new ImageIcon(imagenNormal);
        ImageIcon iconoSeleccionadoEscalado = new ImageIcon(imagenSeleccionada);

        // Panel para el botón y su etiqueta
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.Y_AXIS));
        panelBoton.setOpaque(false);

        // Crear botón con imagen
        JButton boton = new JButton(iconoNormalEscalado);
        boton.setPressedIcon(iconoSeleccionadoEscalado);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(0, 0, 0, 0));
        // Usar zona de interacción al 100% - tamaño completo escalado
        int anchoInteraccion = nuevoAncho;
        int altoInteraccion = nuevoAlto;
        boton.setPreferredSize(new Dimension(anchoInteraccion, altoInteraccion));
        boton.setMinimumSize(new Dimension(anchoInteraccion, altoInteraccion));
        boton.setMaximumSize(new Dimension(anchoInteraccion, altoInteraccion));

        // Efectos hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setIcon(iconoSeleccionadoEscalado);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setIcon(iconoNormalEscalado);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                accionBoton(nombreBoton);
            }
        });

        // Agregar botón al panel
        panelBoton.add(boton);

        return panelBoton;
    }

    private void accionBoton(String nombreBoton) {
        System.out.println("Modo seleccionado: " + nombreBoton);
        System.exit(0); // Cerrar el programa
    }

    private void agregarLetrero(JPanel panel, String nombreBoton) {
        String rutaLetrero = "Resources/Letreros/Modo/" + nombreBoton + "_Letrero.png";
        File archivoLetrero = new File(rutaLetrero);
        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 10% de su tamaño original
            double escalaLetrero = 0.10;
            int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
            int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));
            Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);
            JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
            panel.add(labelLetrero);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Modos());
    }
}
