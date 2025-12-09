package Domain;

/**
 * Pineapple (Piña) - Fruta que se mueve constantemente
 */
public class Pineapple extends Fruit {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 200;

    public Pineapple(Position position, Board board) {
        super(position, "Piña");
        this.setBehavior(new MovingFruitBehavior(board));
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    /**
     * Actualiza la referencia al tablero (necesario para persistencia)
     */
    public void updateBoardReference(Board board) {
        if (this.behavior instanceof MovingFruitBehavior) {
            ((MovingFruitBehavior) this.behavior).setBoard(board);
        }
    }
}
