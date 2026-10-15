package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

public class Elephant extends Piece {

    public Elephant(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        int diffX = Math.abs(x - this.coordinateX);
        int diffY = Math.abs(y - this.coordinateY);

        // Anda exatamente 2 casas em diagonal
        if (diffX != 2 || diffY != 2) {
            return false;
        }

        String color = this.playerPiece.getColor();
        boolean isRed = color.equalsIgnoreCase("Red");

        // Não pode cruzar o rio (X = Linha)
        if (isRed && x < 5) {
            return false;
        }
        if (!isRed && x > 4) {
            return false;
        }

        // Ponto central bloqueado "olho do elefante"
        int eyeX = (this.coordinateX + x) / 2;
        int eyeY = (this.coordinateY + y) / 2;
        if (gameBoard.getPiece(eyeX, eyeY) != null) {
            return false;
        }

        Piece destPiece = gameBoard.getPiece(x, y);
        if (destPiece != null && destPiece.getPlayer().getColor().equalsIgnoreCase(color)) {
            return false;
        }

        return super.moveTo(x, y, gameBoard);
    }
}