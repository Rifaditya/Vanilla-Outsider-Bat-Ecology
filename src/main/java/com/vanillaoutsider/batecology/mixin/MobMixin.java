package com.vanillaoutsider.batecology.mixin;

import com.vanillaoutsider.batecology.spawn.BatSpawningLogic;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void onFinalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
            EntitySpawnReason reason, SpawnGroupData spawnData, CallbackInfoReturnable<SpawnGroupData> cir) {
        // Delegate to helper logic
        if (reason == EntitySpawnReason.NATURAL) { // Only natural spawns, not breeding/eggs
            BatSpawningLogic.checkPackSpawn((Mob) (Object) this, level.getLevel());
        }
    }
}
