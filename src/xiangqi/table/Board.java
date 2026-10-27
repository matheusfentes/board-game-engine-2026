package xiangqi.table;
import xiangqi.pieces.*;

public class Board {
    // Matriz do tabuleiro: 10 linhas (X) por 9 colunas (Y)
    private int boardSizeLine;
    private int boardSizeCol;
    private Piece[][] board;
    private Player player1;
    private Player player2;
    private RuleSet restriction;

    /**
     * Construtor para criar uma instancia pra classe board
     * 
     * @param player1       Define o jogador 1 do jogo
     * @param player2       Define o jogador 2 do jogo
     * @param restriction   Define as regras do jogo
     */
    public Board(Player player1, Player player2, RuleSet restriction) {
        this.boardSizeLine = 10;
        this.boardSizeCol = 9;
        this.board = new Piece[this.boardSizeLine][this.boardSizeCol];
        this.player1 = player1;
        this.player2 = player2;
        this.restriction = restriction;
        initializeBoard();
    }
    
    // Posiciona as pecas dos dois lados pra começar a partida
    private void initializeBoard() {
        // Lado de cima (Black) - Linhas X = 0, 2, 3
        setSide(this.player2, 0, 2, 3);

        // Lado de baixo (Red) - Linhas X = 9, 7, 6
        setSide(this.player1, 9, 7, 6);
    }

  // Instancia as pecas de um dos lados do tabuleiro
    private void setSide(Player player, int mainLine, int cannonLine, int pawnLine) {
        // Simbologia simplificada e unificada (padrão Vermelho) para ambos os lados
        String towerSymbol    = "俥";
        String horseSymbol    = "傌";
        String elephantSymbol = "相";
        String guardSymbol    = "仕";
        String generalSymbol  = "帥";
        String cannonSymbol   = "炮";
        String pawnSymbol     = "兵";

        // Linha principal do fundo (mainLine = linha X)
        this.board[mainLine][0] = new Tower(towerSymbol, this.restriction, player, mainLine, 0);
        this.board[mainLine][1] = new Horse(horseSymbol, this.restriction, player, mainLine, 1);
        this.board[mainLine][2] = new Elephant(elephantSymbol, this.restriction, player, mainLine, 2);
        this.board[mainLine][3] = new Guard(guardSymbol, this.restriction, player, mainLine, 3);
        this.board[mainLine][4] = new General(generalSymbol, this.restriction, player, mainLine, 4);
        this.board[mainLine][5] = new Guard(guardSymbol, this.restriction, player, mainLine, 5);
        this.board[mainLine][6] = new Elephant(elephantSymbol, this.restriction, player, mainLine, 6);
        this.board[mainLine][7] = new Horse(horseSymbol, this.restriction, player, mainLine, 7);
        this.board[mainLine][8] = new Tower(towerSymbol, this.restriction, player, mainLine, 8);

        // Canhoes (cannonLine = linha X)
        this.board[cannonLine][1] = new Cannon(cannonSymbol, this.restriction, player, cannonLine, 1);
        this.board[cannonLine][7] = new Cannon(cannonSymbol, this.restriction, player, cannonLine, 7);

        // Peoes (pawnLine = linha X, colunas Y de 0 a 8 pulando de 2 em 2)
        for (int y = 0; y < this.boardSizeCol; y += 2) {
            this.board[pawnLine][y] = new Pawn(pawnSymbol, this.restriction, player, pawnLine, y);
        }
    }

