package pieces;
import table.*;
public class Horse extends Piece {
    public Horse(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY){
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }
    
    @Override 
    public boolean moveTo(int x, int y, Board gameBoard){
        // verifica a diferenca absoluta da distancia pra verificar se o movimento foi correto
        int diffX = Math.abs(x - this.coordinateX);
        int diffY = Math.abs(y - this.coordinateY);
        boolean isValidMove = (diffX == 1 && diffY == 2) || (diffX == 2 && diffY == 1);

        // pivot eh a posicao a frente do cavalo, onde podem bloquea-lo
        int pivotY = this.coordinateY;
        int pivotX = this.coordinateX; 

        if (isValidMove){
            if (diffY == 2) {
                // Se moveu 2 em Y, o obstáculo está no Y intermediário
                pivotY = this.coordinateY + (y - this.coordinateY) / 2;
            } else {
                // Se moveu 2 em X, o obstáculo está no X intermediário
                pivotX = this.coordinateX + (x - this.coordinateX) / 2;
            }
        }
        
        // verifica se tem um obstaculo no seu movimento inicial (frente ou lado)
        if (gameBoard.getPiece(pivotX, pivotY) == null){
            return super.moveTo(x, y, gameBoard);
        }
        return false;
    }
}
