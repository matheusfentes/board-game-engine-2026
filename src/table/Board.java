package table;
import pieces.*;
import config.GameConfig;

/** Tabuleiro compartilhado: usa grade para Xiangqi e nós conectados para Trilha. */
public class Board {
    // Matriz do tabuleiro: 10 linhas (X) por 9 colunas (Y)
    private int boardSizeLine;
    private int boardSizeCol;
    private Piece[][] board;
    private Player player1;
    private Player player2;
    private RuleSet restriction;
    private GameConfig config;

    /**
     * Construtor para criar uma instancia pra classe board
     * 
     * @param player1       Define o jogador 1 do jogo
     * @param player2       Define o jogador 2 do jogo
     * @param restriction   Define as regras do jogo
     */
    /** Cria o tabuleiro a partir dos jogadores, regras e dimensões configuradas. */
    public Board(Player player1, Player player2, RuleSet restriction, GameConfig config) {
        this.config = config;
        this.boardSizeLine = config.board.rows;
        this.boardSizeCol = config.board.columns;
        this.board = new Piece[this.boardSizeLine][this.boardSizeCol];
        this.player1 = player1;
        this.player2 = player2;
        this.restriction = restriction;
        initializeBoard();
    }

    /** Coloca no tabuleiro as peças iniciais declaradas para Xiangqi. */
    private void initializeBoard() {
        if ("Trilha".equalsIgnoreCase(config.game)) return;
        for (GameConfig.PlacementConfig placement : config.initialPosition) {
            Player owner = placement.player.equalsIgnoreCase(player1.getColor()) ? player1 : player2;
            GameConfig.PieceConfig type = config.pieces.stream()
                .filter(p -> p.name.equalsIgnoreCase(placement.piece)).findFirst().orElseThrow();
            Piece piece = createPiece(type, owner, placement.row, placement.column);
            board[placement.row][placement.column] = piece;
        }
    }

