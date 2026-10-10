public class GameManagement {
    private long startTimeMillis;
    private Player currentTurn;
    private Board gameBoard;
    private Player[] gamePlayers;

    /**
     * 
     * @param board
     * @param players
     */

    //Cria a instancia de GameManagement que une player e board (escolhe quem começa também)
    public GameManagement(Board board, Player[] players) {
        this.startTimeMillis = System.currentTimeMillis();
        this.gameBoard = board;
        this.gamePlayers = players;

        // "Red" (Vermelho) sempre começa no xiangqi
        for (Player player : this.gamePlayers) {
            if (player.getColor().equalsIgnoreCase("Red")) {
                this.currentTurn = player;
                break;
            }
        }
    }

    public boolean isGameOver() {
    // Pra dar o checkmate (um dos generais é capturado)
    int generalCount = 0;
    for (int y = 0; y < 10; y++) {
        for (int x = 0; x < 9; x++) {
            Piece p = gameBoard.getPiece(x, y);
            if (p != null && p.getClass().getSimpleName().equalsIgnoreCase("General")) {
                generalCount++;
            }
        }
    }
    return generalCount < 2;
}

    public boolean executeMove(int originX, int originY, int destX, int destY) {
        Piece piece = this.gameBoard.getPiece(originX, originY);

        // Verifica se existe peca onde ta "apertando/clicando"
        if (piece == null) {
            return false;
        }

        // Confere sea peca pertence ao jogador
        if (!piece.getPlayer().getColor().equalsIgnoreCase(this.currentTurn.getColor())) {
            return false;
        }

        // Tenta executar o movimento
        boolean moveSuccess = this.gameBoard.movePiece(originX, originY, destX, destY);

        // termina a vez caso depois da jogada
        if (moveSuccess) {
            switchTurn();
            return true;
        }

        return false;
    }

    public void switchTurn() {
        for (Player player : this.gamePlayers) {
            if (player.getPlayerCode() != this.currentTurn.getPlayerCode()) {
                this.currentTurn = player;
                break;
            }
        }
    }

    //Retorna os segundos
    public int getTime() {
        long elapsedMillis = System.currentTimeMillis() - this.startTimeMillis;
        return (int) (elapsedMillis / 1000);
    }

    public void resetTime() {
        this.startTimeMillis = System.currentTimeMillis();
    }

    public Player getTurn() {
        return this.currentTurn;
    }

    public Board getGameBoard() {
        return this.gameBoard;
    }

    public Player endgame() {
        return this.currentTurn;
    }
}

// @matheushuang: Foi adicionado a contagem de tempo com System.currentTimeMillis, a lógica de validação de jogada e troca de turno, e a condição de término de partida (isGameOver) quando um dos Generais for de arrasta.