package controller;

import java.util.Scanner;
import pieces.Piece;
import table.*;

public class GameManagement {
    private long startTimeMillis;
    private Player currentTurn;
    private Board gameBoard;
    private Player[] gamePlayers;
    private int[] playerRemainingSeconds;

    /**
- Cria a instancia de GameManagement que une os jogadores e o tabuleiro
- 
- 
@param board    Tabuleiro da partida
@param players  Array com os jogadores participantes
     */
    public GameManagement(Board board, Player[] players) {
        this.startTimeMillis = System.currentTimeMillis();
        this.gameBoard = board;
        this.gamePlayers = players;
        this.playerRemainingSeconds = new int[]{900, 900};

        // "Red" (Vermelho) sempre começa no Xiangqi
        for (Player player : this.gamePlayers) {
            if (player.getColor().equalsIgnoreCase("Red") || player.getColor().equalsIgnoreCase("Vermelho")) {
                this.currentTurn = player;
                break;
            }
        }
    }

    // Inicializa a partida recolhendo os nomes dos jogadores
    public static Board initializeGame(Scanner sc, RuleSet restriction) {
        System.out.println("Bem-vindo ao Xiangqi!");
        System.out.println("Os jogadores devem buscar aplicar um Xeque-Mate no General adversário (帥 para Vermelho / 將 para Preto), de modo a deixá-lo encurralado.");
        System.out.println("O jogo pode acabar quando seu tempo de jogada terminar, então pense o mais rápido possível na sua jogada!\n");

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

    private int getCurrentTurnIndex() {
        for (int i = 0; i < this.gamePlayers.length; i++) {
            if (this.gamePlayers[i].getPlayerCode() == this.currentTurn.getPlayerCode()) {
                return i;
            }
        }
        return 0;
    }

    // Verifica se o jogo acabou por tempo ou ausência de Generais
    public boolean isGameOver() {
        if (this.getTime() <= 0) {
            System.out.println("\n[Fim de Jogo] O tempo acabou!");
            System.out.println(this.currentTurn.getPlayerName() + " perdeu a partida!");
            return true;
        }

        int generalCount = 0;
        // x = Linhas (0 a 9) e y = Colunas (0 a 8)
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 9; y++) {
                Piece p = gameBoard.getPiece(x, y);
                if (p != null && p.getClass().getSimpleName().equalsIgnoreCase("General")) {
                    generalCount++;
                }
            }
        }

        if (generalCount < 2) {
            System.out.println("\n[Fim de Jogo] O General foi capturado!");
            System.out.println(currentTurn.getPlayerName() + " venceu a partida!");
            return true;
        }

        return false;
    }

    // Executa a validação do turno e delega a jogada para o tabuleiro
    public boolean executeMove(int originX, int originY, int destX, int destY) {
        int idx = getCurrentTurnIndex();
        long elapsedSeconds = (System.currentTimeMillis() - this.startTimeMillis) / 1000;
        this.playerRemainingSeconds[idx] -= (int) elapsedSeconds;
        this.startTimeMillis = System.currentTimeMillis();

        if (this.playerRemainingSeconds[idx] <= 0) {
            this.playerRemainingSeconds[idx] = 0;
            return false;
        }

        Piece piece = this.gameBoard.getPiece(originX, originY);

        // Valida se a casa de origem contém uma peça
        if (piece == null) {
            System.out.println("\n[!] Nenhuma peça encontrada na posição selecionada (" + originX + ", " + originY + "). Tente novamente.");
            return false;
        }

        // Valida se a peça pertence ao jogador da vez
        if (!piece.getPlayer().getColor().equalsIgnoreCase(this.currentTurn.getColor())) {
            System.out.println("\n[!] Esta peça pertence ao adversário! Apenas pode mover peças de " + this.currentTurn.getPlayerName() + ".");
            return false;
        }

        // Tenta executar o movimento no tabuleiro
        boolean moveSuccess = this.gameBoard.movePiece(originX, originY, destX, destY);

        if (moveSuccess) {
            this.switchTurn();
            this.resetTime();
            return true;
        } else {
            System.out.println("\n[!] Movimento inválido para a peça " + piece.getSymbol() + " de (" + originX + ", " + originY + ") para (" + destX + ", " + destY + "). Verifique as regras de movimentação!");
            return false;
        }
    }

    // Alterna a vez entre os jogadores
    public void switchTurn() {
        for (Player player : this.gamePlayers) {
            if (player.getPlayerCode() != this.currentTurn.getPlayerCode()) {
                this.currentTurn = player;
                break;
            }
        }
    }

    // Gerenciamento do temporizador (limite fixo de 900 segundos por lance)
    public int getTime() {
        int idx = getCurrentTurnIndex();
        long elapsedSeconds = (System.currentTimeMillis() - this.startTimeMillis) / 1000;
        int remaining = this.playerRemainingSeconds[idx] - (int) elapsedSeconds;
        return Math.max(0, remaining);
    }

    public void resetTime() { 
        this.startTimeMillis = System.currentTimeMillis(); 
    }

    // Getters
    public Player getTurn() { 
        return this.currentTurn; 
    }

    public Board getGameBoard() { 
        return this.gameBoard; 
    }
}