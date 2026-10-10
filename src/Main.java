public class Main {

    public static void main(String[] args) {

        Player player1 = new Player("player 1", "Red");

        Player player2 = new Player("player 2", "Black");

        Board board = new Board(player1, player2);

        System.out.println("Match has begun!");

        RuleSet rule = new RuleSet();
    }
}

