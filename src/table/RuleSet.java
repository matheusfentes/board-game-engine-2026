package table;
import pieces.Piece;

public class RuleSet {
    private boolean isCheck;
    
    public RuleSet() {
        this.isCheck = false;
    }

    /**
     * Construtor que define o xeque.
     * @param isCheck   Define se algum General esta em xeque.
     */
    public RuleSet(boolean isCheck) {
        this.isCheck = isCheck;
    }

    //Verifica se a coordenada pertence ao Palacio (zona "morta" do rei) do jogador.
    public static boolean isInPalace(int x, int y, String playerColor) {
        if (x < 3 || x > 5) {
            return false;
        }
        if (playerColor.equalsIgnoreCase("Black")) {
            return y >= 0 && y <= 2;
        } else {
            return y >= 7 && y <= 9;
        }
    }

    //Verifica se a coordenada ta na regiao do Rio (linha 4 e 5).
    public static boolean isInRiver(int y) {
        return y == 4 || y == 5;
    }

    //Verifica se o peao ja atravessou o Rio pra deixar movimento lateral.
    public static boolean hasCrossedRiver(int y, String playerColor) {
        if (playerColor.equalsIgnoreCase("Red")) {
            return y <= 4;
        } else {
            return y >= 5;
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

        for (int y = 0; y < 10; y++) {
            for (int x = 3; x <= 5; x++) {
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

        if (redGeneralX != -1 && redGeneralX == blackGeneralX) {
            return countObstacles(redGeneralX, blackGeneralY, redGeneralX, redGeneralY, board) == 0;
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