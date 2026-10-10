public class Main {
    public static void main(String[] args) {
        Player player1 = new Player(1, "Red");
        Player player2 = new Player(2, "Black");

        Board board = new Board(player1, player2);
        GameManagement gameManager = new GameManagement(board, new Player[]{player1, player2});

        System.out.println("Match has begun!");
        System.out.println("Time: " + gameManager.getTime() + " seconds");

        RuleSet rule = new RuleSet();
    }
}
//@matheushuang: mexi no tempo