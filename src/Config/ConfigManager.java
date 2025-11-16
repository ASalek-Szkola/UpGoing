package Config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.io.IOException;

public class ConfigManager {
    private static GameConfig config;
    private static final String CONFIG_FILE = "config.yml";

    private ConfigManager() {}

    /**
     * Inicjalizuje menedżer, wczytując plik konfiguracyjny.
     */
    public static void initialize() {
        if (config == null) {
            Yaml yaml = new Yaml();
            try (InputStream inputStream = getConfigFileStream()) {
                if (inputStream == null) {
                    throw new IOException("Nie znaleziono pliku konfiguracyjnego: " + CONFIG_FILE);
                }

                // Parsowanie pliku YAML do obiektu GameConfig
                config = yaml.loadAs(inputStream, GameConfig.class);
                System.out.println("Konfiguracja gry została pomyślnie wczytana.");

            } catch (IOException e) {
                System.err.println("Błąd podczas wczytywania konfiguracji: " + e.getMessage());
                // W krytycznych sytuacjach można rzucić RuntimeException
                throw new RuntimeException("Nie udało się wczytać konfiguracji gry.", e);
            }
        }
    }

    /**
     * Pobiera strumień wejściowy dla pliku konfiguracyjnego z zasobów.
     */
    private static InputStream getConfigFileStream() {
        // Plik musi znajdować się w katalogu zasobów (resources) w classpath.
        return ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
    }

    /**
     * Metoda statyczna do pobierania całej konfiguracji gry.
     * @return Obiekt GameConfig.
     */
    public static GameConfig getConfig() {
        if (config == null) {
            // W przypadku próby użycia przed inicjalizacją, inicjalizujemy ją
            initialize();
        }
        return config;
    }

    // Dodatkowe, wygodne metody dostępu do konkretnych sekcji
    public static GameConfig.Game getGameSettings() {
        return getConfig().getGame();
    }

    public static GameConfig.Player getPlayerSettings() {
        return getConfig().getPlayer();
    }

    public static GameConfig.Platforms getPlatforms() { return getConfig().getPlatforms(); }

    public static GameConfig.Generation getGeneration() { return getConfig().getGeneration(); }

    public static GameConfig.Scoring getScoring() { return getConfig().getScoring(); }
}