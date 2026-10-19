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

        int obstacles = RuleSet.countObstacles(
            this.coordinateX, this.coordinateY, x, y, gameBoard);

        Piece destPiece = gameBoard.getPiece(x, y);

        // Não come se não houver uma peça para "pular carnica" e não há peça de destino
        if (destPiece == null) {
            if (obstacles == 0) {
                return super.moveTo(x, y, gameBoard);
            }
            return false;
        }

        // exatamente 2 peças no caminho: 1 que independe de lado e 1 peça de cor oposta "pula carniça"
        if (destPiece.getPlayer().getColor().equalsIgnoreCase(this.playerPiece.getColor())) {
            return false;
        }

        if (obstacles == 1) {
            return super.moveTo(x, y, gameBoard);
        }

        return false;
    }
}