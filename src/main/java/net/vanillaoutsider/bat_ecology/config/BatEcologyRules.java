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
        public static GameRule<Integer> BAT_GUANO_RATE;
        public static GameRule<Integer> BAT_SWARM_MAX;
        public static GameRule<Integer> BAT_POLLINATE_CHANCE;
        public static GameRule<Integer> BAT_MAX_COLONY_SCALE;

        public static void register() {
                // Load Global Configuration Template
                BatEcologyConfig.load();

                // Range: Blocks | [Default: Configured, Min: 1]
                BAT_ROOST_RANGE = GameRuleBuilder.forInteger(BatEcologyConfig.getRoostRange())
                                .minValue(1)
                                .category(GameRuleCategory.MOBS)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "roost_range"));

                // Rate: Permille (per 1000 ticks) | [Default: Configured, Min: 0]
                BAT_GUANO_RATE = GameRuleBuilder.forInteger(BatEcologyConfig.getGuanoRate())
                                .minValue(0)
                                .category(GameRuleCategory.MOBS)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "guano_rate"));

                // Max Swarm Size: Count | [Default: Configured, Min: 2]
                BAT_SWARM_MAX = GameRuleBuilder.forInteger(BatEcologyConfig.getSwarmMax())
                                .minValue(2)
                                .category(GameRuleCategory.SPAWNING)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "swarm_max"));

                // Pollination Chance: 1/x per second | [Default: Configured, Min: 1]
                BAT_POLLINATE_CHANCE = GameRuleBuilder.forInteger(BatEcologyConfig.getPollinateChance())
                                .minValue(1)
                                .category(GameRuleCategory.MOBS)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "pollinate_chance"));

                // Max Colony Scale: Permille (150 = 1.5x) | [Default: Configured, Min: 100]
                BAT_MAX_COLONY_SCALE = GameRuleBuilder.forInteger(BatEcologyConfig.getMaxColonyScale())
                                .minValue(100)
                                .category(GameRuleCategory.MOBS)
                                .buildAndRegister(Identifier.fromNamespaceAndPath("bat_ecology", "max_colony_scale"));
        }
}
