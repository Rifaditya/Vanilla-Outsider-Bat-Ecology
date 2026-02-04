package net.vanillaoutsider.bat_ecology.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.world.entity.ambient.Bat;
import net.dasik.social.api.breeding.UniversalAgeable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatRenderer.class)
public class BatRendererMixin {

    @Inject(method = "scale", at = @At("HEAD"))
    protected void bat_ecology$scaleBaby(Bat bat, PoseStack poseStack, float partialTickTime, CallbackInfo ci) {
        if (bat instanceof UniversalAgeable ageable && ageable.isUniversalBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }
}
