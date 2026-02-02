package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager;
import net.minecraft.world.entity.ambient.Bat;

import java.util.EnumSet;
import java.util.Random;

/**
 * Bat Pollinate Goal - Orbiting lanterns and applying bonemeal dusting.
 * Only runs for leaders; followers use BatFollowLeaderGoal.
 */
public class BatPollinateGoal extends Goal {
    private final Mob mob;
    private final Random random = new Random();

    // Pollination state
    private BlockPos targetLantern = null;
    private int orbitTimer = 0;
    private int pollinationCooldown = 0;

    public BatPollinateGoal(Mob mob) {
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

        // Scan for Lanterns frequently if not already targeting one (Logic from
        // BatForageGoal)
        if (targetLantern == null && mob.getRandom().nextInt(20) == 0) {
            int range = level.getGameRules().get(BatEcologyRules.BAT_ROOST_RANGE);
            targetLantern = findNearbyLantern(level, range);
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

    /**
     * Calculate leader flight height based on colony size (Concept L72).
     */
    private double getLeaderHeight(int colonySize) {
        if (colonySize <= 10) {
            return 5.0;
        }
        return 5.0 * Math.sqrt(colonySize / 10.0);
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Cooldowns
        if (pollinationCooldown > 0) {
            pollinationCooldown--;
        }

        int pollinateChance = serverLevel.getGameRules().get(BatEcologyRules.BAT_POLLINATE_CHANCE);

        // Get colony size for dynamic height
        int colonySize = 10;
        if (mob instanceof Bat bat) {
            BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm(bat, serverLevel);
            colonySize = swarm.colonySize();
        }
        double dynamicHeight = getLeaderHeight(colonySize);

        if (targetLantern != null) {
            // Check if still valid
            if (!serverLevel.getBlockState(targetLantern).is(Blocks.LANTERN) &&
                    !serverLevel.getBlockState(targetLantern).is(Blocks.SOUL_LANTERN)) {
                targetLantern = null;
                orbitTimer = 0;
                return;
            }

            // Orbit the lantern (Concept L31)
            orbitTimer++;

            double angle = (orbitTimer * 0.15) % (Math.PI * 2);
            double orbitRadius = 2.0;
            double targetX = targetLantern.getX() + 0.5 + Math.cos(angle) * orbitRadius;
            double targetY = targetLantern.getY() + dynamicHeight; // Maintains height rule (Concept L35)
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

            // Pollination Burst every 20 ticks (Concept L34, L44)
            if (orbitTimer % 20 == 0 && pollinationCooldown <= 0) {
                pollinate(serverLevel, targetLantern, pollinateChance);
            }

            // Leave after 40-60 ticks (2-3 seconds) (Concept L31)
            if (orbitTimer > 40 + random.nextInt(20)) {
                targetLantern = null;
                orbitTimer = 0;
                pollinationCooldown = 100; // 5 second cooldown
            }
        }
    }

    private BlockPos findNearbyLantern(ServerLevel level, int range) {
        BlockPos center = mob.blockPosition();
        int scanRange = Math.min(range, 16);

        for (int i = 0; i < 10; i++) {
            int dx = random.nextInt(scanRange * 2 + 1) - scanRange;
            int dy = random.nextInt(scanRange) - scanRange / 2;
            int dz = random.nextInt(scanRange * 2 + 1) - scanRange;

            BlockPos checkPos = center.offset(dx, dy, dz);
            BlockState state = level.getBlockState(checkPos);

            if (state.is(Blocks.LANTERN) || state.is(Blocks.SOUL_LANTERN)) {
                return checkPos;
            }
        }
        return null;
    }

    /**
     * Dusting effect - applies bonemeal to 9x9 area (Concept L34, L46).
     */
    private void pollinate(ServerLevel level, BlockPos lanternPos, int chance) {
        if (random.nextInt(100) >= chance) {
            return;
        }

        int radius = 4; // 9x9 area
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy > -10; dy--) {
                    BlockPos checkPos = lanternPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.getBlock() instanceof CropBlock crop) {
                        if (!crop.isMaxAge(state)) {
                            if (random.nextInt(10) == 0) {
                                level.levelEvent(2005, checkPos, 0);
                                BlockState newState = crop.getStateForAge(
                                        Math.min(crop.getMaxAge(), crop.getAge(state) + 1));
                                level.setBlock(checkPos, newState, 2);
                            }
                        }
                        break;
                    } else if (state.isSolid()) {
                        break;
                    }
                }
            }
        }
    }
}
