package Domain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar recursos de niveles desde archivos binarios
 * Permite cargar configuraciones de niveles guardadas en archivos .dat
 */
public class RecursosNivel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String LEVELS_DIRECTORY = "levels/";

    /**
     * Guarda un nivel en un archivo binario (PRIVADO - solo para inicialización)
     */
    private static void guardarNivel(Level level, String filename) throws GameException {
        File directory = new File(LEVELS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filepath = LEVELS_DIRECTORY + filename + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(level);
            System.out.println("Nivel guardado exitosamente en: " + filepath);
        } catch (IOException e) {
            throw new GameException("Error al guardar nivel: " + filename,
                    GameException.ErrorCode.SAVE_ERROR, e);
        }
    }

    /**
     * Carga un nivel desde un archivo binario (PRIVADO - solo para
     * cargarNivelPorNumero)
     */
    private static Level cargarNivel(String filename) throws GameException {
        String filepath = LEVELS_DIRECTORY + filename + ".dat";
        File file = new File(filepath);

        if (!file.exists()) {
            throw new GameException("Archivo de nivel no encontrado: " + filepath,
                    GameException.ErrorCode.LEVEL_LOAD_ERROR);
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            Level level = (Level) ois.readObject();
            System.out.println("Nivel cargado exitosamente desde: " + filepath);
            return level;
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException("Error al cargar nivel: " + filename,
                    GameException.ErrorCode.LOAD_ERROR, e);
        }
    }

    /**
     * Lista todos los niveles disponibles en el directorio
     * 
     * @return Lista de nombres de archivos de niveles (sin extensión)
     */
    public static List<String> listarNivelesDisponibles() {
        List<String> niveles = new ArrayList<>();
        File directory = new File(LEVELS_DIRECTORY);

        if (!directory.exists() || !directory.isDirectory()) {
            return niveles;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                String nombre = file.getName().replace(".dat", "");
                niveles.add(nombre);
            }
        }

        return niveles;
    }

    /**
     * Verifica si existe un archivo de nivel
     * 
     * @param filename Nombre del archivo (sin extensión)
     * @return true si el archivo existe
     */
    public static boolean existeNivel(String filename) {
        String filepath = LEVELS_DIRECTORY + filename + ".dat";
        return new File(filepath).exists();
    }

    /**
     * Carga un nivel por número (1, 2, 3, etc.)
     * Primero intenta cargar desde archivo, si no existe usa el predefinido
     * 
     * @param levelNumber Número del nivel
     * @return El nivel cargado
     * @throws GameException Si hay error
     */
    public static Level cargarNivelPorNumero(int levelNumber) throws GameException {
        String filename = "nivel_" + levelNumber;

        // Intentar cargar desde archivo
        if (existeNivel(filename)) {
            try {
                return cargarNivel(filename);
            } catch (GameException e) {
                System.err.println("Error al cargar nivel desde archivo, usando predefinido: " + e.getMessage());
            }
        }

        // Si no existe archivo, usar nivel predefinido
        Level level;
        switch (levelNumber) {
            case 1:
                level = Level.createLevel1();
                break;
            case 2:
                level = Level.createLevel2();
                break;
            case 3:
                level = Level.createLevel3();
                break;
            default:
                throw new GameException("Nivel no válido: " + levelNumber,
                        GameException.ErrorCode.LEVEL_LOAD_ERROR);
        }

        return level;
    }

    /**
     * Crea los archivos de niveles predefinidos
     * Útil para inicializar el juego por primera vez
     */
    public static void crearNivelesPredefinidos() {
        try {
            guardarNivel(Level.createLevel1(), "nivel_1");
            guardarNivel(Level.createLevel2(), "nivel_2");
            guardarNivel(Level.createLevel3(), "nivel_3");
            System.out.println("Niveles predefinidos creados exitosamente");
        } catch (GameException e) {
            System.err.println("Error al crear niveles predefinidos: " + e.getMessage());
        }
    }
}
