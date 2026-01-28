package com.vanillaoutsider.batecology.mixin.client;

import com.vanillaoutsider.batecology.BatEcologyMod;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleResources.class)
public abstract class ParticleResourcesMixin {

    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void batecology$onRegisterProviders(CallbackInfo ci) {
        ParticleResourcesAccessor accessor = (ParticleResourcesAccessor) this;

        // Hijack the Happy Villager's SpriteSet to ensure guano particles have
        // textures.
        Object spriteSetObj = accessor.getSpriteSets()
                .get(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.HAPPY_VILLAGER));

        if (spriteSetObj instanceof SpriteSet spriteSet) {
            accessor.getProviders().put(
                    BuiltInRegistries.PARTICLE_TYPE.getId(BatEcologyMod.BAT_GUANO),
                    new SuspendedTownParticle.HappyVillagerProvider(spriteSet));
        }
    }
}