    /** Cria a implementação de peça adequada aos dados da configuração. */
    private Piece createPiece(GameConfig.PieceConfig p, Player owner, int row, int column) {
        return switch (p.name.toLowerCase(java.util.Locale.ROOT)) {
            case "piece" -> new Piece(owner.getColor(), restriction, owner, row, column);
            case "tower" -> new Tower(p.symbol, restriction, owner, row, column);
            case "horse" -> new Horse(p.symbol, restriction, owner, row, column);
            case "elephant" -> new Elephant(p.symbol, restriction, owner, row, column);
            case "guard" -> new Guard(p.symbol, restriction, owner, row, column);
            case "general" -> new General(p.symbol, restriction, owner, row, column);
            case "cannon" -> new Cannon(p.symbol, restriction, owner, row, column);
            case "pawn" -> new Pawn(p.symbol, restriction, owner, row, column);
            default -> throw new IllegalArgumentException("Peça Xiangqi não suportada: " + p.name);
        };
    }
    // Retorna se esta dentro ou fora dos limites do tabuleiro: X = Linha (0 a 9), Y = Coluna (0 a 8)
    /** Informa se linha e coluna estão dentro dos limites retangulares do tabuleiro. */
    public boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < this.boardSizeLine && y >= 0 && y < this.boardSizeCol;
    }

    // Funcao que faz verificacoes de acordo com o tabuleiro para mover a peca
    /** Move uma peça se a jogada passar pelas regras do jogo atual. */
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

    /** Valida limites, ocupação e as regras de movimento aplicáveis. */
    public boolean isLegalMove(int originX, int originY, int destinationX, int destinationY) {
        if (!isInsideBoard(originX, originY) || !isInsideBoard(destinationX, destinationY)
                || (originX == destinationX && originY == destinationY)) {
            // VerificaÃ§Ãµes de tabuleiro
            return false;
        }
        Piece piece = this.board[originX][originY];
        Piece destinationPiece = this.board[destinationX][destinationY];
        if ("Trilha".equalsIgnoreCase(config.game)) {
            if (piece == null || destinationPiece != null || !restriction.isTrilhaNode(destinationX, destinationY)) return false;
            boolean adjacent = restriction.usesMovementRule("ADJACENT")
                    && restriction.areTrilhaNodesAdjacent(originX, originY, destinationX, destinationY);
            boolean mayFly = restriction.usesMovementRule("FLY_WITH_3_PIECES")
                    && countPieces(piece.getPlayer()) <= config.rules.winCondition.quantity;
            return adjacent || mayFly;
        }
        if (piece == null || destinationPiece instanceof General) {
            // o destino nao pode ser o general e a peca nao pode ser vazia
            return false;
        }

        if (restriction.usesCaptureRule("OPPONENT_ONLY") && destinationPiece != null
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
        boolean enforceGeneralSafety = restriction.usesMovementRule("GENERAL_MUST_STAY_SAFE");
        boolean inCheck = enforceGeneralSafety && RuleSet.isPlayerInCheck(piece.getPlayer().getColor(), this);
        boolean generalsFacing = enforceGeneralSafety && RuleSet.areGeneralsFacingEachOther(this);

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
    /** Verifica se o jogador Xiangqi possui ao menos uma jogada legal. */
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

    // O tabuleiro no terminal: cabeÃ§alho mostra Colunas (Y), lateral mostra Linhas (X)
    /** Imprime o tabuleiro no formato correspondente ao jogo configurado. */
    public void printBoard() {
        if ("Trilha".equalsIgnoreCase(config.game)) {
            printTrilhaBoard();
            return;
        }
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

    /** Imprime os 24 pontos válidos do tabuleiro de Trilha. */
    private void printTrilhaBoard() {
        System.out.println("\nTabuleiro da Trilha (linha a-g, coluna 1-7)");
        System.out.print("    ");
        for (int col = 0; col < boardSizeCol; col++) System.out.print((col + 1) + "   ");
        System.out.println();
        for (int row = 0; row < boardSizeLine; row++) {
            System.out.print((char) ('a' + row) + "   ");
            for (int col = 0; col < boardSizeCol; col++) {
                if (!restriction.isTrilhaNode(row, col)) System.out.print("    ");
                else {
                    Piece piece = board[row][col];
                    System.out.print((piece == null ? "+" : piece.getSymbol()) + "   ");
                }
            }
            System.out.println();
        }
    }

    /** Coloca uma peça de Trilha em um nó livre pertencente ao tabuleiro. */
    public boolean placeTrilhaPiece(Player player, int row, int column) {
        if (!"Trilha".equalsIgnoreCase(config.game) || player == null
                || !restriction.isTrilhaNode(row, column) || board[row][column] != null) return false;
        board[row][column] = new Piece(player.getColor(), restriction, player, row, column);
        return true;
    }

    /** Remove e retorna a peça presente na coordenada, se houver. */
    public Piece removePiece(int row, int column) {
        if (!isInsideBoard(row, column)) return null;
        Piece removed = board[row][column];
        board[row][column] = null;
        if (removed != null) removed.capturedPiece();
        return removed;
    }

    /** Conta quantas peças do participante permanecem no tabuleiro. */
    public int countPieces(Player player) {
        int count = 0;
        for (Piece[] row : board) for (Piece piece : row)
            if (piece != null && piece.getPlayer().getPlayerCode() == player.getPlayerCode()) count++;
        return count;
    }

    /** Verifica se o participante da Trilha tem ao menos um destino legal. */
    public boolean hasAnyTrilhaLegalMove(Player player) {
        for (int row = 0; row < boardSizeLine; row++) for (int col = 0; col < boardSizeCol; col++) {
            Piece piece = board[row][col];
            if (piece == null || piece.getPlayer().getPlayerCode() != player.getPlayerCode()) continue;
            for (int destRow = 0; destRow < boardSizeLine; destRow++) for (int destCol = 0; destCol < boardSizeCol; destCol++)
                if (isLegalMove(row, col, destRow, destCol)) return true;
        }
        return false;
    }

    /** Verifica a condição de empate Xiangqi por permanência de peças defensivas. */
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
    /** Retorna a peça na linha e coluna ou {@code null} se a casa estiver vazia/fora. */
    public Piece getPiece(int x, int y) {
        if (!isInsideBoard(x, y)) {
            return null;
        }
        return this.board[x][y];
    }
    /** Retorna o primeiro participante associado ao tabuleiro. */
    public Player getPlayer1(){ return this.player1; }
    /** Retorna o segundo participante associado ao tabuleiro. */
    public Player getPlayer2(){ return this.player2; }
    /** Retorna o conjunto de regras compartilhado pelo tabuleiro e suas peças. */
    public RuleSet getRuleSet(){ return this.restriction; }
    /** Retorna o número de linhas da matriz do tabuleiro. */
    public int getBoardSizeLine(){ return this.boardSizeLine; }
    /** Retorna o número de colunas da matriz do tabuleiro. */
    public int getBoardSizeCol(){ return this.boardSizeCol; }
}
