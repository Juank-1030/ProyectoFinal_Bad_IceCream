package Domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Clase LevelManager - Gestor de niveles del juego
 * Responsable de cargar, gestionar y cambiar entre niveles disponibles
 */
public class LevelManager {
    private static final int TOTAL_NIVELES = 3;
    private static final String RUTA_NIVELES = "levels/";

    private Level nivelesDisponibles[];
    private Level nivelActual;
    private int nivelActualIndex;

    /**
     * Constructor del LevelManager
     * Carga los 3 niveles disponibles desde archivos serializados
     */
    public LevelManager() {
        nivelesDisponibles = new Level[TOTAL_NIVELES];
        nivelActualIndex = 0;
        cargarNiveles();
    }

    /**
     * Carga los niveles desde archivos serializados
     */
    private void cargarNiveles() {
        for (int i = 1; i <= TOTAL_NIVELES; i++) {
            try {
                nivelesDisponibles[i - 1] = cargarNivelDesdeArchivo(i);
                System.out.println("[OK] Nivel " + i + " cargado correctamente");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[ERROR] Error cargando Nivel " + i + ": " + e.getMessage());
                nivelesDisponibles[i - 1] = crearNivelPorDefecto(i);
            }
        }
    }

    /**
     * Carga un nivel específico desde archivo serializado
     * 
     * @param numeroNivel Número del nivel (1, 2 o 3)
     * @return El objeto Level cargado
     * @throws IOException            Si hay error de I/O
     * @throws ClassNotFoundException Si no encuentra la clase Level
     */
    private Level cargarNivelDesdeArchivo(int numeroNivel) throws IOException, ClassNotFoundException {
        String rutaArchivo = RUTA_NIVELES + "nivel_" + numeroNivel + ".dat";
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            throw new IOException("Archivo de nivel no existe: " + rutaArchivo);
        }

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Level) ois.readObject();
        }
    }

    /**
     * Crea un nivel por defecto en caso de no poder cargarlo desde archivo
     * 
     * @param numeroNivel Número del nivel
     * @return Un nivel básico
     */
    private Level crearNivelPorDefecto(int numeroNivel) {
        String nombreNivel = "Nivel " + numeroNivel;
        int anchoTablero = 13;
        int altoTablero = 11;

        Level nivel = new Level(numeroNivel, nombreNivel, anchoTablero, altoTablero);
        return nivel;
    }

    /**
     * Obtiene el nivel actual
     * 
     * @return El Level actual
     */
    public Level getNivelActual() {
        return nivelActual;
    }

    /**
     * Cambia al nivel especificado
     * 
     * @param numeroNivel Número del nivel (1, 2 o 3)
     * @return true si el cambio fue exitoso, false si el nivel no existe
     */
    public boolean cambiarNivel(int numeroNivel) {
        if (numeroNivel < 1 || numeroNivel > TOTAL_NIVELES) {
            System.err.println("[ERROR] Número de nivel inválido: " + numeroNivel);
            return false;
        }

        nivelActual = nivelesDisponibles[numeroNivel - 1];
        nivelActualIndex = numeroNivel - 1;
        System.out.println("[INFO] Cambio a Nivel " + numeroNivel);
        return true;
    }

    /**
     * Obtiene un nivel específico por su número
     * 
     * @param numeroNivel Número del nivel (1, 2 o 3)
     * @return El Level solicitado, o null si no existe
     */
    public Level obtenerNivel(int numeroNivel) {
        if (numeroNivel < 1 || numeroNivel > TOTAL_NIVELES) {
            return null;
        }
        return nivelesDisponibles[numeroNivel - 1];
    }

    /**
     * Obtiene todos los niveles disponibles
     * 
     * @return Array con todos los niveles
     */
    public Level[] getNivelesDisponibles() {
        return nivelesDisponibles;
    }

    /**
     * Obtiene el índice del nivel actual (0-indexado)
     * 
     * @return Índice del nivel actual
     */
    public int getNivelActualIndex() {
        return nivelActualIndex;
    }

    /**
     * Obtiene el número del nivel actual (1-indexado)
     * 
     * @return Número del nivel actual (1, 2 o 3)
     */
    public int getNumerNivelActual() {
        return nivelActualIndex + 1;
    }

    /**
     * Obtiene la cantidad total de niveles disponibles
     * 
     * @return Total de niveles (3)
     */
    public static int getTotalNiveles() {
        return TOTAL_NIVELES;
    }

    /**
     * Obtiene la ruta base de los archivos de niveles
     * 
     * @return Ruta de la carpeta de niveles
     */
    public static String getRutaNiveles() {
        return RUTA_NIVELES;
    }
}