    // Retorna se esta dentro ou fora dos limites do tabuleiro: X = Linha (0 a 9), Y = Coluna (0 a 8)
    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < this.boardSizeLine && y >= 0 && y < this.boardSizeCol;
    }

    // Funcao que faz verificacoes de acordo com o tabuleiro para mover a peca
    public boolean movePiece(int originX, int originY, int destinationX, int destinationY) {
        if (!isLegalMove(originX, originY, destinationX, destinationY)) {
            return false;
        }

        Piece piece = this.board[originX][originY];
        Piece destinationPiece = this.board[destinationX][destinationY];
        this.board[destinationX][destinationY] = piece;
        this.board[originX][originY] = null;
        piece.setCoordinates(destinationX, destinationY);
        if (destinationPiece != null) {
            destinationPiece.capturedPiece();
        }
        return true;
    }

    public boolean isLegalMove(int originX, int originY, int destinationX, int destinationY) {
        if (!isInsideBoard(originX, originY) || !isInsideBoard(destinationX, destinationY)
                || (originX == destinationX && originY == destinationY)) {
            // Verificações de tabuleiro
            return false;
        }
        Piece piece = this.board[originX][originY];
        Piece destinationPiece = this.board[destinationX][destinationY];
        if (piece == null || destinationPiece instanceof General) {
            // o destino nao pode ser o general e a peca nao pode ser vazia
            return false;
        }

        if (destinationPiece != null
                && destinationPiece.getPlayer().getColor().equalsIgnoreCase(piece.getPlayer().getColor())) {
            // verifica se ta capturando uma peca da mesma cor
            return false;
        }

        int originalX = piece.getCoordinateX();
        int originalY = piece.getCoordinateY();
        if (!piece.moveTo(destinationX, destinationY, this)) {
            piece.setCoordinates(originalX, originalY);
            return false;
        }

        // tabuleiro temporario pra ver se o general foi ameacado
        this.board[destinationX][destinationY] = piece;
        this.board[originX][originY] = null;
        boolean inCheck = RuleSet.isPlayerInCheck(piece.getPlayer().getColor(), this);
        boolean generalsFacing = RuleSet.areGeneralsFacingEachOther(this);

        // restauracao do tabuleiro para a setagem de cordenadas
        this.board[originX][originY] = piece;
        this.board[destinationX][destinationY] = destinationPiece;
        piece.setCoordinates(originalX, originalY);

        if (inCheck || generalsFacing) {
            return false;
        } else {
            return true;
        }
    }

    // Verifica o tabuleiro duas vezes, verifica para se existe peca ali, e verifica para se existe um destino legal pra peca
    public boolean hasAnyLegalMove(String playerColor) {
        for (int originX = 0; originX < this.boardSizeLine; originX++) {
            for (int originY = 0; originY < this.boardSizeCol; originY++) {
                Piece piece = this.board[originX][originY];
                if (piece == null || !piece.getPlayer().getColor().equalsIgnoreCase(playerColor)) {
                    // Pula se nao tiver a peca naquela posicao do tabuleiro ou for uma peca diferente
                    continue;
                }

                for (int destinationX = 0; destinationX < this.boardSizeLine; destinationX++) {
                    for (int destinationY = 0; destinationY < this.boardSizeCol; destinationY++) {
                        if (isLegalMove(originX, originY, destinationX, destinationY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // O tabuleiro no terminal: cabeçalho mostra Colunas (Y), lateral mostra Linhas (X)
    public void printBoard() {
        System.out.println("\n    a  b  c  d  e  f  g  h  i (Y)"); // imprime eixo Y (colunas)
        for (int x = 0; x < this.boardSizeLine; x++) {
            System.out.print(x + "   "); // imprime eixo X (linhas)
            for (int y = 0; y < this.boardSizeCol; y++) {
                Piece p = this.board[x][y];
                if (p == null) {
                    System.out.print(".  ");
                } else {
                    System.out.print(p.getSymbol() + " ");
                }
            }
            System.out.println();
            if (x == 4) {
                System.out.println("    -------------------------"); // imprime o rio entre linhas 4 e 5
            }
        }
        System.out.println("(X)");
    }

    public boolean hasOnlyDefensivePiecesRemaining() {
        for (int x = 0; x < this.boardSizeLine; x++) {
            for (int y = 0; y < this.boardSizeCol; y++) {
                Piece piece = this.board[x][y];
                if (piece != null
                        && !(piece instanceof General)
                        && !(piece instanceof Guard)
                        && !(piece instanceof Elephant)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getters dos atributos e getPiece da posicao escolhida (linha x, coluna y)
    public Piece getPiece(int x, int y) {
        if (!isInsideBoard(x, y)) {
            return null;
        }
        return this.board[x][y];
    }
    public Player getPlayer1(){ return this.player1; }
    public Player getPlayer2(){ return this.player2; }
    public RuleSet getRuleSet(){ return this.restriction; }
    public int getBoardSizeLine(){ return this.boardSizeLine; }
    public int getBoardSizeCol(){ return this.boardSizeCol; }
}
