package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.server.level.ServerLevel;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager;
import net.minecraft.world.entity.ambient.Bat;
import java.util.EnumSet;
import java.util.Random;

/**
 * Bat Hunt Goal - Nighttime traversal and range patrol.
 * Only runs for leaders; followers use BatFollowLeaderGoal.
 */
public class BatHuntGoal extends Goal {
    private final Mob mob;
    private final Random random = new Random();

    public BatHuntGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.isPassenger())
            return false;
        if (!(mob.level() instanceof ServerLevel level))
            return false;

        // Only leaders hunt; followers flock
        if (mob instanceof Bat bat) {
            return BatSwarmManager.isLeader(bat, level);
        }
        return true;
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int range = serverLevel.getGameRules().get(BatEcologyRules.BAT_ROOST_RANGE);

        // Normal Hunting/Foraging Movement (Extracted from old BatForageGoal)
        if (mob.getRandom().nextInt(50) == 0) {
            double x = mob.getX() + (random.nextDouble() - 0.5) * range;
            double y = mob.getY() + (random.nextDouble() - 0.5) * (range / 2.0);
            double z = mob.getZ() + (random.nextDouble() - 0.5) * range;

            mob.getNavigation().moveTo(x, y, z, 1.2);
        }
    }
}
