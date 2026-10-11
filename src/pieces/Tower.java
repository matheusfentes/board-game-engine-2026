package pieces;
import table.*;


public class Tower extends Piece {
    public Tower(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY){
        super(pieceName, rules, playerPiece, coordinateX, coordinateY);
    }

    @Override 
    public boolean moveTo(int x, int y, Board gameBoard){
        // verificacao se vai mover verticalmente ou horizontalmente
        boolean isValidMove = (x==this.coordinateX && y!=this.coordinateY)||(x!=this.coordinateX&&y==this.coordinateY);
        if(isValidMove){
            // verifica a direcao do step (1, 0, -1)
            int stepX = Integer.compare(x, this.coordinateX);
            int stepY = Integer.compare(y, this.coordinateY);

            // recebe o x e y atuais
            int currentX = this.coordinateX + stepX;
            int currentY = this.coordinateY + stepY;

            // verifica se ha alguma peca impedindo a passagem para o destino xy
            while(currentX != x || currentY != y){
                if (gameBoard.getPiece(currentX, currentY)!=null){
                    return false;
                }
                currentX += stepX;
                currentY += stepY;
            }

            // se tudo ocorreu com sucesso ele retorna o moveto que troca a posicao e retorna true
            return super.moveTo(x, y, gameBoard);
        }
        return false;
    }
}
