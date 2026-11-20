package engine;

import java.util.Scanner;
import config.GameConfig;
import base.Game;
import base.XiangqiGame;

/** Generic runtime entry receiving a previously validated configuration. */
public class Engine {
    private final GameConfig config;
    public Engine(GameConfig config) {
        if (config == null) throw new IllegalArgumentException("A Engine exige uma configuração validada");
        config.validar();
        this.config = config;
    }
    public void run(Scanner scanner) {
        if ("Xiangqi".equalsIgnoreCase(config.game)) new XiangqiGame(config).start(scanner);
        else throw new IllegalArgumentException("Implementação de jogo não disponível: " + config.game);
    }
}
