import java.util.Scanner;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import config.GameConfig;
import config.GameLoader;
import engine.Engine;

public class Main {
    public static void main(String[] args) throws Exception {
        // Make board symbols render consistently when the terminal uses UTF-8.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        String path = args.length > 0 ? args[0] : "games/xiangqi.json";
        GameConfig config = new GameLoader().load(path);
        new Engine(config).run(scanner);
    }
}
