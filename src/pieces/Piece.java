package pieces;
import table.*;

public class Piece {
    protected String pieceName;
    protected RuleSet restriction;
    protected Player playerPiece; 
    protected int coordinateX;
    protected int coordinateY;

    /**
     * Cria uma nova instancia pra definir o estado inicial da peca
     * 
     * @param pieceName     Define o nome da peca
     * @param rules         Define o ruleset aplicado pra essa peca
     * @param playerPiece   Define a que jogador a peca pertence
     * @param coordinateX   Define a coordenada x inicial da peca 
     * @param coordinateY   Define a coordenada y inicial da peca
     */
    public Piece(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY){
        this.pieceName = pieceName;
        this.restriction = rules;
        this.playerPiece = playerPiece;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public boolean moveTo(int x, int y, Board gameBoard){
        // codigos de restricao de movimento
        this.coordinateX = x;
        this.coordinateY = y;
        return true;
    }

    public void capturedPiece(){
        // declara a peca como capturada, retirando-a do tabuleiro
        this.coordinateX = -1;
        this.coordinateY = -1;
    }

    // getters da classe Piece
    public Player getPlayer() { return this.playerPiece; }
    public String getSymbol() { return this.pieceName; }
}