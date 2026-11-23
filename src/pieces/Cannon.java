package pieces;
import table.Board;
import table.Player;
import table.RuleSet;

/** Peça Xiangqi que se move em linha reta e salta uma tela para capturar. */
public class Cannon extends Piece {

    /** Cria um canhão associado ao jogador e às regras da partida. */
    public Cannon(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    /** Valida movimento ortogonal e a regra configurada de captura por tela. */
    public boolean moveTo(int x, int y, Board gameBoard) {
        // Linha reta ortogonal
        if (x != this.coordinateX && y != this.coordinateY) {
            return false;
        }

        int obstacles = RuleSet.countObstacles(this.coordinateX, this.coordinateY, x, y, gameBoard);

        Piece destPiece = gameBoard.getPiece(x, y);

        // Não come se não houver uma peça para "pular carnica" e não há peça de destino
        if (!this.restriction.usesCaptureRule("CANNON_REQUIRES_ONE_SCREEN")) {
            if (!this.restriction.usesMovementRule("PATH_MUST_BE_CLEAR") || obstacles == 0) {
                return super.moveTo(x, y, gameBoard);
            }
            return false;
        }

        // exatamente 2 peças no caminho: 1 que independe de lado e 1 peça de cor oposta "pula carniça"
        if (destPiece.getPlayer().getColor().equalsIgnoreCase(this.playerPiece.getColor())) {
            return false;
        }

        if (destPiece == null) {
            if (!this.restriction.usesMovementRule("PATH_MUST_BE_CLEAR") || obstacles == 0) {
                return super.moveTo(x, y, gameBoard);
            }
            return false;
        }
        if (destPiece != null && obstacles == 1) {
            return super.moveTo(x, y, gameBoard);
        }

        return false;
    }
}
