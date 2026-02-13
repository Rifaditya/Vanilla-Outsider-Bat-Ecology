package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal-based ambient flying behavior for Bats.
 * Converts vanilla hardcoded customServerAiStep() AI into a proper Goal
 * that can be overridden by higher-priority goals (tempt, breed, follow).
 *
 * <pre>
 * ============================================
 * [GUIDE - DO NOT DELETE]
 * This goal has LOW priority (e.g. 10) so that:
 * - TemptGoal (priority 2) overrides it
 * - BreedGoal (priority 3) overrides it
 * - FollowLeaderGoal (priority 4) overrides it
 * See: Doc/Develop/profile_guide.md
 * ============================================
 * </pre>
 */
public class BatAmbientFlyGoal extends Goal {

    private final Bat bat;
    private BlockPos targetPosition;

    public BatAmbientFlyGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Only use when bat is NOT resting
        return !bat.isResting();
    }

    @Override
    public boolean canContinueToUse() {
        return !bat.isResting();
    }

    @Override
    public void start() {
        this.targetPosition = null;
    }

    @Override
    public void tick() {
        if (!(bat.level() instanceof ServerLevel level))
            return;

        // Clear invalid target
        if (targetPosition != null
                && (!level.isEmptyBlock(targetPosition) || targetPosition.getY() <= level.getMinY())) {
            targetPosition = null;
        }

        // Pick new random target
        if (targetPosition == null || bat.getRandom().nextInt(30) == 0
                || targetPosition.closerToCenterThan(bat.position(), 2.0)) {
            targetPosition = BlockPos.containing(
                    bat.getX() + (double) bat.getRandom().nextInt(7) - (double) bat.getRandom().nextInt(7),
                    bat.getY() + (double) bat.getRandom().nextInt(6) - 2.0,
                    bat.getZ() + (double) bat.getRandom().nextInt(7) - (double) bat.getRandom().nextInt(7));
        }

        // Move towards target
        double dx = (double) targetPosition.getX() + 0.5 - bat.getX();
        double dy = (double) targetPosition.getY() + 0.1 - bat.getY();
        double dz = (double) targetPosition.getZ() + 0.5 - bat.getZ();
        Vec3 movement = bat.getDeltaMovement();
        Vec3 newMovement = movement.add(
                (Math.signum(dx) * 0.5 - movement.x) * 0.1F,
                (Math.signum(dy) * 0.7F - movement.y) * 0.1F,
                (Math.signum(dz) * 0.5 - movement.z) * 0.1F);
        bat.setDeltaMovement(newMovement);

        // Rotate towards movement direction
        float yRotD = (float) (Mth.atan2(newMovement.z, newMovement.x) * 180.0F / (float) Math.PI) - 90.0F;
        float rotDiff = Mth.wrapDegrees(yRotD - bat.getYRot());
        bat.zza = 0.5F;
        bat.setYRot(bat.getYRot() + rotDiff);

    }
}
