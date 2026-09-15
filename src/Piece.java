public class Piece {
    private String moveType;
    private RuleSet restriction;
    private Player playerPiece; 
    private int coordinateX;
    private int coordinateY;

    /**
     * Cria uma nova instancia pra definir o estado inicial da peca
     * 
     * @param moveType      Define o tipo de movimento da peca
     * @param rules         Define o ruleset aplicado pra essa peca
     * @param playerPiece   Define a que jogador a peca pertence
     * @param coordinateX   Define a coordenada x inicial da peca 
     * @param coordinateY   Define a coordenada y inicial da peca
     */
    public Piece(String moveType, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY){
        this.moveType = moveType;
        this.restriction = rules;
        this.playerPiece = playerPiece;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public void moveTo(int x, int y){
        // codigos de restricao de movimento
        this.coordinateX = x;
        this.coordinateY = y;
    }

    public void capturedPiece(){
        // declara a peca como capturada, retirando-a do tabuleiro
        this.coordinateX = -1;
        this.coordinateY = -1;
    }
}
