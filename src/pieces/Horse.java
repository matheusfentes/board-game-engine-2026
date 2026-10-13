package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

public class Horse extends Piece {

    public Horse(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        int diffX = Math.abs(x - this.coordinateX);
        int diffY = Math.abs(y - this.coordinateY);

        // Movimento em "L": 2 casas num eixo e 1 no outro
        boolean validL = (diffX == 2 && diffY == 1) || (diffX == 1 && diffY == 2);
        if (!validL) {
            return false;
        }

        // Ponto de bloqueio ("pata do cavalo")
        int blockX = this.coordinateX;
        int blockY = this.coordinateY;

        if (diffX == 2) {
            blockX = this.coordinateX + (x - this.coordinateX) / 2;
        } else {
            blockY = this.coordinateY + (y - this.coordinateY) / 2;
        }

        if (gameBoard.getPiece(blockX, blockY) != null) {
            return false;
        }

        Piece destPiece = gameBoard.getPiece(x, y);
        if (destPiece != null && destPiece.getPlayer().getColor().equalsIgnoreCase(this.playerPiece.getColor())) {
            return false;
        }

        return super.moveTo(x, y, gameBoard);
    }
}