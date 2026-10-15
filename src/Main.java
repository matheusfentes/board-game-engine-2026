import java.util.Scanner;

import controller.GameManagement;
import table.RuleSet;
import table.Board;
import table.Player;

public class Main {
    public static void main(String[] args) {
        // Scanner para os inputs do projeto
        Scanner scanner = new Scanner(System.in);

        // Instanciacao das regras e do tabuleiro do programa, usando o metodo estatico do gamemanagement pra inicializar o programa
        RuleSet gameRule = new RuleSet();
        Board gameBoard = GameManagement.initializeGame(scanner, gameRule);
        
        // Instanciacao do gerenciador do programa
        GameManagement gameManager = new GameManagement(gameBoard, new Player[]{gameBoard.getPlayer1(), gameBoard.getPlayer2()});

        // loop de jogadas
        while (!gameManager.isGameOver()) {
            Player jogadorAtual = gameManager.getTurn();

            // indicacao do turno do jogador e tempo restante para jogar do jogador atual
            System.out.println("\n========================================");
            System.out.println(" TURNO: " + jogadorAtual.getPlayerName() + " (" + jogadorAtual.getColor() + ")");
            System.out.println(" TEMPO RESTANTE: " + gameManager.getTime() + " segundos");
            System.out.println("========================================");

            gameBoard.printBoard();
            
            // escolha da peca
            System.out.println("Escolha a peca de origem (ex.: a2):"); 
            String origin = scanner.nextLine();

            while (origin.trim().length() != 2 
                    || !Character.isLetter(origin.trim().charAt(0)) 
                    || !Character.isDigit(origin.trim().charAt(1))) {
                System.out.println("\n[!] Entrada invalida para a origem! Formato esperado: uma letra e um numero (ex.: a2).");
                System.out.println("Escolha a peca de origem (ex.: a2):");
                origin = scanner.nextLine();
            }

            // escolha do local destino da peca
            System.out.println("Escolha a peca de destino (ex.: a3):");
            String destination = scanner.nextLine();

            while (destination.trim().length() != 2 
                    || !Character.isLetter(destination.trim().charAt(0)) 
                    || !Character.isDigit(destination.trim().charAt(1))) {
                System.out.println("\n[!] Entrada invalida para o destino! Formato esperado: uma letra e um numero (ex.: a3).");
                System.out.println("Escolha a peca de destino (ex.: a3):");
                destination = scanner.nextLine();
            }

            // trata os char para ficarem minusculos
            origin = origin.trim().toLowerCase();
            destination = destination.trim().toLowerCase();

            int originY = origin.charAt(0) - 'a';
            int originX = Character.getNumericValue(origin.charAt(1));

            int destinationY = destination.charAt(0) - 'a';
            int destinationX = Character.getNumericValue(destination.charAt(1));

            // verificacao de execucao de movimento
            gameManager.executeMove(originX, originY, destinationX, destinationY);
        }

        // fechamento do scanner
        scanner.close();
    }
}