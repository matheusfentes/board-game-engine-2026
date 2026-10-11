import java.util.Scanner;

import controller.GameManagement;
import table.*;

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
        while(!gameManager.isGameOver()){
            // tempo restante para jogar do jogador atual
            System.out.println("\nTempo: " + gameManager.getTime() + " segundos");
            gameBoard.printBoard();
            
            // escolha da peca
            System.out.println("\nEscolha a linha da peca: ");
            int originY = scanner.nextInt();
            System.out.println("Escolha a coluna da peca: ");
            int originX = scanner.nextInt();

            // escolha do local destino da peca
            System.out.println("Escolha a posicao da linha do destino: ");
            int destinationY = scanner.nextInt();
            System.out.println("Escolha a posicao da coluna do destino: ");
            int destinationX = scanner.nextInt();

            // verificacao de execucao de movimento
            if(!gameManager.executeMove(originX, originY, destinationX, destinationY)){
                System.out.println("\n");
            }
        }
        // fechamento do scanner
        scanner.close();
    }
}