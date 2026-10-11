package table;
import pieces.*;

public class Board {
    // Matriz do tabuleiro: 10 linhas (Y) por 9 colunas (X)
    private Piece[][] board = new Piece[10][9];
    private Player player1;
    private Player player2;
    private RuleSet restriction;

    /**
     * Construtor para criar uma de instancia pra classe board
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
        // Lado de cima (Black)
        setSide(player2, 0, 2, 3);

        // Lado de baixo (Red)
        setSide(player1, 9, 7, 6);
    }

    // Instancia as pecas de um dos lados do tabuleiro
    private void setSide(Player player, int mainLine, int cannonLine, int pawnLine) {
        // Linha principal do fundo
        this.board[mainLine][0] = new Tower("俥",this.restriction, player, 0, mainLine);
        this.board[mainLine][1] = new Horse("馬",this.restriction, player, 1, mainLine);
        this.board[mainLine][2] = new Elephant("象", this.restriction, player, 2, mainLine);
        this.board[mainLine][3] = new Guard("士", this.restriction, player, 3, mainLine);
        this.board[mainLine][4] = new General("帥", this.restriction, player, 4, mainLine);
        this.board[mainLine][5] = new Guard("士", this.restriction, player, 5, mainLine);
        this.board[mainLine][6] = new Elephant("象", this.restriction, player, 6, mainLine);
        this.board[mainLine][7] = new Horse("馬", this.restriction, player, 7, mainLine);
        this.board[mainLine][8] = new Tower("俥", this.restriction, player, 8, mainLine);

        // Canhoes
        this.board[cannonLine][1] = new Cannon("砲", this.restriction, player, 1, cannonLine);
        this.board[cannonLine][7] = new Cannon("砲", this.restriction, player, 7, cannonLine);

        // Peoes
        for (int x = 0; x < 9; x += 2) {
            this.board[pawnLine][x] = new Pawn("兵", this.restriction, player, x, pawnLine);
        }
    }

    // retorna se esta dentro ou fora dos limites do tabuleiro
    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 10;
    }

    // funcao que faz verificacoes de acordo com o tabuleiro para mover a peca
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

        // acho q essa verificacao nao precisa, visto q ja foi feita no metodo move do gamemanagement
        Piece piece = this.board[originY][originX];
        if (piece == null) {
            return false;
        }

        // Não deixa comer própria peça
        Piece destinationPiece = this.board[destinationY][destinationX];
        if (destinationPiece != null && destinationPiece.getPlayer().getColor().equalsIgnoreCase(piece.getPlayer().getColor())) {
            System.out.println("Nao pode comer a propria peca!");
            return false;
        }

        if(piece.moveTo(destinationX, destinationY, this)){
            // Se tiver peça onde a peça ta indo pode comer
            if (destinationPiece != null) {
                destinationPiece.capturedPiece();
            }

            // Atualiza a matriz e a posicao interna da peca
            this.board[destinationY][destinationX] = piece;
            this.board[originY][originX] = null;

            return true;
        }
        return false;
    }

    
    //O tabuleiro no terminal usando os simbolos das pecas (quando tu adicionar vai puxar em getSymbol @luis).
    public void printBoard() {
        System.out.println("\n  0  1  2  3  4  5  6  7  8"); //imprime eixo X
        for (int y = 0; y < 10; y++) {
            System.out.print(y + " "); //imprime eixo Y
            for (int x = 0; x < 9; x++) {
                Piece p = this.board[y][x];
                if (p == null) {
                    System.out.print(".  "); //imprime casa vazia
                } else {
                    System.out.print(p.getSymbol() + " "); //imprime cada peca
                }
            }
            System.out.println();
            if (y == 4) {
                System.out.println("  -------------------------"); //imprime o rio
            }
        }
    }
    
    // getters dos atributos e o getpiece da posicao escolhida
    public Piece getPiece(int x, int y) {
        if (!isInsideBoard(x, y)) {
            return null;
        }
        return this.board[y][x];
    }
    public Player getPlayer1(){ return this.player1; }
    public Player getPlayer2(){ return this.player2; }
    public RuleSet getRuleSet(){ return this.restriction; }
}