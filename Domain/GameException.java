package Domain;

/**
 * Excepción personalizada para errores específicos del juego
 * Maneja errores relacionados con la lógica del dominio
 */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Código de error para categorización
     */
    public enum ErrorCode {
        INVALID_POSITION("Posición inválida"),
        INVALID_MOVEMENT("Movimiento inválido"),
        ENTITY_NOT_FOUND("Entidad no encontrada"),
        LEVEL_LOAD_ERROR("Error al cargar nivel"),
        BOARD_INITIALIZATION_ERROR("Error al inicializar tablero"),
        SAVE_ERROR("Error al guardar partida"),
        LOAD_ERROR("Error al cargar partida"),
        CONFIGURATION_ERROR("Error de configuración"),
        GAMEPLAY_ERROR("Error durante el juego");

        private final String description;

        ErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private ErrorCode errorCode;

    /**
     * Constructor con mensaje
     */
    public GameException(String message) {
        super(message);
        this.errorCode = ErrorCode.GAMEPLAY_ERROR;
    }

    /**
     * Constructor con mensaje y código de error
     */
    public GameException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor con mensaje y causa
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GAMEPLAY_ERROR;
    }

    /**
     * Constructor con mensaje, código de error y causa
     */
    public GameException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Obtiene el código de error
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Obtiene un mensaje detallado del error
     */
    public String getDetailedMessage() {
        return String.format("[%s] %s: %s", 
            errorCode.name(), 
            errorCode.getDescription(), 
            getMessage());
    }

    @Override
    public String toString() {
        return getDetailedMessage();
    }
}
