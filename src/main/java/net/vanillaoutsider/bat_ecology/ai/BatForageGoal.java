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
 * Bat foraging goal with Lantern circuit pollination.
 * When near a Lantern, orbits and applies bonemeal to 9x9 area.
 */
public class BatForageGoal extends Goal {
    private final Mob mob;
    private final Random random = new Random();

    // Pollination state
    private BlockPos targetLantern = null;
    private int orbitTimer = 0;
    private int pollinationCooldown = 0;

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
        targetLantern = null;
        orbitTimer = 0;
    }

    /**
     * Calculate leader flight height based on colony size.
     * Uses sqrt scaling: height = 5 * sqrt(colonySize / 10)
     * 
     * Examples:
     * 10 bats → 5 blocks
     * 40 bats → 10 blocks
     * 90 bats → 15 blocks
     * 200 bats → 22 blocks
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

        int range = serverLevel.getGameRules().get(BatEcologyRules.BAT_ROOST_RANGE);
        int pollinateChance = serverLevel.getGameRules().get(BatEcologyRules.BAT_POLLINATE_CHANCE);

        // Get colony size for dynamic height
        int colonySize = 10; // default
        if (mob instanceof Bat bat) {
            BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm(bat, serverLevel);
            colonySize = swarm.colonySize();
        }
        double dynamicHeight = getLeaderHeight(colonySize);

        // --- Lantern Circuit Mode ---
        if (targetLantern != null) {
            // Check if still valid
            if (!serverLevel.getBlockState(targetLantern).is(Blocks.LANTERN) &&
                    !serverLevel.getBlockState(targetLantern).is(Blocks.SOUL_LANTERN)) {
                targetLantern = null;
                orbitTimer = 0;
            } else {
                // Orbit the lantern
                orbitTimer++;

                double angle = (orbitTimer * 0.15) % (Math.PI * 2);
                double orbitRadius = 2.0;
                double targetX = targetLantern.getX() + 0.5 + Math.cos(angle) * orbitRadius;
                double targetY = targetLantern.getY() + dynamicHeight; // Dynamic height based on colony
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

                // Pollination every 20 ticks (1 second)
                if (orbitTimer % 20 == 0 && pollinationCooldown <= 0) {
                    pollinate(serverLevel, targetLantern, pollinateChance);
                }

                // Leave after 40-60 ticks (2-3 seconds)
                if (orbitTimer > 40 + random.nextInt(20)) {
                    targetLantern = null;
                    orbitTimer = 0;
                    pollinationCooldown = 100; // 5 second cooldown before next pollination
                }

                return; // Don't do normal movement while orbiting
            }
        }

        // --- Scan for Lanterns ---
        if (mob.getRandom().nextInt(40) == 0 && targetLantern == null) {
            targetLantern = findNearbyLantern(serverLevel, range);
        }

        // --- Normal Foraging Movement ---
        if (mob.getRandom().nextInt(50) == 0) {
            double x = mob.getX() + (random.nextDouble() - 0.5) * range;
            double y = mob.getY() + (random.nextDouble() - 0.5) * (range / 2.0);
            double z = mob.getZ() + (random.nextDouble() - 0.5) * range;

            mob.getNavigation().moveTo(x, y, z, 1.2);
        }
    }

    /**
     * Find a nearby lantern block.
     */
    private BlockPos findNearbyLantern(ServerLevel level, int range) {
        BlockPos center = mob.blockPosition();
        int scanRange = Math.min(range, 16); // Limit scan to 16 blocks for performance

        for (int i = 0; i < 10; i++) { // 10 random samples
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
     * Apply pollination effect to 9x9 area below lantern.
     */
    private void pollinate(ServerLevel level, BlockPos lanternPos, int chance) {
        // Random chance check (1-100)
        if (random.nextInt(100) >= chance) {
            return;
        }

        // Scan 9x9 area (4 blocks in each horizontal direction)
        int radius = 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Find ground level
                for (int dy = 0; dy > -10; dy--) {
                    BlockPos checkPos = lanternPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.getBlock() instanceof CropBlock crop) {
                        // Apply growth if not fully grown
                        if (!crop.isMaxAge(state)) {
                            // 10% per-block chance to avoid massive instant growth
                            if (random.nextInt(10) == 0) {
                                level.levelEvent(2005, checkPos, 0); // Bonemeal particles
                                BlockState newState = crop.getStateForAge(
                                        Math.min(crop.getMaxAge(), crop.getAge(state) + 1));
                                level.setBlock(checkPos, newState, 2);
                            }
                        }
                        break; // Found ground, stop vertical scan
                    } else if (state.isSolid()) {
                        break; // Hit solid ground
                    }
                }
            }
        }
    }
}
