public class Board {
    // Matriz do tabuleiro: 10 linhas (Y) por 9 colunas (X)
    private Piece[][] board = new Piece[10][9];
    private Player player1;
    private Player player2;

    /**
     * 
     * @param player1
     * @param player2
     */

    public Board(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
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
        this.board[mainLine][0] = new Tower(player, 0, mainLine);
        this.board[mainLine][1] = new Horse(player, 1, mainLine);
        this.board[mainLine][2] = new Elephant(player, 2, mainLine);
        this.board[mainLine][3] = new Guard(player, 3, mainLine);
        this.board[mainLine][4] = new General(player, 4, mainLine);
        this.board[mainLine][5] = new Guard(player, 5, mainLine);
        this.board[mainLine][6] = new Elephant(player, 6, mainLine);
        this.board[mainLine][7] = new Horse(player, 7, mainLine);
        this.board[mainLine][8] = new Tower(player, 8, mainLine);

        // Canhoes
        this.board[cannonLine][1] = new Cannon(player, 1, cannonLine);
        this.board[cannonLine][7] = new Cannon(player, 7, cannonLine);

        // Peões
        for (int x = 0; x < 9; x += 2) {
            this.board[pawnLine][x] = new Pawn(player, x, pawnLine);
        }
    }

    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 10;
    }

    public boolean movePiece(int originX, int originY, int destinationX, int destinationY) {
        // 1. Limites do tabuleiro
        if (!isInsideBoard(originX, originY) || !isInsideBoard(destinationX, destinationY)) {
            return false;
        }

        // Nao permite mover para a mesma casa
        if (originX == destinationX && originY == destinationY) {
            return false;
        }

        Piece piece = this.board[originY][originX];
        if (piece == null) {
            return false;
        }

        Piece destinationPiece = this.board[destinationY][destinationX];

        // Não deixa comer própria peça
        if (destinationPiece != null && destinationPiece.getPlayer().getColor().equalsIgnoreCase(piece.getPlayer().getColor())) {
            return false;
        }

        // Validacao das regras de cada peça. ex.: cavalo nao pode pular as peças
        if (!piece.isValidMove(destinationX, destinationY, this)) {
            return false;
        }

        // Se tiver peça onde a peça ta indo pode comer
        if (destinationPiece != null) {
            destinationPiece.capturedPiece();
        }

        // Atualiza a matriz e a posicao interna da peca
        piece.moveTo(destinationX, destinationY);
        this.board[destinationY][destinationX] = piece;
        this.board[originY][originX] = null;

        return true;
    }

    public Piece getPiece(int x, int y) {
        if (!isInsideBoard(x, y)) {
            return null;
        }
        return this.board[y][x];
    }

    //O tabuleiro no terminal usando os simbolos das pecas (quando tu adicionar vai puxar em getSymbol @luis).
    public void printBoard() {
        System.out.println("  0  1  2  3  4  5  6  7  8 (X)"); //imprime eixo X
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
}

// @matheushuang: fiz a matriz posição para add as novas subclasses para as peças e adicionei o printBoard() para mostrar o tabuleiro no terminal.