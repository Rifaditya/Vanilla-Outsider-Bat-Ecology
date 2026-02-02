package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager;
import net.minecraft.world.entity.ambient.Bat;

import java.util.EnumSet;
import java.util.Random;

/**
 * Bat Orbit Lantern Goal - Orbiting any block tagged as #minecraft:lanterns.
 * Only runs for leaders; followers use BatFollowLeaderGoal.
 */
public class BatOrbitLanternGoal extends Goal {
    private final Mob mob;
    private final Random random = new Random();

    // Orbit state
    private BlockPos targetLantern = null;
    private int orbitTimer = 0;

    public BatOrbitLanternGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.isPassenger())
            return false;
        if (!(mob.level() instanceof ServerLevel level))
            return false;

        // Only leaders scan and orbit; followers flock
        if (mob instanceof Bat bat) {
            if (!BatSwarmManager.isLeader(bat, level))
                return false;
        }

        // Scan for Lanterns frequently if not already targeting one
        if (targetLantern == null && mob.getRandom().nextInt(40) == 0) {
            // Hardcoded range 24 for efficiency
            targetLantern = findNearbyLantern(level, 24);
        }

        return targetLantern != null;
    }

    @Override
    public void start() {
        orbitTimer = 0;
    }

    @Override
    public void stop() {
        targetLantern = null;
        orbitTimer = 0;
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (targetLantern != null) {
            // Check if still valid (using Tag)
            if (!serverLevel.getBlockState(targetLantern).is(BlockTags.LANTERNS)) {
                targetLantern = null;
                orbitTimer = 0;
                return;
            }

            // Orbit the lantern
            orbitTimer++;

            // Get colony size for dynamic height
            int colonySize = 10;
            if (mob instanceof Bat bat) {
                BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm(bat, serverLevel);
                colonySize = swarm.colonySize();
            }
            double dynamicHeight = getLeaderHeight(colonySize);

            double angle = (orbitTimer * 0.15) % (Math.PI * 2);
            double orbitRadius = 2.0;
            double targetX = targetLantern.getX() + 0.5 + Math.cos(angle) * orbitRadius;
            double targetY = targetLantern.getY() + dynamicHeight; // Maintains height rule
            double targetZ = targetLantern.getZ() + 0.5 + Math.sin(angle) * orbitRadius;

            // Move toward orbit position
            double dx = targetX - mob.getX();
            double dy = targetY - mob.getY();
            double dz = targetZ - mob.getZ();

            Vec3 movement = mob.getDeltaMovement();
            mob.setDeltaMovement(movement.add(
                    (Math.signum(dx) * 0.3 - movement.x) * 0.1,
                    (Math.signum(dy) * 0.3 - movement.y) * 0.1,
                    (Math.signum(dz) * 0.3 - movement.z) * 0.1));

            // Orbit for 10 seconds (200 ticks) as per refined plan
            if (orbitTimer > 200) {
                targetLantern = null;
                orbitTimer = 0;
            }
        }
    }

    private double getLeaderHeight(int colonySize) {
        if (colonySize <= 10) {
            return 5.0;
        }
        return 5.0 * Math.sqrt(colonySize / 10.0);
    }

    private BlockPos findNearbyLantern(ServerLevel level, int range) {
        BlockPos center = mob.blockPosition();
        int scanRange = Math.min(range, 16);

        for (int i = 0; i < 15; i++) { // 15 random samples
            int dx = random.nextInt(scanRange * 2 + 1) - scanRange;
            int dy = random.nextInt(scanRange) - scanRange / 2;
            int dz = random.nextInt(scanRange * 2 + 1) - scanRange;

            BlockPos checkPos = center.offset(dx, dy, dz);
            BlockState state = level.getBlockState(checkPos);

            if (state.is(BlockTags.LANTERNS)) {
                return checkPos;
            }
        }
        return null;
    }
}
