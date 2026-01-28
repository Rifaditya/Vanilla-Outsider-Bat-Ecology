package com.vanillaoutsider.batecology.social.core;

import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * The Highlander Control Center (Ported for Bat Ecology).
 * Ensures only one master pulse ticks the global brain across multiple VO mods.
 */
public class GlobalSocialSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("BatEcology_HiveMind");

    // The Version Handshake: Increment this if local logic evolves
    public static final int ENGINE_VERSION = 100; // v1.0.0
    private static int activeEngineVersion = -1;

    // Highlander State
    private static boolean isSlave = false;
    private static Object masterInstance = null;
    private static Method masterPulseMethod = null;

    /**
     * The Master Pulse: Finds one random entity in the Hive Mind and ticks its
     * brain.
     */
    public static void pulse(ServerLevel level) {
        // 1. Initial Handshake (One-time check)
        if (activeEngineVersion == -1) {
            performHighlanderHandshake();
        }

        // 2. If we are a slave, delegate to Master (or do nothing if Master is another
        // mod running normally)
        if (isSlave) {
            return;
        }

        // 3. If we are Master (or Standalone), run the logic
        // For Bat Ecology Standalone, we just tick our internal registry.
        // In a full implementation, we would access the shared registry.
        // Here, we simplify: This pulse mainly serves to trigger events if we are the
        // elected leader.
        InternalBatRegistry.pulse(level);
    }

    private static void performHighlanderHandshake() {
        try {
            // Check for Better Dogs (or other Master) presence via Reflection
            Class<?> betterDogsSystem = Class.forName("net.vanillaoutsider.social.core.GlobalSocialSystem");
            int remoteVersion = betterDogsSystem.getField("ENGINE_VERSION").getInt(null);

            if (remoteVersion >= ENGINE_VERSION) {
                // They are newer or equal -> We submit.
                LOGGER.info("Hive Mind: Detected superior/peer scheduler (v{}). Entering SLAVE mode.", remoteVersion);
                isSlave = true;
                activeEngineVersion = remoteVersion;
            } else {
                // We are newer (Unlikely for a sub-mod, but possible) -> We rule.
                LOGGER.info("Hive Mind: Detected inferior scheduler (v{}). Asserting MASTER mode.", remoteVersion);
                isSlave = false;
                activeEngineVersion = ENGINE_VERSION;
            }
        } catch (ClassNotFoundException e) {
            // No other mod found. We are alone.
            LOGGER.info("Hive Mind: No external scheduler detected. Running in STANDALONE mode.");
            isSlave = false;
            activeEngineVersion = ENGINE_VERSION;
        } catch (Exception e) {
            LOGGER.error("Hive Mind: Handshake failed.", e);
            isSlave = false; // Fallback to running locally to prevent breakage
        }
    }
}
