package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

public class Tower extends Piece {

    public Tower(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        // Move em linha reta: ou mesma linha X, ou mesma coluna Y
        if (x != this.coordinateX && y != this.coordinateY) {
            return false;
        }

        // Obstáculos pelo caminho
        if (RuleSet.countObstacles(
                this.coordinateX, this.coordinateY, x, y, gameBoard) != 0) {
            return false;
        }

        Piece destPiece = gameBoard.getPiece(x, y);
        if (destPiece != null && destPiece.getPlayer().getColor().equalsIgnoreCase(this.playerPiece.getColor())) {
            return false;
        }

        return super.moveTo(x, y, gameBoard);
    }
}