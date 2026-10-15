package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

public class Guard extends Piece {

    public Guard(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        int diffX = Math.abs(x - this.coordinateX);
        int diffY = Math.abs(y - this.coordinateY);

        // Diagonal de exatamente 1 casa
        if (diffX != 1 || diffY != 1) {
            return false;
        }

        // Coluna Y restrita ao Palácio ('d' a 'f')
        if (y < 3 || y > 5) {
            return false;
        }

        String color = this.playerPiece.getColor();
        boolean isRed = color.equalsIgnoreCase("Red");

        // Linha X restrita ao Palácio
        if (isRed) {
            if (x < 7 || x > 9) return false;
        } else {
            if (x < 0 || x > 2) return false;
        }

        Piece destPiece = gameBoard.getPiece(x, y);
        if (destPiece != null && destPiece.getPlayer().getColor().equalsIgnoreCase(color)) {
            return false;
        }

        return super.moveTo(x, y, gameBoard);
    }
}