public class Board {
    // board do jogo: 10 linhas por 9 colunas
    private Piece[][] board = new Piece[10][9];
    private Player player1;
    private Player player2;

/**
 * 
 * @param player1 Define o primeiro jogador
 * @param player2
 */

    public Board(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        initializeBoard();
    }

    // Coloca todas as peças no lugar pra começar a partida
    private void initializeBoard() {
        // Lado de cima (Preto)
        setSide(player2, 0, 2, 3);

        // Lado de baixo (Vermelho)
        setSide(player1, 9, 7, 6);
    }

    // Cria e arruma as peças de um dos lados do board
    private void setSide(Player player, int mainLine, int cannonLine, int pawnLine) {
        // Ordem das peças na primeira linha
        String[] pieceName = {
            "Tower", "Horse", "Elephant", "Guard", "General",
            "Guard", "Elephant", "Horse", "Tower"
        };

        // Instancia a fileira do fundo
        for (int x = 0; x < 9; x++) {
            makePositionPiece(pieceName[x], player, x, mainLine);
        }

        // Os dois canhões
        makePositionPiece("Cannon", player, 1, cannonLine);
        makePositionPiece("Cannon", player, 7, cannonLine);

        // Os 5 peões intercalados
        for (int x = 0; x < 9; x += 2) {
            makePositionPiece("Pawn", player, x, pawnLine);
        }
    }

    // Checa onde a peça tá começa, cria as rules e põe no board
    private void makePositionPiece(String type, Player player, int x, int y) {
        // Checa se tá na região do rio (linhas 4 e 5)
        boolean inRiver = (y == 4 || y == 5);

        // Checa se tá na área do palácio
        boolean inPalace = (x >= 3 && x <= 5) && (y <= 2 || y >= 7);

        RuleSet rules = new RuleSet(false, inRiver, inPalace);
        Piece piece = new Piece(type, rules, player, x, y);

        this.board[y][x] = piece;
    }

    // Passa a peça de uma coordenada pra outra
    public void movePiece(int originX, int originY, int destinationX, int destinationY) {
        Piece piece = board[originY][originX];

        // Se não tem peça na casinha onde apertou, não faz nada
        if (piece == null) return;

        // Se já tinha peça no destination, come
        Piece pieceDestination = board[destinationY][destinationX];
        if (pieceDestination != null) {
            pieceDestination.capturedPiece();
        }

        // Atualiza a posição da peça e limpa a casa antiga
        piece.moveTo(destinationX, destinationY);
        board[destinationY][destinationX] = piece;
        board[originY][originX] = null;
    }

    // Pega a peça em uma posição específica
    public Piece getpiece(int x, int y) {
        return board[y][x];
    }
}