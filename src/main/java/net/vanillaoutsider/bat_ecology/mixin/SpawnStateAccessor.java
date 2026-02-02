package net.vanillaoutsider.bat_ecology.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NaturalSpawner.SpawnState.class)
public interface SpawnStateAccessor {
    @Invoker("canSpawn")
    boolean invokeCanSpawn(EntityType<?> type, BlockPos pos, ChunkAccess chunk);

    @Invoker("afterSpawn")
    void invokeAfterSpawn(Mob mob, ChunkAccess chunk);
}
