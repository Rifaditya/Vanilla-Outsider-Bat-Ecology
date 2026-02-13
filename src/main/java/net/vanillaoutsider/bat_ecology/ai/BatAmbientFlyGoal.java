package net.vanillaoutsider.bat_ecology.ai;

import net.dasik.social.ai.util.UniversalRandomPos;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;

public class BatAmbientFlyGoal extends Goal {
    private final Bat bat;

    public BatAmbientFlyGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.bat.isResting()
                && this.bat.getRandom().nextInt(10) == 0
                && !this.bat.isVehicle(); // Added vehicle check to stop fighting riders
    }

    @Override
    public boolean canContinueToUse() {
        return !this.bat.getNavigation().isDone() && !this.bat.isResting() && !this.bat.isVehicle();
    }

    @Override
    public void start() {
        @Nullable
        Vec3 target = this.findTarget();
        if (target != null) {
            this.bat.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
        }
    }

    @Nullable
    private Vec3 findTarget() {
        // Use UniversalRandomPos to find an AIR target (hovering logic)
        // Range: 8 horizontal, 4 vertical
        // Direction: Current looking direction
        // Height: 2-6 blocks above solid ground preferred (hoverMin=2, hoverMax=6)

        Vec3 viewVector = this.bat.getViewVector(0.0F);

        Vec3 hoverPos = UniversalRandomPos.getHoverPos(
                this.bat,
                8,
                4,
                viewVector.x,
                viewVector.z,
                (float) (Math.PI / 2),
                3,
                1);

        return hoverPos;
    }

    @Override
    public void tick() {
        // No manual velocity setting needed! Navigation handles it.
        // If stuck, we might want to stop? StandardAerialNavigation handles stuck
        // checks.
    }
}
