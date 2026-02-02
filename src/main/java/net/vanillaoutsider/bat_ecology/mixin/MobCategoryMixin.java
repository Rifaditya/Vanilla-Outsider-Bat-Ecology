package net.vanillaoutsider.bat_ecology.mixin;

import net.minecraft.world.entity.MobCategory;
import net.vanillaoutsider.bat_ecology.config.BatEcologyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public class MobCategoryMixin {

    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void bat_ecology$scaleAmbientCap(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MobCategory.AMBIENT) {
            int mult = BatEcologyConfig.getSpawnMult();
            if (mult > 1) {
                // Base is 15. Scale it up, but not linearly 1:1 to avoid total chaos
                // Example: Mult 100 -> Cap 15 + (100 * 2) = 215? Or just 15 * mult?
                // Plan says: Scale based on bd_bat_spawn_mult
                // Let's go with Base + (Mult * 5) to allow plenty of room without infinite
                cir.setReturnValue(15 + (mult * 5));
            }
        }
    }
}
