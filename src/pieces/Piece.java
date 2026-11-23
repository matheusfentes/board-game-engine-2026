package pieces;

import table.Board;
import table.Player;
import table.RuleSet;

/** Representa uma peça genérica, seu dono e sua posição no tabuleiro. */
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
    /** Cria uma peça com símbolo, regras, jogador e coordenadas iniciais. */
    public Piece(String pieceName, RuleSet rules, Player playerPiece, int coordinateX, int coordinateY) {
        this.pieceName = pieceName;
        this.restriction = rules;
        this.playerPiece = playerPiece;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    /** Atualiza as coordenadas; subclasses aplicam as regras próprias de movimento. */
    public boolean moveTo(int x, int y, Board gameBoard) {
        this.coordinateX = x;
        this.coordinateY = y;
        return true;
    }

    /** Marca a peça como removida do tabuleiro. */
    public void capturedPiece() {
        this.coordinateX = -1;
        this.coordinateY = -1;
    }

    /** Define a posição atual da peça. */
    public void setCoordinates(int x, int y) {
        this.coordinateX = x;
        this.coordinateY = y;
    }

    // Getters
    /** Retorna a linha atual da peça. */
    public int getCoordinateX() { return this.coordinateX; }

    /** Retorna a coluna atual da peça. */
    public int getCoordinateY() { return this.coordinateY; }

    /** Retorna o jogador dono da peça. */
    public Player getPlayer() { return this.playerPiece; }

    /** Retorna o conjunto compartilhado de regras do jogo. */
    public RuleSet getRestriction() { return this.restriction; }
    
    /** Retorna o símbolo da peça com a cor ANSI correspondente ao jogador. */
    public String getSymbol() { 
        if (this.playerPiece == null) return this.pieceName;
        String color = this.playerPiece.getColor();
        if ("Red".equalsIgnoreCase(color) || "X".equalsIgnoreCase(color)) return ANSI_RED + this.pieceName + ANSI_RESET;
        if ("Black".equalsIgnoreCase(color) || "O".equalsIgnoreCase(color)) return ANSI_BLACK_SIDE + this.pieceName + ANSI_RESET;
        return this.pieceName;
    }

}
