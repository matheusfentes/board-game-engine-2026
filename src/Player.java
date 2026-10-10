public class Player {
    private int playerCode;
    private String color; // Vermelho ou Preto

    /**
     * 
     * @param playerCode Define o código referente a cada jogador
     * @param color Define a cor de cada jogador
     */
    public Player(int playerCode, String color) {
        this.color = color;
        this.playerCode = playerCode;
    }

    public String getColor() {
        return color;
    }
    
    public int getPlayerCode() {
        return playerCode;
    }
}