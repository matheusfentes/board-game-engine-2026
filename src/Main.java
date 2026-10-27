import java.util.Scanner;

import base.Game;
import base.TrilhaGame;
import base.XiangqiGame;

public class Main {
    public static void main(String[] args) {
        // Scanner pra receber a entrada
        Scanner scanner = new Scanner(System.in);

        // Print na tela do menu pra os jogadores escolherem entre as opções dos jogos
        String menu = "Selecione o jogo:\n" + "\n" + "1 - Xiangqi\n" + "2 - Trilha";
        System.out.println(menu);

        // entrada da escolha do jogador (Xiangqi - 1, Trilha - 2)
        int choice = scanner.nextInt();
        scanner.nextLine(); // buffer

        // switch case pra criação de novas instancias das classes dos jogos baseadas na interface Game
        Game game = switch (choice) {
            case 1 -> new XiangqiGame();
            case 2 -> new TrilhaGame();
            default -> throw new IllegalArgumentException("Jogo inválido");
        };

        game.start(scanner);

    }
}