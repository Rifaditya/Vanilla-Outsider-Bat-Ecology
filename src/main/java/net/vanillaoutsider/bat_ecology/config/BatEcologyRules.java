package net.vanillaoutsider.bat_ecology.config;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

/**
 * Native GameRules configuration for Bat Ecology.
 * Uses Fabric GameRule API with 26.1 unobfuscated mappings.
 */
public class BatEcologyRules {

        public static GameRule<Integer> BAT_ROOST_RANGE;
        public static GameRule<Integer> BAT_GUANO_INTERVAL;
        public static GameRule<Integer> BAT_SWARM_MAX;
        public static GameRule<Integer> BAT_MAX_COLONY_SCALE;
        public static GameRule<Integer> BAT_SPAWN_MULT;
        public static GameRule<Integer> BAT_POLLINATE_CHANCE;

        // Custom Category
        public static final GameRuleCategory BAT_ECOLOGY = GameRuleCategory
                        .register(Identifier.fromNamespaceAndPath("bat_ecology", "main"));

        public static void register() {
                // Load Global Configuration Template
                BatEcologyConfig.load();

                // Range: Blocks | [Default: Configured, Min: 1]
                BAT_ROOST_RANGE = GameRuleBuilder.forInteger(BatEcologyConfig.getRoostRange())
                                .minValue(1)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "roost_range"));

                // Interval: Ticks between drops | [Default: 200 (10s), Min: 1]
                BAT_GUANO_INTERVAL = GameRuleBuilder.forInteger(BatEcologyConfig.getGuanoInterval())
                                .minValue(1)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "guano_interval"));

                // Max Swarm Size: Count | [Default: Configured, Min: 2]
                BAT_SWARM_MAX = GameRuleBuilder.forInteger(BatEcologyConfig.getSwarmMax())
                                .minValue(2)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "swarm_max"));

                // Max Colony Scale: Permille (150 = 1.5x) | [Default: Configured, Min: 100]
                BAT_MAX_COLONY_SCALE = GameRuleBuilder.forInteger(BatEcologyConfig.getMaxColonyScale())
                                .minValue(100)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "max_colony_scale"));

                // Spawn Multiplier: scalar | [Default: Configured, Min: 1]
                BAT_SPAWN_MULT = GameRuleBuilder.forInteger(BatEcologyConfig.getSpawnMult())
                                .minValue(1)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "spawn_mult"));

                // Pollinate Chance: 1 in X ticks | [Default: Configured, Min: 1]
                BAT_POLLINATE_CHANCE = GameRuleBuilder.forInteger(BatEcologyConfig.getPollinateChance())
                                .minValue(1)
                                .category(BAT_ECOLOGY)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "pollinate_chance"));
        }
}
