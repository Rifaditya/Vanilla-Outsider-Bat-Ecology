package com.vanillaoutsider.batecology.client;

import com.vanillaoutsider.batecology.BatEcologyMod;
import net.fabricmc.api.ClientModInitializer;
// import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.SuspendedTownParticle;

public class BatEcologyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // ParticleFactoryRegistry.getInstance().register(BatEcologyMod.BAT_GUANO,
        // SuspendedTownParticle.HappyVillagerProvider::new);
    }
}
