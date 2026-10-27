package xiangqi.pieces;
import xiangqi.table.Board;
import xiangqi.table.Player;
import xiangqi.table.RuleSet;

public class General extends Piece {

    public General(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        int diffX = Math.abs(x - this.coordinateX);
        int diffY = Math.abs(y - this.coordinateY);

        // Movimento ortogonal de exatamente 1 casa
        boolean oneStep = (diffX == 1 && diffY == 0) || (diffX == 0 && diffY == 1);
        if (!oneStep) {
            return false;
        }

        // Validação do Palácio: Y = Coluna ('d' a 'f'), X = Linha (Red: 7 a 9, Black: 0 a 2)
        if (y < 3 || y > 5) {
            return false;
        }

        String color = this.playerPiece.getColor();
        boolean isRed = color.equalsIgnoreCase("Red") ;

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