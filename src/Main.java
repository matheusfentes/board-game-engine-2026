import java.util.Scanner;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import config.GameConfig;
import config.GameLoader;
import engine.Engine;

/** Ponto de entrada que carrega a configuração JSON e inicia o jogo escolhido. */
public class Main {
    /**
     * Configura a saída UTF-8, carrega o arquivo informado e inicia a engine.
     *
     * @param args primeiro argumento opcional com o caminho do JSON do jogo
     * @throws Exception se o arquivo não puder ser lido ou a configuração for inválida
     */
    public static void main(String[] args) throws Exception {
        // Mantem os simbolos do tabuleiros consistentes na UTF-8 se n tiver configurado no sistema da pessoa
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        String path = args.length > 0 ? args[0] : "games/xiangqi.json";
        GameConfig config = new GameLoader().load(path);
        new Engine(config).run(scanner);
    }
}
