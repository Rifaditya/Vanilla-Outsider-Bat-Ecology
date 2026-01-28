package com.vanillaoutsider.batecology.mixin.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleResources.class)
public interface ParticleResourcesAccessor {
    @Accessor("providers")
    Int2ObjectMap<ParticleProvider<?>> getProviders();

    @Accessor("spriteSets")
    Map<Identifier, Object> getSpriteSets();
}
