package Domain;


/**
 * Cherry (Cereza) - Fruta que se teletransporta aleatoriamente
 */
public class Cherry extends Fruit {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 50;

    public Cherry(Position position, Board board) {
        super(position, "Cereza");
        this.setBehavior(new TeleportFruitBehavior(board));
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    /**
     * Actualiza la referencia al tablero (necesario para persistencia)
     */
    public void updateBoardReference(Board board) {
        if (this.behavior instanceof TeleportFruitBehavior) {
            ((TeleportFruitBehavior) this.behavior).setBoard(board);
        }
    }
}
