package com.starkgroup.vms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads .env environment variables into the Spring environment and system properties
 * before application context or datasource initialization.
 * Keeps credentials and environment-specific settings out of source control.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);

    private static final String[] CANDIDATE_PATHS = {
            ".env",
            "src/.env",
            "../.env"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> envMap = loadDotenvMap();
        if (!envMap.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("dotenvProperties", envMap));
        }
    }

    /**
     * Reads candidate .env files and loads them into System properties if not already present.
     */
    public static Map<String, Object> loadDotenvIntoSystemProperties() {
        Map<String, Object> envMap = loadDotenvMap();
        for (Map.Entry<String, Object> entry : envMap.entrySet()) {
            if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return envMap;
    }

    private static Map<String, Object> loadDotenvMap() {
        Map<String, Object> map = new HashMap<>();

        for (String candidate : CANDIDATE_PATHS) {
            Path path = Paths.get(candidate).toAbsolutePath().normalize();
            if (Files.exists(path) && Files.isRegularFile(path)) {
                log.info("Loading environment configuration from: {}", path);
                parseFile(path, map);
                break; // Stop after first matched .env file
            }
        }

        return map;
    }

    private static void parseFile(Path path, Map<String, Object> map) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equalsIdx = line.indexOf('=');
                if (equalsIdx > 0) {
                    String key = line.substring(0, equalsIdx).trim();
                    String value = line.substring(equalsIdx + 1).trim();

                    // Strip surrounding single or double quotes
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                        if (value.length() >= 2) {
                            value = value.substring(1, value.length() - 1);
                        }
                    }

                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            log.warn("Could not read .env file at {}: {}", path, e.getMessage());
        }
    }
}

