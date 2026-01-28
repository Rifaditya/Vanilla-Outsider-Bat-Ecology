package com.vanillaoutsider.batecology.ai;

import com.vanillaoutsider.batecology.social.core.InternalBatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BatSwarmAI {
    private final Bat bat;
    private long lastPromotionCheck = 0;
    private static final int SEARCH_RADIUS = 16;

    public BatSwarmAI(Bat bat) {
        this.bat = bat;
    }

    public void tick() {
        if (bat.level().isClientSide())
            return;

        // 1. Day/Night Cycle - Uses isBrightOutside() in Snapshot 5
        boolean isDay = bat.level().isBrightOutside();

        if (isDay) {
            // Day Logic: Roost
            if (!bat.isResting() && bat.onGround()) {
                bat.setResting(true);
            }
            return;
        } else {
            if (bat.isResting())
                bat.setResting(false); // Wake up!
        }

        // 2. Swarm Logic (Only at Night)
        int colonyId = InternalBatRegistry.getColonyID(bat);
        if (colonyId == -1)
            return;

        Optional<Bat> leader = InternalBatRegistry.getLeader(colonyId, (ServerLevel) bat.level());

        if (leader.isEmpty() || !leader.get().isAlive()) {
            // Promote new leader if enough time passed
            if (bat.level().getGameTime() - lastPromotionCheck > 100) {
                InternalBatRegistry.promoteLeader(bat);
                lastPromotionCheck = bat.level().getGameTime();
            }
            return;
        }

        if (leader.get() == bat) {
            doLeaderLogic();
        } else {
            doFollowerLogic(leader.get());
        }
    }

    private void doLeaderLogic() {
        // Find Lanterns
        BlockPos pos = bat.blockPosition();
        BlockPos target = null;

        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-SEARCH_RADIUS, -5, -SEARCH_RADIUS),
                pos.offset(SEARCH_RADIUS, 5, SEARCH_RADIUS))) {
            if (bat.level().getBlockState(p).getBlock() instanceof LanternBlock) {
                target = p;
                break;
            }
        }

        if (target != null) {
            Vec3 targetVec = Vec3.atCenterOf(target).add(0, 5, 0); // Hover 5 blocks above
            moveTowards(targetVec, 0.5f);

            // Trigger Guano Task (handled via separate logic or pulse)
        }
    }

    private void doFollowerLogic(Bat leader) {
        double dist = bat.distanceToSqr(leader);
        if (dist > 400) { // 20 blocks
            // Teleport catch-up
            bat.teleportTo(leader.getX(), leader.getY(), leader.getZ());
        } else if (dist > 16) { // 4 blocks
            moveTowards(leader.position().add(2, 0, 2), 0.4f);
        }
    }

    private void moveTowards(Vec3 pos, float speed) {
        Vec3 dir = pos.subtract(bat.position()).normalize().scale(speed);
        bat.setDeltaMovement(dir);
    }

    public boolean shouldSuppressVanillaAI() {
        // Suppress random vanilla flight at night if in a colony
        return !bat.level().isBrightOutside() && InternalBatRegistry.getColonyID(bat) != -1;
    }
}
