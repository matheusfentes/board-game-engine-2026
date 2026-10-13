package table;
import pieces.Piece;

public class RuleSet {
    private boolean isCheck;
    
    public RuleSet() {
        this.isCheck = false;
    }

    /**
- Construtor que define o xeque.
- 
@param isCheck   Define se algum General esta em xeque.
     */
    public RuleSet(boolean isCheck) {
        this.isCheck = isCheck;
    }

    

    //Verifica se a coordenada pertence ao Palacio (zona "morta" do rei) do jogador.
    public static boolean isInPalace(int x, int y, String playerColor) {
        if (y < 3 || y > 5) {
            return false;
        }
        if (playerColor.equalsIgnoreCase("Black")) {
            return x >= 0 && x <= 2;
        } else {
            return x >= 7 && x <= 9;
        }
    }

    //Verifica se a coordenada ta na regiao do Rio (linha 4 e 5).
    public static boolean isInRiver(int x) {
        return x == 4 || x == 5;
    }

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

        for (int x = 0; x < 10; x++) {
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
//@luis mudei a logica do cheque
    /// Verifica se o General da cor indicada está sob ameaça de alguma peça inimiga
    public static boolean isPlayerInCheck(String playerColor, Board board) {
        int generalX = -1;
        int generalY = -1;

        // 1. Localiza a posição do General do jogador no tabuleiro
        for (int x = 0; x < 10; x++) {
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
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 9; y++) {
                Piece enemyPiece = board.getPiece(x, y);
                if (enemyPiece != null && !enemyPiece.getPlayer().getColor().equalsIgnoreCase(playerColor)) {
                    int origX = enemyPiece.getCoordinateX();
                    int origY = enemyPiece.getCoordinateY();

                    // Testa a validade do movimento
                    if (enemyPiece.moveTo(generalX, generalY, board)) {
                        // Restaura imediatamente a posição interna da peça de teste
                        enemyPiece.moveTo(origX, origY, board);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // getter e setter do check (nao sei como isso vai continuar)
    public boolean getIsCheck() {
        return this.isCheck;
    }
    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}