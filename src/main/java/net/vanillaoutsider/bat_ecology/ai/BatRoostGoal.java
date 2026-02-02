package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import java.util.EnumSet;

public class BatRoostGoal extends Goal {
    private final Mob mob;

    public BatRoostGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Only run if we are NOT already hanging
        if (mob instanceof net.minecraft.world.entity.ambient.Bat bat && bat.isResting()) {
            return false;
        }

        // Leader Only (Concept L22)
        if (mob instanceof net.minecraft.world.entity.ambient.Bat bat
                && mob.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager.isLeader(bat, serverLevel);
        }

        return false;
    }

    @Override
    public void tick() {
        if (mob.getRandom().nextInt(20) == 0) { // Check more frequently to find roost quickly
            int range = ((net.minecraft.server.level.ServerLevel) mob.level()).getGameRules()
                    .get(net.vanillaoutsider.bat_ecology.config.BatEcologyRules.BAT_ROOST_RANGE);
            // Don't scan the entire range (too laggy). Scan local area.
            int scanRadius = Math.min(range, 8);

            // Pick a random spot within scan radius
            BlockPos randomPos = mob.blockPosition().offset(
                    mob.getRandom().nextInt(scanRadius * 2 + 1) - scanRadius,
                    mob.getRandom().nextInt(scanRadius * 2 + 1) - scanRadius,
                    mob.getRandom().nextInt(scanRadius * 2 + 1) - scanRadius);

            // Check if valid ceiling AND NO SKYLIGHT (Dark/Indoors)
            // Condition: Solid block above, Air below, No sky access
            if (mob.level().getBlockState(randomPos).isSolid() &&
                    mob.level().isEmptyBlock(randomPos.below()) &&
                    !mob.level().canSeeSky(randomPos.below())) {

                // Fly to the air block BELOW the ceiling
                BlockPos roostSpot = randomPos.below();
                mob.getNavigation().moveTo(roostSpot.getX() + 0.5, roostSpot.getY() + 0.5, roostSpot.getZ() + 0.5, 1.0);

                // If we are close enough, set resting
                if (mob.distanceToSqr(roostSpot.getX() + 0.5, roostSpot.getY() + 0.5, roostSpot.getZ() + 0.5) < 2.0) {
                    if (mob instanceof net.minecraft.world.entity.ambient.Bat bat) {
                        bat.setResting(true);
                    }
                }
            }
        }
    }
}
