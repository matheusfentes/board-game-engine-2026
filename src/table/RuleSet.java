package table;
import pieces.Piece;
import config.GameConfig;
import java.util.*;

/** Fornece regras configuradas e verificações compartilhadas dos jogos de tabuleiro. */
public class RuleSet {
    private final GameConfig config;
    private static final int[][][] TRILHA_MILLS = {
        {{0,0},{0,3},{0,6}}, {{1,1},{1,3},{1,5}}, {{2,2},{2,3},{2,4}},
        {{3,0},{3,1},{3,2}}, {{3,4},{3,5},{3,6}}, {{4,2},{4,3},{4,4}},
        {{5,1},{5,3},{5,5}}, {{6,0},{6,3},{6,6}},
        {{0,0},{3,0},{6,0}}, {{1,1},{3,1},{5,1}}, {{2,2},{3,2},{4,2}},
        {{0,3},{1,3},{2,3}}, {{4,3},{5,3},{6,3}}, {{2,4},{3,4},{4,4}},
        {{1,5},{3,5},{5,5}}, {{0,6},{3,6},{6,6}}
    };

    /** Cria o conjunto de regras ligado à configuração validada do jogo. */
    public RuleSet(GameConfig config) { this.config = config; }

    /** Informa se uma regra de movimento foi ativada no JSON. */
    public boolean usesMovementRule(String rule) {
        return config.rules.movementRule.contains(rule);
    }

    /** Informa se uma regra de captura foi ativada no JSON. */
    public boolean usesCaptureRule(String rule) {
        return config.rules.captureRule.contains(rule);
    }

    /** Verifica se a coordenada corresponde a um dos 24 nós da Trilha. */
    public boolean isTrilhaNode(int row, int column) {
        for (int[][] mill : TRILHA_MILLS) for (int[] node : mill)
            if (node[0] == row && node[1] == column) return true;
        return false;
    }

    /** Verifica se dois nós da Trilha compartilham uma ligação direta. */
    public boolean areTrilhaNodesAdjacent(int rowA, int colA, int rowB, int colB) {
        for (int[][] mill : TRILHA_MILLS) {
            for (int i = 0; i < 2; i++) {
                int[] a = mill[i], b = mill[i + 1];
                if ((a[0] == rowA && a[1] == colA && b[0] == rowB && b[1] == colB)
                        || (b[0] == rowA && b[1] == colA && a[0] == rowB && a[1] == colB)) return true;
            }
        }
        return false;
    }

    /** Verifica se a peça na coordenada completa uma linha de três peças. */
    public boolean isTrilhaMill(Board board, int row, int column, String playerColor) {
        for (int[][] mill : TRILHA_MILLS) {
            boolean containsNode = false, allOwned = true;
            for (int[] node : mill) {
                if (node[0] == row && node[1] == column) containsNode = true;
                Piece piece = board.getPiece(node[0], node[1]);
                if (piece == null || !piece.getPlayer().getColor().equalsIgnoreCase(playerColor)) allOwned = false;
            }
            if (containsNode && allOwned) return true;
        }
        return false;
    }

    /** Informa se a peça selecionada pertence a uma trilha completa. */
    public boolean isTrilhaPieceProtected(Board board, Piece target, String opponentColor) {
        return isTrilhaMill(board, target.getCoordinateX(), target.getCoordinateY(), opponentColor);
    }

