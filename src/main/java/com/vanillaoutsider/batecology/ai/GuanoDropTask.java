package com.vanillaoutsider.batecology.ai;

import com.vanillaoutsider.batecology.BatEcologyMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GuanoDropTask {
    
    // Cached ItemStack to reduce allocation churn
    private static final ItemStack BONE_MEAL_STACK = new ItemStack(Items.BONE_MEAL);

    public static void triggerPollination(ServerLevel level, BlockPos center) {
        BlockPos.betweenClosed(center.offset(-4, -5, -4), center.offset(4, -1, 4)).forEach(pos -> {
            if (level.getRandom().nextInt(10) == 0) {
                applyGuano(level, pos, true);
            }
        });

        level.sendParticles(BatEcologyMod.BAT_GUANO, center.getX() + 0.5, center.getY(), center.getZ() + 0.5, 10, 2.0,
                2.0, 2.0, 0.1);
    }

    public static void applyGuano(Level level, BlockPos pos, boolean isPollination) {
        if (!level.isClientSide()) {
            // Use cached stack copy to ensure thread safety if ItemStacks ever become mutable in a dangerous way
            // or just rely on the fact that growCrop modifies the stack count.
            // Since growCrop decrements the count, we must recreate or reset it.
            // Optimized: Create new stack only when needed, but 1KB Haiku prefers correctness over micro-optimization if logic requires mutation.
            // However, BoneMealItem.growCrop consumes the item.
            
            ItemStack meal = BONE_MEAL_STACK.copy(); 
            if (BoneMealItem.growCrop(meal, level, pos)) {
                if (!isPollination) {
                    level.levelEvent(2005, pos, 0);
                }
            }
        }
    }
}
