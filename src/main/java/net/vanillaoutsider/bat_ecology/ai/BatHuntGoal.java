package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
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

        // Hunting/Foraging Movement
        if (mob.getRandom().nextInt(20) == 0) { // Check more frequently

            // "Exit Hiding" Logic: If currently indoors (no sky), try to find a spot WITH
            // sky
            boolean isIndoors = !serverLevel.canSeeSky(mob.blockPosition());

            double x, y, z;

            if (isIndoors) {
                // Try to find an exit (search for sky)
                BlockPos bestPos = null;
                for (int i = 0; i < 10; i++) {
                    BlockPos testPos = mob.blockPosition().offset(
                            random.nextInt(16) - 8,
                            random.nextInt(8) - 2, // Biased slightly up/flat, not too much down
                            random.nextInt(16) - 8);

                    if (serverLevel.isEmptyBlock(testPos) && serverLevel.canSeeSky(testPos)) {
                        bestPos = testPos;
                        break; // Found an exit! Make a break for it.
                    }
                }

                if (bestPos != null) {
                    x = bestPos.getX();
                    y = bestPos.getY();
                    z = bestPos.getZ();
                } else {
                    // Still stuck indoors, just wander but bias UPWARDS to find exit?
                    x = mob.getX() + (random.nextDouble() - 0.5) * range;
                    y = mob.getY() + (random.nextDouble() * 5.0) - 1.0; // Bias UP
                    z = mob.getZ() + (random.nextDouble() - 0.5) * range;
                }
            } else {
                // Already outside, standard patrol
                x = mob.getX() + (random.nextDouble() - 0.5) * range;
                y = mob.getY() + (random.nextDouble() - 0.5) * (range / 2.0);
                z = mob.getZ() + (random.nextDouble() - 0.5) * range;
            }

            mob.getNavigation().moveTo(x, y, z, 1.2);
        }
    }
}
