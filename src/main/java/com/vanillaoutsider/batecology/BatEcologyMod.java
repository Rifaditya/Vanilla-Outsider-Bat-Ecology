package com.vanillaoutsider.batecology;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatEcologyMod implements ModInitializer {
    public static final String MOD_ID = "batecology";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SimpleParticleType BAT_GUANO = FabricParticleTypes.simple();

    public static final AttachmentType<Integer> COLONY_ID_ATTACHMENT = AttachmentRegistry.createDefaulted(
            Identifier.fromNamespaceAndPath(MOD_ID, "colony_id"),
            () -> -1
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Vanilla Outsider: Bat Ecology initializing...");
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "bat_guano"),
                BAT_GUANO);
    }
}
