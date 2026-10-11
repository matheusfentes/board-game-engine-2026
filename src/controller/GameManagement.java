package controller;
import java.util.Scanner;
import pieces.Piece;
import table.*;

public class GameManagement {
    private long startTimeMillis;
    private Player currentTurn;
    private Board gameBoard;
    private Player[] gamePlayers;
    private boolean endgame;

    /**
     * Cria a instancia de GameManagement que une player e board (escolhe quem começa também)
     * @param board
     * @param players
     */
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

    // metodo estatico que inicializa a partida 
    public static Board initializeGame(Scanner sc, RuleSet restriction){

        // mensagem de boas vindas e mini tutorial
        System.out.println("Bem-vindo ao Xiangqi!");
        System.out.println("Os jogadores devem buscar aplicar um Xeque-Mate no General/Rei(帥) adversário, de modo que o deixe encurralado pelas jogadas seguintes do jogador atacante.");
        System.out.println("O jogo pode acabar quando seu tempo de jogada terminar, entao pense o mais rapido possivel na sua jogada!\n");

        // recebe o nome dos jogadores
        System.out.println("Digite o nome do jogador 1: ");
        String name1 = sc.nextLine();
        System.out.println("Digite o nome do jogador 2: ");
        String name2 = sc.nextLine();

        Player player1 = new Player(name1, 1, "Red");
        Player player2 = new Player(name2, 2, "Black");
        Board gameBoard = new Board(player1, player2, restriction);

        gameBoard.printBoard();

        System.out.println("\nO jogador " + name1 + " inicia, visto que recebeu a cor vermelha, popularmente associada a boa sorte na cultura chinesa!\n");

        return gameBoard;
    }

    // funcao que verifica se o jogo acabou ou nao
    public boolean isGameOver() {
        // tempo acaba
        if(this.getTime() <= 0){
            System.out.println("\n[Fim de Jogo] O tempo acabou!");
            System.out.println(this.currentTurn.getPlayerName() + " perdeu a partida!");
            return true;
        }

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

        if (generalCount < 2){
            System.out.println(currentTurn.getPlayerName() + " venceu a partida!");
        }
        return generalCount < 2;
    }

    // funcao que executa a funcao de mover a peca, fazendo a validacao das entradas e passando para validar no tabuleiro e na peca em suas classes
    public boolean executeMove(int originX, int originY, int destX, int destY) {
        Piece piece = this.gameBoard.getPiece(originX, originY);

        // Verifica se existe peca onde ta "apertando/clicando"
        if (piece == null) {
            System.out.println(this.currentTurn.getPlayerName() + " nao escolheu uma peca valida, tente novamente.");
            return false;
        }

        // Confere se a peca pertence ao jogador
        if (!piece.getPlayer().getColor().equalsIgnoreCase(this.currentTurn.getColor())) {
            System.out.println(this.currentTurn.getPlayerName() + " nao escolheu sua propria peca, tente novamente.");
            return false;
        }

        // Tenta executar o movimento
        boolean moveSuccess = this.gameBoard.movePiece(originX, originY, destX, destY);

        // termina a vez caso depois da jogada
        if (moveSuccess) {
            // verifica se ainda deu tempo de terminar a jogada
            if (this.getTime()>0){ this.resetTime(); }
            this.switchTurn();
            return true;
        }

        return false;
    }

    // mudanca de turno de jogadores
    public void switchTurn() {
        for (Player player : this.gamePlayers) {
            if (player.getPlayerCode() != this.currentTurn.getPlayerCode()) {
                this.currentTurn = player;
                break;
            }
        }
    }

    // funcoes de gerenciamento do tempo da partida, com um tempo fixo de 120 segundos
    public int getTime() {
        long elapsedMillis = System.currentTimeMillis() - this.startTimeMillis;
        return (int) (120 - (elapsedMillis / 1000));
    }
    public void resetTime() { this.startTimeMillis = System.currentTimeMillis(); }

    // getters do GameManagement
    public Player getTurn() { return this.currentTurn; }
    public Board getGameBoard() { return this.gameBoard; }
    public boolean endgame() {
        // nao sei se isso deve continuar
        return this.endgame;
    }
}