    /** Retorna verdadeiro quando todas as peças do jogador estão em trilhas. */
    public boolean allTrilhaPiecesInMills(Board board, String playerColor) {
        boolean found = false;
        for (int row = 0; row < board.getBoardSizeLine(); row++) {
            for (int col = 0; col < board.getBoardSizeCol(); col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getPlayer().getColor().equalsIgnoreCase(playerColor)) {
                    found = true;
                    if (!isTrilhaPieceProtected(board, piece, playerColor)) return false;
                }
            }
        }
        return found;
    }

    /** Verifica se a última jogada formou uma trilha para o jogador indicado. */
    public boolean formsTrilhaMill(Board board, int row, int column, String playerColor) {
        return isTrilhaMill(board, row, column, playerColor);
    }

    /** Retorna o limite de peças definido para a vitória na Trilha. */
    public int trilhaWinPieceThreshold() { return config.rules.winCondition.quantity; }
    /** Retorna o limite de turnos sem captura para declarar empate. */
    public int trilhaDrawTurns() { return config.rules.drawCondition.turns; }

    //Verifica se o peao ja atravessou o Rio pra deixar movimento lateral.
    /** Verifica se a linha informada já está além do rio para a cor indicada. */
    public static boolean hasCrossedRiver(int x, String playerColor) {
        if (playerColor.equalsIgnoreCase("Red")) {
            return x <= 4;
        } else {
            return x >= 5;
        }
    }

    //Regra que conta pecas no caminho em linha reta. Ex.: Utilizada pela Torre (0 pecas) e pelo Canhao (0 para mover, 1 para capturar).
    /** Conta peças entre duas coordenadas alinhadas no tabuleiro. */
    public static int countObstacles(int originX, int originY, int destX, int destY, Board board) {
        int count = 0;
        int stepX = Integer.compare(destX, originX);
        int stepY = Integer.compare(destY, originY);

        int currentX = originX + stepX;
        int currentY = originY + stepY;

        while (currentX != destX || currentY != destY) {
            if (board.getPiece(currentX, currentY) != null) {
                count++;
            }
            currentX += stepX;
            currentY += stepY;
        }

        return count;
    }

    //Regra do Flying General: os generais nao podem se encarar diretamente na mesma fileira sem nenhuma peca no caminho.
    /** Verifica se os Generais estão frente a frente sem uma peça entre eles. */
    public static boolean areGeneralsFacingEachOther(Board board) {
        int redGeneralX = -1, redGeneralY = -1;
        int blackGeneralX = -1, blackGeneralY = -1;

        for (int x = 0; x < board.getBoardSizeLine(); x++) {
            for (int y = 3; y <= 5; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getClass().getSimpleName().equalsIgnoreCase("General")) {
                    if (piece.getPlayer().getColor().equalsIgnoreCase("Red")) {
                        redGeneralX = x;
                        redGeneralY = y;
                    } else {
                        blackGeneralX = x;
                        blackGeneralY = y;
                    }
                }
            }
        }

        if (redGeneralY != -1 && redGeneralY == blackGeneralY) {
            return countObstacles(blackGeneralX, redGeneralY, redGeneralX, redGeneralY, board) == 0;
        }

        return false;
    }

    // Verifica se o General da cor indicada está sob ameaça de alguma peça inimiga
    /** Informa se o General da cor indicada está atacado por uma peça adversária. */
    public static boolean isPlayerInCheck(String playerColor, Board board) {
        int generalX = -1;
        int generalY = -1;

        // Localiza a posição do General do jogador no tabuleiro
        for (int x = 0; x < board.getBoardSizeLine(); x++) {
            for (int y = 3; y <= 5; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getClass().getSimpleName().equalsIgnoreCase("General")) {
                    if (piece.getPlayer().getColor().equalsIgnoreCase(playerColor)) {
                        generalX = x;
                        generalY = y;
                        break;
                    }
                }
            }
            if (generalX != -1) break;
        }

        // Se o General não for encontrado, não tem xeque
        if (generalX == -1) {
            return false;
        }

        // Itera sobre todas as peças do adversário e testa se alguma consegue mover até ao General
        for (int x = 0; x < board.getBoardSizeLine(); x++) {
            for (int y = 0; y < board.getBoardSizeCol(); y++) {
                Piece enemyPiece = board.getPiece(x, y);
                if (enemyPiece != null && !enemyPiece.getPlayer().getColor().equalsIgnoreCase(playerColor)) {
                    int origX = enemyPiece.getCoordinateX();
                    int origY = enemyPiece.getCoordinateY();

                    // Testa o ataque e restaura as coordenadas
                    boolean attacksGeneral = enemyPiece.moveTo(generalX, generalY, board);
                    enemyPiece.setCoordinates(origX, origY);
                    if (attacksGeneral) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Metodo que verifica se houve o xeque-mate
    /** Verifica se o jogador está em xeque e não dispõe de jogadas legais. */
    public static boolean isCheckmate(String playerColor, Board board) {
        return isPlayerInCheck(playerColor, board) && !board.hasAnyLegalMove(playerColor);
    }
}
