package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

public class Cannon extends Piece {

    public Cannon(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        // Linha reta ortogonal
        if (x != this.coordinateX && y != this.coordinateY) {
            return false;
        }

        int stepX = Integer.compare(x, this.coordinateX);
        int stepY = Integer.compare(y, this.coordinateY);

        int curX = this.coordinateX + stepX;
        int curY = this.coordinateY + stepY;
        int obstacles = 0;

        while (curX != x || curY != y) {
            if (gameBoard.getPiece(curX, curY) != null) {
                obstacles++;
            }
            curX += stepX;
            curY += stepY;
        }

        Piece destPiece = gameBoard.getPiece(x, y);

        // Movimento sem captura: 0 obstáculos e casa vazia
        if (destPiece == null) {
            if (obstacles == 0) {
                return super.moveTo(x, y, gameBoard);
            }
            return false;
        }

        // Captura: exatamente 1 plataforma/obstáculo e peça de cor oposta
        if (destPiece.getPlayer().getColor().equalsIgnoreCase(this.playerPiece.getColor())) {
            return false;
        }

        if (obstacles == 1) {
            return super.moveTo(x, y, gameBoard);
        }

        return false;
    }
}