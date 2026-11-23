package engine;

import java.util.Scanner;
import config.GameConfig;
import base.Game;
import base.XiangqiGame;
import base.TrilhaGame;

/** Seleciona e inicia a implementação correspondente à configuração do jogo. */
public class Engine {
    private final GameConfig config;
    /**
     * Cria a engine e valida a configuração antes de aceitar o jogo.
     * @param config configuração do jogo a executar
     * @throws IllegalArgumentException se a configuração for nula ou inválida
     */
    public Engine(GameConfig config) {
        if (config == null) throw new IllegalArgumentException("A Engine exige uma configuração validada");
        config.validar();
        this.config = config;
    }
    /** Inicia o jogo selecionado usando o scanner fornecido para entrada. - mudar isso pra alguma logica de escolha de jogos */
    public void run(Scanner scanner) {
        if ("Xiangqi".equalsIgnoreCase(config.game)) new XiangqiGame(config).start(scanner);
        else if ("Trilha".equalsIgnoreCase(config.game)) new TrilhaGame(config).start(scanner);
        else throw new IllegalArgumentException("Implementação de jogo não disponível: " + config.game);
    }
}
