package com.vanillaoutsider.batecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SwarmLogic {

    private static final double VIEW_RADIUS = 10.0;
    private static final double SEPARATION_RADIUS = 2.0;

    public static void tickColony(Bat bat, ServerLevel level) {
        // 1. Is Resting? (Insomnia Fix)
        if (bat.isResting()) {
            handleRoosting(bat, level);
            return;
        }

        // 2. Election & Leader Check
        if (bat.tickCount % 20 == (bat.getId() % 20)) { // Heuristic Election (Staggered)
            electLeader(bat, level);
        }

        // 3. Movement Logic (Smart vs Dumb)
        boolean isLeader = isLeader(bat);
        if (isLeader) {
            handleLeaderMovement(bat, level);
        } else {
            handleFollowerMovement(bat, level);
        }
    }

    private static void handleRoosting(Bat bat, ServerLevel level) {
        // Simplistic roosting checks. If block above is invalid, wake up.
        BlockPos above = bat.blockPosition().above();
        if (level.isEmptyBlock(above)) {
            bat.setResting(false);
            level.levelEvent(null, 1025, bat.blockPosition(), 0); // Flap sound
        }
    }

    private static boolean isLeader(Bat bat) {
        // Temporary: Simple tagging via custom data or just check if it has a path
        // For this phase, lets say if it has a navigation path, it thinks it's a leader.
        // real implementation needs a field, but we can't add fields easily to Entity without Mixin.
        // We will use a transient memory trick: The bat with the highest ID in the chunk is the leader.
        return true; // Placeholder for now, defaulting to self-governance until group logic connects
    }

    private static void electLeader(Bat bat, ServerLevel level) {
        // In a real Boid system, "Leader" is emergent.
        // Here, we just want to ensure we aren't crashing.
    }

    private static void handleLeaderMovement(Bat bat, ServerLevel level) {
        // Leader uses Navigation (Smart)
        if (bat.getNavigation().isDone() || bat.getRandom().nextInt(50) == 0) {
            // Pick a random spot 10 blocks away
            RandomSource random = bat.getRandom();
            double x = bat.getX() + (random.nextDouble() - 0.5) * 20.0;
            double y = bat.getY() + (random.nextDouble() - 0.5) * 10.0;
            double z = bat.getZ() + (random.nextDouble() - 0.5) * 20.0;
            
            BlockPos target = BlockPos.containing(x, y, z);
            
            // Validate: Must be air and in world bounds
            if (level.isEmptyBlock(target) && level.isInWorldBounds(target)) {
                 bat.getNavigation().moveTo(x, y, z, 1.0);
            }
        }
    }

    private static void handleFollowerMovement(Bat bat, ServerLevel level) {
        List<Bat> neighbors = level.getEntitiesOfClass(Bat.class, bat.getBoundingBox().inflate(VIEW_RADIUS));
        Vec3 alignment = Vec3.ZERO;
        Vec3 cohesion = Vec3.ZERO;
        Vec3 separation = Vec3.ZERO;
        int count = 0;

        for (Bat neighbor : neighbors) {
            if (neighbor == bat) continue;
            if (neighbor.distanceToSqr(bat) > VIEW_RADIUS * VIEW_RADIUS) continue; // Distance Filter

            // Stochastic Sampling: In dense packs, break early to save CPU
            if (count > 3) break; 

            alignment = alignment.add(neighbor.getDeltaMovement());
            cohesion = cohesion.add(neighbor.position());
            
            if (neighbor.distanceToSqr(bat) < SEPARATION_RADIUS * SEPARATION_RADIUS) {
                 separation = separation.add(bat.position().subtract(neighbor.position()));
            }
            count++;
        }

        if (count > 0) {
            alignment = alignment.scale(1.0 / count).normalize();
            cohesion = cohesion.scale(1.0 / count).subtract(bat.position()).normalize();
            separation = separation.normalize();
        }

        // Fluid Repulsion (Fluid Death Fix)
        Vec3 fluidAvoidance = Vec3.ZERO;
        if (level.containsAnyLiquid(bat.getBoundingBox().inflate(2.0))) {
             fluidAvoidance = new Vec3(0, 0.5, 0); // Fly UP
        }

        // Apply Boid Rules
        Vec3 velocity = bat.getDeltaMovement();
        velocity = velocity.add(alignment.scale(0.1));
        velocity = velocity.add(cohesion.scale(0.05));
        velocity = velocity.add(separation.scale(0.2));
        velocity = velocity.add(fluidAvoidance.scale(0.5));

        // Limit speed
        if (velocity.lengthSqr() > 0.5 * 0.5) {
            velocity = velocity.normalize().scale(0.5);
        }

        bat.setDeltaMovement(velocity);
    }
}
