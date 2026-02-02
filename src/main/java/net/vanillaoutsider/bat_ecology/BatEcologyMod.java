package net.vanillaoutsider.bat_ecology;

import net.fabricmc.api.ModInitializer;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatEcologyMod implements ModInitializer {
        public static final String MOD_ID = "bat_ecology";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

        @Override
        public void onInitialize() {
                LOGGER.info("Bat Ecology: Initializing Hive Mind...");

                // 1. Initialize Configuration & Rules (Protocol 12)
                BatEcologyRules.register();

                // Register Universal Breeding for Bats (Protocol 12 Update)
                net.dasik.social.api.breeding.UniversalBreedingRegistry.register(
                                net.minecraft.world.entity.EntityType.BAT,
                                net.minecraft.world.item.Items.GLOW_BERRIES,
                                6000 // 5 minutes cooldown
                );

                // Initialize Social System
                net.dasik.social.api.SocialEventRegistry.register(
                                new net.vanillaoutsider.bat_ecology.social.events.BatHuntEvent());
                net.dasik.social.api.SocialEventRegistry.register(
                                new net.vanillaoutsider.bat_ecology.social.events.BatGuanoEvent());
                net.dasik.social.api.SocialEventRegistry.register(
                                new net.vanillaoutsider.bat_ecology.social.events.SocialRoostEvent());

                // 3. Register Global Hive Mind Pulse (Protocol 14)
                // Pulse logic handled by DasikLibrary automatically via mixins usually, but if
                // this is explicit call:
                // GlobalSocialSystem is internal in DasikLibrary. The library handles pulsing.
                // If the original mod manually pulsed, we might need to check how DasikLibrary
                // works.
                // Assuming DasikLibrary's GlobalSocialSystem handles it or we call it from api.
                // Wait, GlobalSocialSystem in DasikLibrary is in `net.dasik.social.core`.
                // Ideally consumers shouldn't pulse it manually if it's a shared system?
                // Concept doc said: "Highlander Rule: Only ONE pulse per game tick".
                // It implies the system itself handles it or one mod calls it?
                // "GlobalSocialSystem.pulse(level)" was called here.
                // I should check if DasikLibrary has a public pulse method or if it registers a
                // tick handler.
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(level -> {
                        net.dasik.social.core.GlobalSocialSystem.pulse(level);
                });

                LOGGER.info("Bat Ecology: Systems Online.");
        }
}
