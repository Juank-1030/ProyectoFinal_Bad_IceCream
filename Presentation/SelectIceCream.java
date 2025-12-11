package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Clase SelectIceCream - Pantalla del modo Player vs Player
 */
public class SelectIceCream extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private String rutaBotones = "Resources/Botones/Helados/";
    private Runnable onBackClick;
    private Runnable onChocolateClick;
    private Runnable onVainillaClick;
    private Runnable onFresaClick;

    public SelectIceCream() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - PVP");
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

        // Crear botones de helados
        JPanel botonChocolate = crearBoton("Chocolate", false);
        JPanel botonVainilla = crearBoton("Vainilla", false);
        JPanel botonFresa = crearBoton("Fresa", false);

        // Panel contenedor horizontal para los tres botones centrado
        JPanel panelBotonesFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBotonesFila.setOpaque(false);
        panelBotonesFila.add(botonChocolate);
        panelBotonesFila.add(botonVainilla);
        panelBotonesFila.add(botonFresa);

        // Panel para los letreros
        JPanel panelLetresFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelLetresFila.setOpaque(false);

        // Agregar letreros
        agregarLetrero(panelLetresFila, "Chocolate");
        agregarLetrero(panelLetresFila, "Vainilla");
        agregarLetrero(panelLetresFila, "Fresa");

        // Panel superior que contiene botones
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new OverlayLayout(panelSuperior));
        panelSuperior.setOpaque(false);

        // Panel contenedor para botones con GridBagLayout interno
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setOpaque(false);

        GridBagConstraints gbcLetrerosHelados = new GridBagConstraints();
        gbcLetrerosHelados.gridx = 0;
        gbcLetrerosHelados.gridy = 0;
        gbcLetrerosHelados.anchor = GridBagConstraints.NORTH;
        gbcLetrerosHelados.fill = GridBagConstraints.HORIZONTAL;
        gbcLetrerosHelados.weightx = 1.0;
        gbcLetrerosHelados.weighty = 0.0;
        gbcLetrerosHelados.insets = new Insets(0, 0, -20, 0);
        panelContenedor.add(panelLetresFila, gbcLetrerosHelados);

        GridBagConstraints gbcBoton = new GridBagConstraints();
        gbcBoton.gridx = 0;
        gbcBoton.gridy = 1;
        gbcBoton.anchor = GridBagConstraints.NORTH;
        gbcBoton.fill = GridBagConstraints.HORIZONTAL;
        gbcBoton.weightx = 1.0;
        gbcBoton.weighty = 0.0;
        gbcBoton.insets = new Insets(-10, 0, 0, 0);
        panelContenedor.add(panelBotonesFila, gbcBoton);

        panelSuperior.add(panelContenedor);
        panelSuperior.setPreferredSize(new Dimension(800, 200));
        panelSuperior.setMaximumSize(new Dimension(800, 200));

        // Panel para centrar verticalmente y mover con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Agregar letrero "Helados" en la parte superior
        JLabel letrero = agregarLetrero();
        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.NONE;
        gbcLetrero.weightx = 0.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(5, 0, 0, 0);
        panelCentral.add(letrero, gbcLetrero);

        GridBagConstraints gbcSuperior = new GridBagConstraints();
        gbcSuperior.gridx = 0;
        gbcSuperior.gridy = 1;
        gbcSuperior.anchor = GridBagConstraints.NORTH;
        gbcSuperior.fill = GridBagConstraints.NONE;
        gbcSuperior.weightx = 0.0;
        gbcSuperior.weighty = 0.0;
        gbcSuperior.insets = new Insets(0, 0, 0, 0);
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
        gbcBack.insets = new Insets(0, 0, 10, 0);
        panelCentral.add(botonBack, gbcBack);

        panelFondo.add(panelCentral, BorderLayout.CENTER);
        add(panelFondo);

        setVisible(false); // Oculta inicialmente, se mostrará desde el controlador
    }

    private JLabel agregarLetrero() {
        String rutaLetrero = "Resources/Letreros/Modo/helados.png";
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

    private void agregarLetrero(JPanel panel, String nombreHelado) {
        String rutaLetrero = "Resources/Letreros/PVP/" + nombreHelado + ".png";
        File archivoLetrero = new File(rutaLetrero);
        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 15% de su tamaño original (más grande)
            double escalaLetrero = 0.15;
            int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
            int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));
            Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                    Image.SCALE_SMOOTH);
            ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);
            JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
            panel.add(labelLetrero);
        }
    }

    private JPanel crearBoton(String nombreHelado, boolean aumentarTamaño) {
        String rutaNormal = rutaBotones + nombreHelado + ".png";
        String rutaSeleccionado = rutaBotones + nombreHelado + "Selected.png";

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

        // Escalar botones al 18% de su tamaño original, o 23.8% si es aumentarTamaño
        // (+15%)
        double escala = aumentarTamaño ? 0.238 : 0.18;

        // Calcular tamaño basado en la imagen más grande para que todas sean iguales
        int anchoNormal = (int) (iconoNormal.getIconWidth() * escala);
        int altoNormal = (int) (iconoNormal.getIconHeight() * escala);
        int anchoSeleccionado = (int) (iconoSeleccionado.getIconWidth() * escala);
        int altoSeleccionado = (int) (iconoSeleccionado.getIconHeight() * escala);

        // Usar el máximo para que ambas imágenes tengan el mismo tamaño
        int nuevoAncho = Math.max(Math.max(anchoNormal, anchoSeleccionado), 1);
        int nuevoAlto = Math.max(Math.max(altoNormal, altoSeleccionado), 1);

        // Aplicar factor adicional para Vainilla Selected (20% más grande), Fresa y
        // Chocolate Selected (15% más grande)
        int anchoSeleccionadoFinal = nuevoAncho;
        int altoSeleccionadoFinal = nuevoAlto;
        if (nombreHelado.equals("Vainilla")) {
            anchoSeleccionadoFinal = (int) (nuevoAncho * 1.2);
            altoSeleccionadoFinal = (int) (nuevoAlto * 1.2);
        } else if (nombreHelado.equals("Fresa") || nombreHelado.equals("Chocolate")) {
            anchoSeleccionadoFinal = (int) (nuevoAncho * 1.15);
            altoSeleccionadoFinal = (int) (nuevoAlto * 1.15);
        }

        Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        Image imagenSeleccionada = iconoSeleccionado.getImage().getScaledInstance(anchoSeleccionadoFinal,
                altoSeleccionadoFinal,
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
        // Mostrar imagen completa sin recortes
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
                accionBoton(nombreHelado);
            }
        });

        // Agregar botón al panel
        panelBoton.add(boton);

        return panelBoton;
    }

    private void accionBoton(String nombreHelado) {
        // SOLO ejecutar el callback correspondiente
        // La lógica está en el Controller
        if (nombreHelado.equals("Chocolate") && onChocolateClick != null) {
            onChocolateClick.run();
        } else if (nombreHelado.equals("Vainilla") && onVainillaClick != null) {
            onVainillaClick.run();
        } else if (nombreHelado.equals("Fresa") && onFresaClick != null) {
            onFresaClick.run();
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

    public void setOnChocolateClick(Runnable callback) {
        this.onChocolateClick = callback;
    }

    public void setOnVainillaClick(Runnable callback) {
        this.onVainillaClick = callback;
    }

    public void setOnFresaClick(Runnable callback) {
        this.onFresaClick = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SelectIceCream());
    }
}
