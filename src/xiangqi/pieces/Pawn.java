package xiangqi.pieces;
import xiangqi.table.Board;
import xiangqi.table.Player;
import xiangqi.table.RuleSet;

public class Pawn extends Piece {

    public Pawn (String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY){
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override
    public boolean moveTo(int x, int y, Board gameBoard) {
        // recebe a regra e cor do peao pra saber a validade e direcao da sua jogada
        String color = this.playerPiece.getColor();
        boolean crossedRiver = RuleSet.hasCrossedRiver(this.coordinateX, color);

        // operador ternario pra verificar qual a direcao da peca
        boolean forward = (color.equalsIgnoreCase("Red")) ?
            (y == this.coordinateY && x == this.coordinateX - 1) : 
            (y == this.coordinateY && x == this.coordinateX + 1);
            
        boolean sides = (x == this.coordinateX && (y == this.coordinateY + 1 || y == this.coordinateY - 1)); 
        
        // verifica se os operadores ternarios indicam a validade da jogada e se ele ja cruzou o rio
        if (forward || (crossedRiver && sides)){
            return super.moveTo(x, y, gameBoard);
        }

        // retorna false se der algo errado
        return false;
    }
}