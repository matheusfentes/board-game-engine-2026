package table;
import pieces.*;

public class Board {
    // Matriz do tabuleiro: 10 linhas (X) por 9 colunas (Y)
    private Piece[][] board = new Piece[10][9];
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
        this.player1 = player1;
        this.player2 = player2;
        this.restriction = restriction;
        initializeBoard();
    }
    
    // Posiciona as pecas dos dois lados pra começar a partida
    private void initializeBoard() {
        // Lado de cima (Black) - Linhas X = 0, 2, 3
        setSide(player2, 0, 2, 3);

        // Lado de baixo (Red) - Linhas X = 9, 7, 6
        setSide(player1, 9, 7, 6);
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
        for (int y = 0; y < 9; y += 2) {
            this.board[pawnLine][y] = new Pawn(pawnSymbol, this.restriction, player, pawnLine, y);
        }
    }

    // Retorna se esta dentro ou fora dos limites do tabuleiro: X = Linha (0 a 9), Y = Coluna (0 a 8)
    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 10 && y >= 0 && y < 9;
    }

    // Funcao que faz verificacoes de acordo com o tabuleiro para mover a peca
    public boolean movePiece(int originX, int originY, int destinationX, int destinationY) {
        // Limites do tabuleiro
        if (!isInsideBoard(originX, originY) || !isInsideBoard(destinationX, destinationY)) {
            System.out.println("Fora dos limites do tabuleiro...");
            return false;
        }

        // Nao permite mover para a mesma casa
        if (originX == destinationX && originY == destinationY) {
            System.out.println("Voce escolheu a mesma casa!");
            return false;
        }

        Piece piece = this.board[originX][originY];
        if (piece == null) {
            return false;
        }

        // Não deixa comer a própria peça
        Piece destinationPiece = this.board[destinationX][destinationY];
        if (destinationPiece != null && destinationPiece.getPlayer().getColor().equalsIgnoreCase(piece.getPlayer().getColor())) {
            System.out.println("Nao pode comer a propria peca!");
            return false;
        }

        // Tenta mover a peca de acordo com a regra geometrica dela
        if (piece.moveTo(destinationX, destinationY, this)) {
            // Simula o movimento no tabuleiro para validar se infringe as regras de Xeque e Flying General
            this.board[destinationX][destinationY] = piece;
            this.board[originX][originY] = null;

            boolean inCheck = RuleSet.isPlayerInCheck(piece.getPlayer().getColor(), this);
            boolean flyingGeneral = RuleSet.areGeneralsFacingEachOther(this);

            if (inCheck || flyingGeneral) {
                // Reverte o tabuleiro e a peca caso o movimento resulte em auto-xeque ou enfrente o outro General
                this.board[originX][originY] = piece;
                this.board[destinationX][destinationY] = destinationPiece;
                piece.moveTo(originX, originY, this);

                if (flyingGeneral) {
                    System.out.println("Movimento invalido: Generais nao podem se encarar diretamente (Flying General)!");
                } else {
                    System.out.println("Movimento invalido: o seu General ficaria sob Xeque!");
                }
                return false;
            }

            // Se tiver peça onde a peça está indo, pode comer
            if (destinationPiece != null) {
                destinationPiece.capturedPiece();
            }

            return true;
        }
        return false;
    }

    // O tabuleiro no terminal: cabeçalho mostra Colunas (Y), lateral mostra Linhas (X)
    public void printBoard() {
        System.out.println("\n(Y) 0  1  2  3  4  5  6  7  8"); // imprime eixo Y (colunas)
        for (int x = 0; x < 10; x++) {
            System.out.print(x + "   "); // imprime eixo X (linhas)
            for (int y = 0; y < 9; y++) {
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
    }

    public void printBoard(Player currentTurn) {
        System.out.println("\n(Y) 0  1  2  3  4  5  6  7  8   [Vez de: " + currentTurn.getPlayerName() + " - " + currentTurn.getColor() + "]");
        for (int x = 0; x < 10; x++) {
            System.out.print(x + "   ");
            for (int y = 0; y < 9; y++) {
                Piece p = this.board[x][y];
                if (p == null) {
                    System.out.print(".  ");
                } else {
                    System.out.print(p.getSymbol() + " ");
                }
            }
            System.out.println();
            if (x == 4) {
                System.out.println("    -------------------------");
            }
        }
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
}