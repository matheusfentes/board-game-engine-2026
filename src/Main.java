public class Main {

    public static void main(String[] args) {

        Player jogador1 = new Player("Jogador 1", "Vermelho");

        Player jogador2 = new Player("Jogador 2", "Preto");

        Board board = new Board(jogador1, jogador2);

        System.out.println("Partida iniciada!");
    }
}