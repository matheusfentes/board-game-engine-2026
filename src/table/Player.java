package table;

/** Mantém a identidade, o nome e o símbolo/cor de um participante. */
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
    /** Cria um participante com nome, identificador único e símbolo de jogo. */
    public Player(String playerName, int playerCode, String color) {
        this.playerName = playerName;
        this.playerCode = playerCode;
        this.color = color;
    }

    // getters dos atributos de Player
    /** Retorna o nome informado pelo participante. */
    public String getPlayerName(){ return this.playerName; }
    /** Retorna a cor ou símbolo que identifica o participante no jogo. */
    public String getColor() { return this.color; }
    /** Retorna o identificador numérico do participante na partida. */
    public int getPlayerCode() { return this.playerCode; }
}
