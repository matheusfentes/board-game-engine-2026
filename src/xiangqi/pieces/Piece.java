package xiangqi.pieces;

import xiangqi.table.Board;
import xiangqi.table.Player;
import xiangqi.table.RuleSet;

public class Piece {
    protected String pieceName;
    protected RuleSet restriction;
    protected Player playerPiece; 
    protected int coordinateX;
    protected int coordinateY;

    // Cores ANSI para o terminal
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[1;31m";       // Vermelho vivo
    public static final String ANSI_BLACK_SIDE = "\u001B[97m";  // Branco para contraste no terminal

    /**
     * Cria uma nova instancia pra definir o estado inicial da peca
     * 
     * @param pieceName     Define o nome da peca
     * @param rules         Define o ruleset aplicado pra essa peca
     * @param playerPiece   Define a que jogador a peca pertence
     * @param coordinateX   Define a coordenada x inicial da peca 
     * @param coordinateY   Define a coordenada y inicial da peca
     */
    public Piece(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        this.pieceName = pieceName;
        this.restriction = rules;
        this.playerPiece = playerPiece;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public boolean moveTo(int x, int y, Board gameBoard) {
        this.coordinateX = x;
        this.coordinateY = y;
        return true;
    }

    public void capturedPiece() {
        this.coordinateX = -1;
        this.coordinateY = -1;
    }

    public void setCoordinates(int x, int y) {
        this.coordinateX = x;
        this.coordinateY = y;
    }

    // Getters
    public int getCoordinateX() { return this.coordinateX; }

    public int getCoordinateY() { return this.coordinateY; }

    public Player getPlayer() { return this.playerPiece; }

    public RuleSet getRestriction() { return this.restriction; }
    
    public String getSymbol() { 
        if (this.playerPiece == null) return this.pieceName;
        String color = this.playerPiece.getColor();
        if ("Red".equalsIgnoreCase(color)) return ANSI_RED + this.pieceName + ANSI_RESET;
        if ("Black".equalsIgnoreCase(color)) return ANSI_BLACK_SIDE + this.pieceName + ANSI_RESET;
        return this.pieceName;
    }

}
