package com.vanillaoutsider.batecology.spawn;

import com.vanillaoutsider.batecology.access.BatEcologyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

public class BatSpawningLogic {

    private static final ThreadLocal<Boolean> IS_PACK_SPAWNING = ThreadLocal.withInitial(() -> false);

    public static void checkPackSpawn(Mob mob, Level level) {
        if (level.isClientSide() || !(mob instanceof Bat bat))
            return;
        if (IS_PACK_SPAWNING.get())
            return;

        if (level.getRandom().nextFloat() > 0.1)
            return;

        BlockPos pos = bat.blockPosition();

        if (!level.isEmptyBlock(pos.above()))
            return;

        try {
            IS_PACK_SPAWNING.set(true);
            int packSize = 4 + level.getRandom().nextInt(6);

            for (int i = 0; i < packSize; i++) {
                Bat neighbor = EntityType.BAT.create(level, EntitySpawnReason.NATURAL);
                if (neighbor != null) {
                    neighbor.setPos(bat.getX(), bat.getY(), bat.getZ());
                    if (level instanceof ServerLevel sl) {
                        neighbor.finalizeSpawn(sl, sl.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
                        sl.addFreshEntity(neighbor);
                    }
                }
            }
        } finally {
            IS_PACK_SPAWNING.set(false);
        }
    }
}
