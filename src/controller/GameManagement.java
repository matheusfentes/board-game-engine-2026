package controller;

import java.util.Scanner;

import pieces.Piece;
import table.Board;
import table.Player;
import table.RuleSet;
import config.GameConfig;

/** Coordena turnos, relógio, jogadas e condições de fim do Xiangqi. */
public class GameManagement {
    private long startTimeMillis;
    private Player currentTurn;
    private Board gameBoard;
    private Player[] gamePlayers;
    private int[] playerRemainingSeconds;
    private GameConfig config;

    /**
     * Cria o gerenciador de uma partida e configura relógio e primeiro turno.
     * @param board tabuleiro da partida
     * @param players participantes, na ordem usada pelo gerenciamento
     * @param config configuração validada do jogo
     */
    public GameManagement(Board board, Player[] players, GameConfig config) {
        this.startTimeMillis = System.currentTimeMillis();
        this.gameBoard = board;
        this.gamePlayers = players;
        this.config = config;
        this.playerRemainingSeconds = new int[players.length];
        java.util.Arrays.fill(this.playerRemainingSeconds, config.turn.timeLimitSeconds);

        // "Red" (Vermelho) sempre começa no Xiangqi
        for (Player player : this.gamePlayers) {
            if (player.getColor().equalsIgnoreCase(config.turn.firstPlayer)) {
                this.currentTurn = player;
                break;
            }
        }
    }

    /** Solicita os nomes dos jogadores e monta o tabuleiro inicial de Xiangqi. */
    public static Board initializeGame(Scanner sc, RuleSet restriction, GameConfig config) {
        System.out.println("Bem-vindo ao Xiangqi!");
        System.out.println("Os jogadores devem buscar aplicar um Xeque-Mate no General adversário (帥), deixando-o encurralado ou eliminando-o.");
        System.out.println("O jogo pode acabar quando seu tempo de jogada terminar, então pense o mais rápido possível na sua jogada!\n");

        System.out.println("Digite o nome do jogador 1: ");
        String name1 = sc.nextLine();
        System.out.println("Digite o nome do jogador 2: ");
        String name2 = sc.nextLine();

        Player player1 = new Player(name1, 1, config.players.symbols.get(0));
        Player player2 = new Player(name2, 2, config.players.symbols.get(1));
        Board gameBoard = new Board(player1, player2, restriction, config);

        gameBoard.printBoard();

        System.out.println("\nO jogador " + name1 + " inicia, pois recebeu a cor vermelha, associada a boa sorte!\n");

        return gameBoard;
    }

    // Metodo que recebe o index do jogador atual, usado nas funcoes de gerenciamento de tempo do jogo
    /** Localiza o índice do jogador atual nos vetores de participantes e relógios. */
    private int getCurrentTurnIndex() {
        for (int i = 0; i < this.gamePlayers.length; i++) {
            if (this.gamePlayers[i].getPlayerCode() == this.currentTurn.getPlayerCode()) {
                return i;
            }
        }
        return 0;
    }

    // Verifica se o jogo acabou por tempo, xeque-mate ou falta de movimentos legais
    /** Verifica tempo, empate por material e ausência de jogadas legais. */
    public boolean isGameOver() {
        // Se o tempo terminar, game over 
        if (this.getTime() <= 0) {
            System.out.println("\n[Fim de Jogo] O tempo acabou!");
            System.out.println(this.currentTurn.getPlayerName() + " perdeu a partida!");
            return true;
        }

        // Verifica se existem apenas pecas defensivas no jogo
        if ("ONLY_DEFENSIVE_PIECES_REMAIN".equals(config.rules.drawCondition.type)
                && this.gameBoard.hasOnlyDefensivePiecesRemaining()) {
            System.out.println("\n[Fim de Jogo] Empate: restam apenas pecas defensivas, insuficientes pra um xeque-mate!");
            return true;
        }

        // Verifica se existe movimentos legais de pecas (ex. general em xeque, voce deve salva-lo)
        if ("CHECKMATE_OR_NO_LEGAL_MOVE".equals(config.rules.winCondition.type)
                && !this.gameBoard.hasAnyLegalMove(this.currentTurn.getColor())) {
            boolean checkmate = RuleSet.isCheckmate(this.currentTurn.getColor(), this.gameBoard);
            if (checkmate) {
                System.out.println("\n[Fim de Jogo] Xeque-mate!");
            } else {
                System.out.println("\n[Fim de Jogo] O jogador nao tem movimentos legais!");
            }
            System.out.println(this.currentTurn.getPlayerName() + " perdeu a partida!");
            for (Player player : this.gamePlayers) {
                if (player.getPlayerCode() != this.currentTurn.getPlayerCode()) {
                    System.out.println(player.getPlayerName() + " venceu a partida!");
                    break;
                }
            }
            return true;
        }

        return false;
    }

    // Executa a validação do turno e delega a jogada para o tabuleiro
    /** Cobra o tempo do turno e tenta executar a jogada informada. */
    public boolean executeMove(int originX, int originY, int destX, int destY) {
        // Gerenciamento do tempo por index do jogador, retirando o tempo restante
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
            System.out.println("\n[!] Nenhuma peça encontrada na posição selecionada (" + (char) ('a' + originY) + originX + "). Tente novamente.");
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
            return true;
        } else {
            System.out.println("\n[!] Movimento inválido para a peça " + piece.getSymbol() + " de (" + (char) ('a' + originY) + originX + ") para (" + (char) ('a' + destY) + destX + "). Verifique as regras de movimentação!");
            return false;
        }
    }

    // Alterna a vez entre os jogadores
    /** Alterna o turno para o outro participante. */
    public void switchTurn() {
        for (Player player : this.gamePlayers) {
            if (player.getPlayerCode() != this.currentTurn.getPlayerCode()) {
                this.currentTurn = player;
                break;
            }
        }
    }

    // Gerenciamento do temporizador (limite fixo de 900 segundos por lance)
    /** Retorna o tempo restante do jogador atual em segundos. */
    public int getTime() {
        int idx = getCurrentTurnIndex();
        long elapsedSeconds = (System.currentTimeMillis() - this.startTimeMillis) / 1000;
        int remaining = this.playerRemainingSeconds[idx] - (int) elapsedSeconds;
        return Math.max(0, remaining);
    }

    // Getters
    /** Retorna o jogador que deve fazer a próxima jogada. */
    public Player getTurn() { 
        return this.currentTurn; 
    }

    /** Retorna o tabuleiro gerenciado pela partida. */
    public Board getGameBoard() { 
        return this.gameBoard; 
    }
}
