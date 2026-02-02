package net.vanillaoutsider.bat_ecology.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Global Configuration Template.
 * Loads defaults from config/bat_ecology.json to be used by GameRules.
 * Protocol 12: Hybrid Architecture.
 */
public class BatEcologyConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("bat_ecology.json");

    // Default Values
    public int roostRange = 32;
    public int guanoRate = 50; // 5.0%
    public int swarmMax = 10;
    public int pollinateChance = 100; // 1%
    public int maxColonyScale = 150; // 1.5x (permille)

    private static BatEcologyConfig INSTANCE = new BatEcologyConfig();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, BatEcologyConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback to defaults if load fails
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
            e.printStackTrace();
        }
    }

    // Accessors for GameRule registration
    public static int getRoostRange() {
        return INSTANCE.roostRange;
    }

    public static int getGuanoRate() {
        return INSTANCE.guanoRate;
    }

    public static int getSwarmMax() {
        return INSTANCE.swarmMax;
    }

    public static int getPollinateChance() {
        return INSTANCE.pollinateChance;
    }

    public static int getMaxColonyScale() {
        return INSTANCE.maxColonyScale;
    }
}
