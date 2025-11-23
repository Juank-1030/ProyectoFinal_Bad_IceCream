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

        // Panel para los botones de modos - izquierda y mitad inferior
        JPanel panelModos = new JPanel(new GridBagLayout());
        panelModos.setOpaque(false);

        // Crear botones
        JPanel botonPVP = crearBoton("PVP", "Player vs Player", true); // true = aumentar 15%
        JPanel botonPVM = crearBoton("PVM", "Player vs Machine", false);
        JPanel botonMVM = crearBoton("MVM", "Machine vs Machine", false);

        // Panel contenedor horizontal para los tres botones
        JPanel panelBotonesFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelBotonesFila.setOpaque(false);
        panelBotonesFila.add(botonPVP);
        panelBotonesFila.add(botonPVM);
        panelBotonesFila.add(botonMVM);

        // Panel espaciador a la izquierda
        JPanel panelEspaciador = new JPanel();
        panelEspaciador.setOpaque(false);
        panelEspaciador.setPreferredSize(new Dimension(160, 0)); // 10% más a la izquierda

        // Configurar GridBagConstraints para posicionar en la mitad del JFrame
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1; // Segunda fila (mitad inferior)
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 1.0; // Ocupar espacio vertical

        // Panel contenedor para espaciador y botones
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setOpaque(false);
        panelContenedor.add(panelEspaciador, BorderLayout.WEST);
        panelContenedor.add(panelBotonesFila, BorderLayout.CENTER);

        panelModos.add(panelContenedor, gbc);

        panelFondo.add(panelModos, BorderLayout.CENTER);
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

        // Escalar botones al 18% de su tamaño original, o 20.7% si es PVP (+15%)
        double escala = aumentarTamaño ? 0.207 : 0.18; // 0.18 * 1.15 = 0.207
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

        // Crear etiqueta de texto
        JLabel labelTexto = new JLabel(textoLabel);
        labelTexto.setForeground(Color.BLACK);
        labelTexto.setFont(new Font("Arial", Font.BOLD, 14));
        labelTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // Agregar botón y etiqueta al panel
        panelBoton.add(boton);
        panelBoton.add(Box.createVerticalStrut(5));
        panelBoton.add(labelTexto);

        return panelBoton;
    }

    private void accionBoton(String nombreBoton) {
        System.out.println("Modo seleccionado: " + nombreBoton);
        System.exit(0); // Cerrar el programa
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Modos());
    }
}
