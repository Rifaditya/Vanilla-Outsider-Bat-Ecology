package com.vanillaoutsider.batecology.mixin;

import com.vanillaoutsider.batecology.social.core.InternalBatRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void batecology$onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if ((Object) this instanceof Bat bat) {
             if (!bat.level().isClientSide()) {
                 InternalBatRegistry.unregister(bat);
             }
        }
    }
}
