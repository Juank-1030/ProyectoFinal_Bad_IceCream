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
    private Runnable onBackClick;
    private Runnable onPVPClick;
    private Runnable onPVMClick;
    private Runnable onMVMClick;

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
        JPanel panelLetresFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 73, 0));
        panelLetresFila.setOpaque(false);

        // Agregar letreros
        agregarLetrero(panelLetresFila, "PVP");
        agregarLetrero(panelLetresFila, "PVM");
        agregarLetrero(panelLetresFila, "MVM");

        // Panel superior que agrupa letreros y botones con tamaño fijo
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new OverlayLayout(panelSuperior));
        panelSuperior.setOpaque(false);

        // Panel contenedor para letreros y botones con GridBagLayout interno
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setOpaque(false);

        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.HORIZONTAL;
        gbcLetrero.weightx = 1.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(0, 0, 0, 0);
        panelContenedor.add(panelLetresFila, gbcLetrero);

        GridBagConstraints gbcBoton = new GridBagConstraints();
        gbcBoton.gridx = 0;
        gbcBoton.gridy = 1;
        gbcBoton.anchor = GridBagConstraints.NORTH;
        gbcBoton.fill = GridBagConstraints.HORIZONTAL;
        gbcBoton.weightx = 1.0;
        gbcBoton.weighty = 0.0;
        gbcBoton.insets = new Insets(-35, 0, 0, 0);
        panelContenedor.add(panelBotonesFila, gbcBoton);

        panelSuperior.add(panelContenedor);
        panelSuperior.setPreferredSize(new Dimension(800, 200));
        panelSuperior.setMaximumSize(new Dimension(800, 200));

        // Panel para centrar verticalmente y mover con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Agregar letrero "Modos" en la parte superior
        JLabel letreromodos = agregarLetreromodos();
        GridBagConstraints gbcLetreromodos = new GridBagConstraints();
        gbcLetreromodos.gridx = 0;
        gbcLetreromodos.gridy = 0;
        gbcLetreromodos.anchor = GridBagConstraints.NORTH;
        gbcLetreromodos.fill = GridBagConstraints.NONE;
        gbcLetreromodos.weightx = 0.0;
        gbcLetreromodos.weighty = 0.0;
        gbcLetreromodos.insets = new Insets(5, 0, 0, 0);
        panelCentral.add(letreromodos, gbcLetreromodos);

        GridBagConstraints gbcSuperior = new GridBagConstraints();
        gbcSuperior.gridx = 0;
        gbcSuperior.gridy = 1;
        gbcSuperior.anchor = GridBagConstraints.NORTH;
        gbcSuperior.fill = GridBagConstraints.NONE;
        gbcSuperior.weightx = 0.0;
        gbcSuperior.weighty = 0.0;
        gbcSuperior.insets = new Insets(15, 0, 0, 0);
        panelCentral.add(panelSuperior, gbcSuperior);

        // Agregar botón Back en la parte inferior
        JButton botonBack = crearBotonBack();
        GridBagConstraints gbcBack = new GridBagConstraints();
        gbcBack.gridx = 0;
        gbcBack.gridy = 2;
        gbcBack.anchor = GridBagConstraints.SOUTH;
        gbcBack.fill = GridBagConstraints.NONE;
        gbcBack.weightx = 0.0;
        gbcBack.weighty = 1.0;
        gbcBack.insets = new Insets(0, 0, 50, 0);
        panelCentral.add(botonBack, gbcBack);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);

        setVisible(false); // Oculta inicialmente, se mostrará desde el controlador
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
        // Zona de interacción: reducida en altura (75%), ancho completo
        int anchoInteraccion = nuevoAncho;
        int altoInteraccion = (int) (nuevoAlto * 0.75);
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
        // SOLO ejecutar el callback correspondiente
        // La lógica de QUID está en el Controller
        if (nombreBoton.equals("PVP") && onPVPClick != null) {
            onPVPClick.run();
        } else if (nombreBoton.equals("PVM") && onPVMClick != null) {
            onPVMClick.run();
        } else if (nombreBoton.equals("MVM") && onMVMClick != null) {
            onMVMClick.run();
        }
    }

    private void agregarLetrero(JPanel panel, String nombreBoton) {
        String rutaLetrero = "Resources/Letreros/Modo/" + nombreBoton + "_Letrero.png";
        File archivoLetrero = new File(rutaLetrero);
        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 11% de su tamaño original (10% más)
            double escalaLetrero = 0.11;
            int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
            int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));
            Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);
            JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
            panel.add(labelLetrero);
        }
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
            // Escalar botón back al 15%
            double escala = 0.15;
            int nuevoAncho = Math.max(1, (int) (iconoNormal.getIconWidth() * escala));
            int nuevoAlto = Math.max(1, (int) (iconoNormal.getIconHeight() * escala));

            // Zona de interacción: reducida en altura (60%), ancho completo
            int anchoInteraccion = nuevoAncho;
            int altoInteraccion = (int) (nuevoAlto * 0.60);
            boton.setPreferredSize(new Dimension(anchoInteraccion, altoInteraccion));

            Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenNormal);
            boton.setIcon(iconoEscalado);

            // Agregar efecto hover
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
                    accionBotonBack();
                }
            });
        }

        return boton;
    }

    private void accionBotonBack() {
        if (onBackClick != null) {
            onBackClick.run();
        }
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    public void setOnPVPClick(Runnable callback) {
        this.onPVPClick = callback;
    }

    public void setOnPVMClick(Runnable callback) {
        this.onPVMClick = callback;
    }

    public void setOnMVMClick(Runnable callback) {
        this.onMVMClick = callback;
    }

    private JLabel agregarLetreromodos() {
        String rutaLetrero = "Resources/Letreros/Modo/modos.png";
        File archivoLetrero = new File(rutaLetrero);

        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 12% de su tamaño original
            double escalaLetrero = 0.12;
            int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
            int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));
            Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);
            JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
            return labelLetrero;
        }

        return new JLabel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Modos());
    }
}
