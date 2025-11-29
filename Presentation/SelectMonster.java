package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase SelectMonster - Pantalla de selección de monstruo en modo PVP
 * El jugador 2 selecciona qué monstruo desea controlar
 */
public class SelectMonster extends JFrame {
    private String rutaFondo = "Resources/Opciones_Menu/Fondo.png";
    private String rutaBotones = "Resources/Botones/Monstruos/"; // Carpeta de botones de monstruos
    private Runnable onBackClick;
    private Map<String, Runnable> monstruoCallbacks = new HashMap<>(); // Callbacks por monstruo

    public SelectMonster() {
        inicializarVentana();
    }

    private void inicializarVentana() {
        setTitle("Bad Ice Cream - Seleccionar Monstruo");
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

        // Cargar botones dinámicamente desde la carpeta
        List<String> monstruos = cargarMonstruosDisponibles();

        // Panel contenedor horizontal para los botones centrado y alineados
        // verticalmente
        JPanel panelBotonesFila = new JPanel(new FlowLayout(FlowLayout.CENTER, 68, 50)) {
            @Override
            public void doLayout() {
                super.doLayout();
                // Encontrar la altura máxima de todos los componentes
                int maxHeight = 0;
                for (java.awt.Component comp : this.getComponents()) {
                    if (comp.getHeight() > maxHeight) {
                        maxHeight = comp.getHeight();
                    }
                }
                // Alinear todos los componentes a la misma altura (vertical center)
                for (java.awt.Component comp : this.getComponents()) {
                    comp.setLocation(comp.getX(),
                            (maxHeight - comp.getHeight()) / 2 + ((this.getHeight() - maxHeight) / 2));
                }
            }
        };
        panelBotonesFila.setOpaque(false);

        // Crear botón para cada monstruo encontrado
        for (String monstruo : monstruos) {
            JPanel boton = crearBoton(monstruo, false);
            panelBotonesFila.add(boton);
        }

        // Panel para los letreros
        JPanel panelLetresFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)); // 12px de espaciado (reducido 10%)
        panelLetresFila.setOpaque(false);

        // Crear letrero para cada monstruo
        for (String monstruo : monstruos) {
            JLabel letrero = crearLetrero(monstruo);
            panelLetresFila.add(letrero);
        }

        // Panel wrapper para los letreros con margen
        JPanel panelLetrerosWrapper = new JPanel();
        panelLetrerosWrapper.setLayout(new BorderLayout());
        panelLetrerosWrapper.setOpaque(false);
        panelLetrerosWrapper.add(panelLetresFila, BorderLayout.WEST);
        panelLetrerosWrapper.setBorder(new javax.swing.border.EmptyBorder(0, 10, 0, 0)); // 10px margen más a la
                                                                                         // izquierda

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new OverlayLayout(panelSuperior)); // Cambiar a OverlayLayout
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
        gbcLetrerosHelados.insets = new Insets(0, 0, -100, 0); // -100px para mover mucho más arriba
        panelContenedor.add(panelLetrerosWrapper, gbcLetrerosHelados);

        GridBagConstraints gbcBoton = new GridBagConstraints();
        gbcBoton.gridx = 0;
        gbcBoton.gridy = 1;
        gbcBoton.anchor = GridBagConstraints.NORTH;
        gbcBoton.fill = GridBagConstraints.HORIZONTAL;
        gbcBoton.weightx = 1.0;
        gbcBoton.weighty = 0.0;
        gbcBoton.insets = new Insets(-15, 0, 0, 0); // Aumentar de -5 a -15 para reducir espacio
        panelContenedor.add(panelBotonesFila, gbcBoton);

        panelSuperior.add(panelContenedor);
        panelSuperior.setPreferredSize(new Dimension(800, 200));
        panelSuperior.setMaximumSize(new Dimension(800, 200));

        // Panel para centrar verticalmente y mover con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        // Agregar letrero "Monstruos" en la parte superior
        JLabel letrero = agregarLetrero();
        GridBagConstraints gbcLetrero = new GridBagConstraints();
        gbcLetrero.gridx = 0;
        gbcLetrero.gridy = 0;
        gbcLetrero.anchor = GridBagConstraints.NORTH;
        gbcLetrero.fill = GridBagConstraints.NONE;
        gbcLetrero.weightx = 0.0;
        gbcLetrero.weighty = 0.0;
        gbcLetrero.insets = new Insets(-5, 0, 0, 0); // Movido más arriba (de 5 a -5)
        panelCentral.add(letrero, gbcLetrero);

        GridBagConstraints gbcSuperior = new GridBagConstraints();
        gbcSuperior.gridx = 0;
        gbcSuperior.gridy = 1;
        gbcSuperior.anchor = GridBagConstraints.NORTH;
        gbcSuperior.fill = GridBagConstraints.NONE;
        gbcSuperior.weightx = 0.0;
        gbcSuperior.weighty = 0.0;
        gbcSuperior.insets = new Insets(0, 0, 0, 0); // Sin inset adicional
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

    /**
     * Carga dinámicamente todos los monstruos disponibles en
     * Resources/Botones/Monstruos/
     */
    private List<String> cargarMonstruosDisponibles() {
        List<String> monstruos = new ArrayList<>();
        File carpetaBotones = new File(rutaBotones);

        if (!carpetaBotones.exists()) {
            System.out.println("⚠️ Carpeta de botones no encontrada: " + rutaBotones);
            return monstruos;
        }

        // Buscar todos los archivos Selected.gif (cada uno debe tener un Normal.png)
        File[] archivos = carpetaBotones.listFiles((d, n) -> n.endsWith("Selected.gif"));

        if (archivos != null) {
            for (File archivo : archivos) {
                // Extraer nombre del monstruo (ej: "Narval Selected.gif" → "Narval")
                String nombre = archivo.getName()
                        .replace("Selected.gif", "")
                        .trim();

                // Verificar si existe el archivo Normal.png
                String rutaNormal = rutaBotones + nombre + "Normal.png";
                if (!new File(rutaNormal).exists()) {
                    System.out.println("⚠️ No se encontró: " + rutaNormal);
                    continue;
                }

                monstruos.add(nombre);
            }
        }

        return monstruos;
    }

    private JLabel agregarLetrero() {
        String rutaLetrero = "Resources/Letreros/Modo/Monstruos.png";
        File archivoLetrero = new File(rutaLetrero);

        if (archivoLetrero.exists()) {
            ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);
            // Escalar letrero al 15% de su tamaño original
            double escalaLetrero = 0.15;
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

    /**
     * Crea un letrero para un monstruo específico
     * Busca el archivo en Resources/Letreros/Monstruos/[NombreMonstruo].png
     */
    private JLabel crearLetrero(String nombreMonstruo) {
        // Mapear nombres de monstruos a nombres de archivos
        Map<String, String> mapeoArchivos = new HashMap<>();
        mapeoArchivos.put("Narval", "Narval.png");
        mapeoArchivos.put("Pot", "Pot.png");
        mapeoArchivos.put("Trol", "Troll.png");
        mapeoArchivos.put("YellowSquid", "Squid.png");

        String nombreArchivo = mapeoArchivos.get(nombreMonstruo);
        if (nombreArchivo == null) {
            // Fallback: usar el nombre del monstruo + .png
            nombreArchivo = nombreMonstruo + ".png";
        }

        String rutaLetrero = "Resources/Letreros/Monstruos/" + nombreArchivo;
        File archivoLetrero = new File(rutaLetrero);

        if (!archivoLetrero.exists()) {
            System.out.println("⚠️ Letrero no encontrado: " + rutaLetrero);
            JLabel labelVacio = new JLabel();
            labelVacio.setPreferredSize(new Dimension(100, 50));
            return labelVacio;
        }

        ImageIcon iconoLetrero = new ImageIcon(rutaLetrero);

        // Escalar letreros al 14% de su tamaño original (más pequeño para caber todos)
        double escalaLetrero = 0.14;
        int anchoLetrero = Math.max(1, (int) (iconoLetrero.getIconWidth() * escalaLetrero));
        int altoLetrero = Math.max(1, (int) (iconoLetrero.getIconHeight() * escalaLetrero));

        Image imagenLetrero = iconoLetrero.getImage().getScaledInstance(anchoLetrero, altoLetrero,
                Image.SCALE_SMOOTH);
        ImageIcon iconoLetrerEscalado = new ImageIcon(imagenLetrero);

        JLabel labelLetrero = new JLabel(iconoLetrerEscalado);
        labelLetrero.setPreferredSize(new Dimension(anchoLetrero + 10, altoLetrero + 10));
        labelLetrero.setHorizontalAlignment(JLabel.CENTER);
        labelLetrero.setVerticalAlignment(JLabel.CENTER);

        return labelLetrero;
    }

    private JPanel crearBoton(String nombreMonstruo, boolean aumentarTamaño) {
        String rutaNormal = rutaBotones + nombreMonstruo + "Normal.png";
        String rutaSeleccionado = rutaBotones + nombreMonstruo + "Selected.gif";

        File archivoNormal = new File(rutaNormal);
        File archivoSeleccionado = new File(rutaSeleccionado);

        if (!archivoNormal.exists() || !archivoSeleccionado.exists()) {
            System.err.println("Error: No se encontró " + (archivoNormal.exists() ? rutaSeleccionado : rutaNormal));
            JPanel panelVacio = new JPanel();
            panelVacio.setOpaque(false);
            return panelVacio;
        }

        ImageIcon iconoNormal = new ImageIcon(rutaNormal);
        ImageIcon iconoSeleccionado = new ImageIcon(rutaSeleccionado);

        // Escalar botones al 131.25% (262.5% / 2 = 131.25%) - 50% más pequeño
        double escala = 1.3125;

        // Para PNG: escalar usando getScaledInstance
        int anchoOriginal = iconoNormal.getIconWidth();
        int altoOriginal = iconoNormal.getIconHeight();

        int nuevoAncho = Math.max(1, (int) (anchoOriginal * escala));
        int nuevoAlto = Math.max(1, (int) (altoOriginal * escala));

        // Escalar PNG normal
        Image imagenNormal = iconoNormal.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        ImageIcon iconoNormalEscalado = new ImageIcon(imagenNormal);

        // GIF/Seleccionado: NO escalar para preservar animación
        // Cargarlo sin escalar permite que Java Swing anime el GIF correctamente
        // Pero vamos a crear una versión escalada del GIF para hacerlo más grande
        Image imagenGifEscalada = iconoSeleccionado.getImage().getScaledInstance(
                (int) (iconoSeleccionado.getIconWidth() * 1.2),
                (int) (iconoSeleccionado.getIconHeight() * 1.2),
                Image.SCALE_DEFAULT);
        ImageIcon iconoSeleccionadoSinEscalar = new ImageIcon(imagenGifEscalada);

        // Crear botón con imagen
        JButton boton = new JButton(iconoNormalEscalado);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setHorizontalAlignment(JButton.CENTER);
        boton.setVerticalAlignment(JButton.CENTER);

        // Mostrar imagen completa sin recortes
        int anchoInteraccion = nuevoAncho;
        int altoInteraccion = nuevoAlto;
        int altoPanel = nuevoAlto;
        int margenSuperior = 0;

        // Si es Narval o Trol, bajar con margen superior
        if (nombreMonstruo.equalsIgnoreCase("Narval")) {
            altoPanel = nuevoAlto + 40; // Aumentar panel para dar espacio
            margenSuperior = 40; // Margen para empujar botón hacia abajo
        } else if (nombreMonstruo.equalsIgnoreCase("Trol")) {
            altoPanel = nuevoAlto + 55; // Más espacio para Trol
            margenSuperior = 55; // Margen mayor para Trol
        }

        boton.setPreferredSize(new Dimension(anchoInteraccion, altoInteraccion));
        boton.setMinimumSize(new Dimension(anchoInteraccion, altoInteraccion));
        boton.setMaximumSize(new Dimension(anchoInteraccion, altoInteraccion));

        // Panel para el botón
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);

        panelBoton.setPreferredSize(new Dimension(anchoInteraccion, altoPanel));
        panelBoton.setMinimumSize(new Dimension(anchoInteraccion, altoPanel));
        panelBoton.setMaximumSize(new Dimension(anchoInteraccion, altoPanel));

        // Si es Narval o Trol, usar BoxLayout con espacio arriba
        if (nombreMonstruo.equalsIgnoreCase("Narval") || nombreMonstruo.equalsIgnoreCase("Trol")) {
            panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.Y_AXIS));
            panelBoton.add(Box.createVerticalStrut(margenSuperior)); // Espacio fijo arriba
            panelBoton.add(boton);
            panelBoton.add(Box.createVerticalGlue()); // Espacio flexible abajo
        } else {
            panelBoton.setLayout(new BorderLayout());
            panelBoton.add(boton, BorderLayout.NORTH);
        }

        // Efectos hover - Cambiar a imagen seleccionada (GIF animado)
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Mostrar GIF animado sin escalar para que funcione la animación
                boton.setIcon(iconoSeleccionadoSinEscalar);
                boton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Volver a imagen normal escalada
                boton.setIcon(iconoNormalEscalado);
                boton.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                accionBoton(nombreMonstruo);
            }
        });

        return panelBoton;
    }

    private void accionBoton(String nombreMonstruo) {
        // Ejecutar el callback registrado para este monstruo
        if (monstruoCallbacks.containsKey(nombreMonstruo)) {
            monstruoCallbacks.get(nombreMonstruo).run();
        } else {
            System.out.println("⚠️ No hay callback registrado para: " + nombreMonstruo);
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

    /**
     * Registrar callback para un monstruo específico
     */
    public void setOnMonstruoClick(String nombreMonstruo, Runnable callback) {
        monstruoCallbacks.put(nombreMonstruo, callback);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SelectMonster());
    }
}
