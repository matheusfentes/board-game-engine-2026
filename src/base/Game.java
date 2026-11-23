package base;
import java.util.Scanner;

/** Contrato comum dos jogos que podem ser executados pela engine. */
public interface Game {
    /**
     * Inicia e executa o ciclo interativo do jogo.
     * @param scanner fonte de entrada do terminal
     */
    void start(Scanner scanner);
}
