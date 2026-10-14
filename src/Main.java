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
            System.out.println("\nEscolha a COLUNA (eixo Y) da peça de origem: ");
            int originY = scanner.nextInt();
            System.out.println("Escolha a LINHA (eixo X) da peça de origem: ");
            int originX = scanner.nextInt();

            // escolha do local destino da peca
            System.out.println("Escolha a COLUNA (eixo Y) de destino: ");
            int destinationY = scanner.nextInt();
            System.out.println("Escolha a LINHA (eixo X) de destino: ");
            int destinationX = scanner.nextInt();

            if(gameManager.getTime()<=0){
                // pula pro fim da partida antes de executar o movimento
                continue;
            }

            // verificacao de execucao de movimento
            gameManager.executeMove(originX, originY, destinationX, destinationY);
        }
        // fechamento do scanner
        scanner.close();
    }
}