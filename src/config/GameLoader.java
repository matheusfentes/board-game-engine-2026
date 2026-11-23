package config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/** Lê arquivos JSON e entrega configurações desserializadas e validadas. */
public class GameLoader {
    private final Gson gson = new GsonBuilder().create();
    /**
     * Carrega um arquivo UTF-8, desserializa-o com Gson e valida seus dados.
     * @param path caminho do arquivo JSON
     * @return configuração pronta para a engine
     * @throws IOException se houver erro ao abrir ou ler o arquivo
     * @throws IllegalArgumentException se o JSON ou os dados forem inválidos
     */
    public GameConfig load(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) throw new IllegalArgumentException("Caminho do JSON é obrigatório");
        try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            GameConfig config = gson.fromJson(reader, GameConfig.class);
            if (config == null) throw GameConfig.invalid("arquivo vazio ou sem objeto JSON");
            config.validar();
            return config;
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("JSON de configuração malformado em '" + path + "': " + e.getMessage(), e);
        }
    }
}
