package table;
import pieces.Piece;

public class RuleSet {
    //Verifica se o peao ja atravessou o Rio pra deixar movimento lateral.
    public static boolean hasCrossedRiver(int x, String playerColor) {
        if (playerColor.equalsIgnoreCase("Red")) {
            return x <= 4;
        } else {
            return x >= 5;
        }
    }

    //Regra que conta pecas no caminho em linha reta. Ex.: Utilizada pela Torre (0 pecas) e pelo Canhao (0 para mover, 1 para capturar).
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

    // Recebe o metodo de verificacao de movimentos do board
    public static boolean hasAnyLegalMove(String playerColor, Board board) {
        return board.hasAnyLegalMove(playerColor);
    }

    // Metodo que verifica se houve o xeque-mate
    public static boolean isCheckmate(String playerColor, Board board) {
        return isPlayerInCheck(playerColor, board) && !hasAnyLegalMove(playerColor, board);
    }
}