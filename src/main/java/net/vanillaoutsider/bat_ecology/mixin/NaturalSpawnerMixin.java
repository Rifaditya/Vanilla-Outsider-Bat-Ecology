package net.vanillaoutsider.bat_ecology.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.vanillaoutsider.bat_ecology.config.BatEcologyConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Shadow
    private static void spawnCategoryForChunk(MobCategory category, ServerLevel level, LevelChunk chunk,
            NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback) {
    }

    @Inject(method = "spawnForChunk", at = @At("TAIL"))
    private static void bat_ecology$applySpawnMultiplier(ServerLevel level, LevelChunk chunk,
            NaturalSpawner.SpawnState spawnState, java.util.List<MobCategory> spawningCategories,
            CallbackInfo ci) {

        int mult = BatEcologyConfig.getSpawnMult();
        // Check if AMBIENT is in the list of categories to spawn
        if (mult <= 1 || !spawningCategories.contains(MobCategory.AMBIENT)) {
            return;
        }

        // Loop extra attempts for Ambient category
        // Cap the extra loops to 100 to prevent server hang on extreme configs
        int loops = Math.min(mult - 1, 100);

        for (int i = 0; i < loops; i++) {
            spawnCategoryForChunk(MobCategory.AMBIENT, level, chunk, (type, pos, c) -> {
                return ((SpawnStateAccessor) (Object) spawnState).invokeCanSpawn(type, pos, c);
            }, (mob, c) -> {
                ((SpawnStateAccessor) (Object) spawnState).invokeAfterSpawn(mob, c);
            });
        }
    }
}
