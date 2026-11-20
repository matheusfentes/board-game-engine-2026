package config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class GameLoader {
    private final Gson gson = new GsonBuilder().create();
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
