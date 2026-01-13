package Config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.io.IOException;

public class ConfigManager {
    private static GameConfig config;
    private static final String CONFIG_FILE = "config.yml";

    private ConfigManager() {}

    // Load config file
    public static void initialize() {
        if (config == null) {
            Yaml yaml = new Yaml();
            try (InputStream inputStream = getConfigFileStream()) {
                if (inputStream == null) {
                    throw new IOException("Nie znaleziono pliku konfiguracyjnego: " + CONFIG_FILE);
                }

                config = yaml.loadAs(inputStream, GameConfig.class);
                System.out.println("Konfiguracja gry została pomyślnie wczytana.");

            } catch (IOException e) {
                System.err.println("Błąd podczas wczytywania konfiguracji: " + e.getMessage());
                throw new RuntimeException("Nie udało się wczytać konfiguracji gry.", e);
            }
        }
    }

    private static InputStream getConfigFileStream() {
        return ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
    }

    public static GameConfig getConfig() {
        if (config == null) {
            initialize();
        }
        return config;
    }

    public static GameConfig.Game getGameSettings() {
        return getConfig().getGame();
    }

    public static GameConfig.Player getPlayerSettings() {
        return getConfig().getPlayer();
    }

    public static GameConfig.Platforms getPlatforms() { return getConfig().getPlatforms(); }

    public static GameConfig.Generation getGeneration() { return getConfig().getGeneration(); }

    public static GameConfig.Platforms getPlatformsSettings() {
        return getConfig().getPlatforms();
    }
    public static GameConfig.Generation getGenerationSettings() {
        return getConfig().getGeneration();
    }
}