public class Player {
    private String nome;
    private String cor; // Vermelho ou Preto

    public Player(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
    }

    public String getNome() {
        return nome;
    }

    public String getCor() {
        return cor;
    }
}