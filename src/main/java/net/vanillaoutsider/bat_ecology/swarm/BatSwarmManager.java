package net.vanillaoutsider.bat_ecology.swarm;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.bat_ecology.BatExtensions;

import java.util.*;

/**
 * Manages bat swarm behavior with leader election and boid-flocking for
 * followers.
 * Leader runs expensive scans; followers simply follow the leader.
 * Spread dynamically scales with colony size using sqrt math.
 */
public class BatSwarmManager {

    // Cache of swarms by area (chunk-based grouping)
    private static final Map<Long, SwarmData> SWARMS = new HashMap<>();

    // Base distances for 10 bats (reference colony size)
    private static final double BASE_MIN_DIST = 2.0; // Min distance before cohesion
    private static final double BASE_MAX_DIST = 16.0; // Max distance before separation
    private static final int BASE_COLONY_SIZE = 10;

    /**
     * Get or create swarm data for bats in the same area.
     */
    public static SwarmData getSwarm(Bat bat, ServerLevel level) {
        long chunkKey = bat.chunkPosition().pack();

        SwarmData swarm = SWARMS.get(chunkKey);
        if (swarm == null || swarm.isStale(level.getGameTime())) {
            swarm = rebuildSwarm(bat, level, chunkKey);
            SWARMS.put(chunkKey, swarm);
        }

        return swarm;
    }

    /**
     * Rebuild swarm data for a chunk area.
     */
    private static SwarmData rebuildSwarm(Bat bat, ServerLevel level, long chunkKey) {
        // Dynamic search radius - scales with expected colony size
        AABB searchBox = bat.getBoundingBox().inflate(64.0);
        List<Bat> nearbyBats = level.getEntitiesOfClass(Bat.class, searchBox, b -> {
            if (!(b instanceof BatExtensions))
                return false;
            return !((BatExtensions) b).bat_ecology$isBaby();
        });

        // Sort by UUID to deterministically elect leader
        nearbyBats.sort(Comparator.comparing(b -> b.getUUID().toString()));

        int colonySize = nearbyBats.size();
        Bat leader = nearbyBats.isEmpty() ? null : nearbyBats.getFirst();
        List<Bat> followers = nearbyBats.size() > 1 ? nearbyBats.subList(1, nearbyBats.size()) : List.of();

        return new SwarmData(leader, followers, colonySize, level.getGameTime());
    }

    /**
     * Calculate spread multiplier based on colony size.
     * Uses sqrt scaling: spread = base * sqrt(colonySize / 10)
     * 
     * Examples:
     * 10 bats → 1.0x spread (2-16 blocks)
     * 40 bats → 2.0x spread (4-32 blocks)
     * 90 bats → 3.0x spread (6-48 blocks)
     * 200 bats → 4.47x spread (9-72 blocks)
     */
    public static double getSpreadMultiplier(int colonySize) {
        if (colonySize <= BASE_COLONY_SIZE) {
            return 1.0;
        }
        return Math.sqrt((double) colonySize / BASE_COLONY_SIZE);
    }

    /**
     * Apply boid-flocking behavior for a follower bat with dynamic spread.
     */
    public static void applyFollowerBehavior(Bat follower, Bat leader, int colonySize) {
        if (leader == null || follower == leader) {
            return;
        }

        double dx = leader.getX() - follower.getX();
        double dy = leader.getY() - follower.getY();
        double dz = leader.getZ() - follower.getZ();

        double distSq = dx * dx + dy * dy + dz * dz;

        // Dynamic spread based on colony size
        double spreadMult = getSpreadMultiplier(colonySize);
        double minDistSq = (BASE_MIN_DIST * spreadMult) * (BASE_MIN_DIST * spreadMult);
        double maxDistSq = (BASE_MAX_DIST * spreadMult) * (BASE_MAX_DIST * spreadMult);

        // Only follow if not too close (cohesion) but not too far (separation)
        if (distSq > minDistSq && distSq < maxDistSq) {
            Vec3 movement = follower.getDeltaMovement();

            // Gentler steering for larger colonies to prevent chaos
            double strength = 0.05 / spreadMult;
            Vec3 newMovement = movement.add(
                    dx * strength,
                    dy * strength,
                    dz * strength);

            // Clamp velocity
            if (newMovement.lengthSqr() > 0.5 * 0.5) {
                newMovement = newMovement.normalize().scale(0.5);
            }

            follower.setDeltaMovement(newMovement);
        }
    }

    /**
     * Legacy method for backward compatibility.
     */
    public static void applyFollowerBehavior(Bat follower, Bat leader) {
        applyFollowerBehavior(follower, leader, BASE_COLONY_SIZE);
    }

    /**
     * Check if this bat is the swarm leader.
     */
    public static boolean isLeader(Bat bat, ServerLevel level) {
        SwarmData swarm = getSwarm(bat, level);
        return swarm.leader() == bat;
    }

    /**
     * Swarm data record with colony size.
     */
    public record SwarmData(Bat leader, List<Bat> followers, int colonySize, long createdAt) {
        public boolean isStale(long currentTime) {
            return currentTime - createdAt > 200; // Rebuild every 10 seconds
        }
    }
}
