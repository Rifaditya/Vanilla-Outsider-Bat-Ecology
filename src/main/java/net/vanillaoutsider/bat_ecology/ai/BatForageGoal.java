package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;

import java.util.EnumSet;
import java.util.Random;

/**
 * Bat foraging goal with Lantern circuit pollination.
 * When near a Lantern, orbits and applies bonemeal to 9x9 area.
 */
public class BatForageGoal extends Goal {
    private final Mob mob;
    private final Random random = new Random();

    public BatForageGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !mob.isPassenger();
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // --- Normal Foraging Movement ---
        // --- Normal Foraging Movement ---
        int range = serverLevel.getGameRules().get(BatEcologyRules.BAT_ROOST_RANGE);
        if (mob.getRandom().nextInt(50) == 0) {
            double x = mob.getX() + (random.nextDouble() - 0.5) * range;
            double y = mob.getY() + (random.nextDouble() - 0.5) * (range / 2.0);
            double z = mob.getZ() + (random.nextDouble() - 0.5) * range;

            mob.getNavigation().moveTo(x, y, z, 1.2);
        }
    }

}
