package table;

public class Player {
    private String playerName;
    private int playerCode;
    private String color; // Vermelho ou Preto

    /**
     * Construtor para criar uma instancia da classe Player
     * 
     * @param playerName    Define o nome do jogador para ser utilizado durante a execucao
     * @param playerCode    Define o código referente a cada jogador
     * @param color         Define a cor de cada jogador
     */
    public Player(String playerName, int playerCode, String color) {
        this.playerName = playerName;
        this.playerCode = playerCode;
        this.color = color;
    }

    // getters dos atributos de Player
    public String getPlayerName(){ return this.playerName; }
    public String getColor() { return this.color; }
    public int getPlayerCode() { return this.playerCode; }
}