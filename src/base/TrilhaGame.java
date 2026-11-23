package base;

import java.util.Scanner;
import config.GameConfig;
import pieces.Piece;
import table.Board;
import table.Player;
import table.RuleSet;

/** Executa a Trilha usando as estruturas compartilhadas Board, Player e RuleSet. */
public class TrilhaGame implements Game {
    private final GameConfig config;

    /** Cria uma partida de Trilha a partir da configuração validada. */
    public TrilhaGame(GameConfig config) { this.config = config; }

    /** Executa as fases de colocação, movimento, captura e encerramento. */
    @Override
    public void start(Scanner scanner) {
        System.out.println("Bem-vindo ao jogo de Trilha!");
        Player playerX = new Player(readName(scanner, "X"), 1, config.players.symbols.get(0));
        Player playerO = new Player(readName(scanner, "O"), 2, config.players.symbols.get(1));
        Player[] players = { playerX, playerO };
        RuleSet rules = new RuleSet(config);
        Board board = new Board(playerX, playerO, rules, config);
        int[] placed = new int[players.length];
        int current = config.turn.firstPlayer.equalsIgnoreCase(playerX.getColor()) ? 0 : 1;
        int noCaptureTurns = 0;

        while (true) {
            Player active = players[current];
            Player opponent = players[1 - current];
            boolean placing = placed[current] < config.pieces.get(0).quantity;
            board.printBoard();
            System.out.println("Turno de " + active.getPlayerName() + " (" + active.getColor() + ")");

            if (!placing && board.countPieces(active) < config.rules.winCondition.quantity) {
                announceWinner(opponent);
                break;
            }
            if (!placing && config.rules.additionalWinCondition != null
                    && "OPPONENT_HAS_NO_LEGAL_MOVE".equals(config.rules.additionalWinCondition.type)
                    && !board.hasAnyTrilhaLegalMove(active)) {
                announceWinner(opponent);
                break;
            }

            boolean madeMill;
            if (placing) {
                System.out.println("Peças restantes para colocar: " + (config.pieces.get(0).quantity - placed[current]));
                int[] destination = readPosition(scanner, "Escolha um ponto vazio (ex.: a1): ");
                if (!board.placeTrilhaPiece(active, destination[0], destination[1])) {
                    System.out.println("Ponto inválido ou ocupado. Tente novamente.");
                    continue;
                }
                placed[current]++;
                madeMill = rules.formsTrilhaMill(board, destination[0], destination[1], active.getColor());
            } else {
                int[] origin = readPosition(scanner, "Escolha a peça de origem (ex.: a1): ");
                int[] destination = readPosition(scanner, "Escolha o ponto de destino (ex.: a4): ");
                if (!board.movePiece(origin[0], origin[1], destination[0], destination[1])) {
                    System.out.println("Movimento inválido. Escolha uma peça sua e um ponto vazio adjacente"
                            + (board.countPieces(active) <= config.rules.winCondition.quantity ? " ou qualquer ponto vazio se puder voar" : "") + ".");
                    continue;
                }
                madeMill = rules.formsTrilhaMill(board, destination[0], destination[1], active.getColor());
            }

            boolean captured = false;
            if (madeMill && rules.usesCaptureRule("AFTER_MILL") && board.countPieces(opponent) > 0) {
                System.out.println("Você formou uma trilha! Remova uma peça de " + opponent.getPlayerName() + ".");
                captured = captureOpponent(scanner, board, rules, opponent);
            }

            if (config.rules.drawCondition != null
                    && "NO_CAPTURE_FOR_TURNS_WITH_3_PIECES".equals(config.rules.drawCondition.type)
                    && board.countPieces(playerX) == config.rules.winCondition.quantity
                    && board.countPieces(playerO) == config.rules.winCondition.quantity
                    && !captured) {
                noCaptureTurns++;
                if (noCaptureTurns >= config.rules.drawCondition.turns) {
                    board.printBoard();
                    System.out.println("Empate: não houve captura durante " + noCaptureTurns + " turnos com três peças.");
                    break;
                }
            } else {
                noCaptureTurns = 0;
            }
            current = 1 - current;
        }
    }

    /** Solicita e remove uma peça adversária respeitando a proteção das trilhas. */
    private boolean captureOpponent(Scanner scanner, Board board, RuleSet rules, Player opponent) {
        while (true) {
            int[] targetPosition = readPosition(scanner, "Ponto da peça que deseja remover (ex.: d4): ");
            Piece target = board.getPiece(targetPosition[0], targetPosition[1]);
            if (target == null || target.getPlayer().getPlayerCode() != opponent.getPlayerCode()) {
                System.out.println("Escolha uma peça do adversário.");
                continue;
            }
            boolean protectedPiece = rules.usesCaptureRule("PROTECTED_UNLESS_ALL_OPPONENT_PIECES_IN_MILLS")
                    && rules.isTrilhaPieceProtected(board, target, opponent.getColor())
                    && !rules.allTrilhaPiecesInMills(board, opponent.getColor());
            if (protectedPiece) {
                System.out.println("Essa peça está protegida por outra trilha. Escolha uma peça fora de uma trilha.");
                continue;
            }
            board.removePiece(targetPosition[0], targetPosition[1]);
            return true;
        }
    }

    /** Lê o nome do participante e fornece um nome padrão se a entrada estiver vazia. */
    private String readName(Scanner scanner, String symbol) {
        System.out.print("Nome do jogador " + symbol + ": ");
        String name = scanner.nextLine().trim();
        return name.isEmpty() ? "Jogador " + symbol : name;
    }

    /** Lê uma coordenada do tabuleiro no formato letra-linha e número-coluna. */
    private int[] readPosition(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.length() == 2 && input.charAt(0) >= 'a' && input.charAt(0) <= 'g'
                    && input.charAt(1) >= '1' && input.charAt(1) <= '7') {
                return new int[] { input.charAt(0) - 'a', input.charAt(1) - '1' };
            }
            System.out.println("Formato inválido. Use linha a-g e coluna 1-7 (ex.: a1).");
        }
    }

    /** Exibe o vencedor da partida. */
    private void announceWinner(Player winner) {
        System.out.println(winner.getPlayerName() + " venceu a partida!");
    }
}
