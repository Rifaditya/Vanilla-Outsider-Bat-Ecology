package net.vanillaoutsider.bat_ecology.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Global Configuration Template.
 * Loads defaults from config/bat_ecology.json to be used by GameRules.
 * Protocol 12: Hybrid Architecture.
 */
public class BatEcologyConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("bat_ecology");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("bat_ecology.json");

    // Default Values
    public int roostRange = 32;
    public int guanoInterval = 200; // Ticks (10 seconds)
    public int swarmMax = 10;
    public int maxColonyScale = 150; // 1.5x (permille)
    public int spawnMult = 1;
    public int pollinateChance = 100; // 1 in 100 chance

    private static BatEcologyConfig INSTANCE = new BatEcologyConfig();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, BatEcologyConfig.class);
            } catch (IOException e) {
                LOGGER.error("Failed to load config, using defaults", e);
            }
        } else {
            save(); // Generate template if non-existent
        }
    }

    public static void save() {
        try {
            String json = GSON.toJson(INSTANCE);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    // Accessors for GameRule registration
    public static int getRoostRange() {
        return INSTANCE.roostRange;
    }

    public static int getGuanoInterval() {
        return INSTANCE.guanoInterval;
    }

    public static int getSwarmMax() {
        return INSTANCE.swarmMax;
    }

    public static int getMaxColonyScale() {
        return INSTANCE.maxColonyScale;
    }

    public static int getSpawnMult() {
        return INSTANCE.spawnMult;
    }

    public static int getPollinateChance() {
        return INSTANCE.pollinateChance;
    }
}
