package com.vanillaoutsider.batecology.ai;

import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;

public class BatMoveControl extends MoveControl {
    private final Bat bat;

    public BatMoveControl(Bat bat) {
        super(bat);
        this.bat = bat;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            // Chunk Tether (Void Suicide Fix)
            if (!this.bat.level().hasChunkAt(new net.minecraft.core.BlockPos((int)this.wantedX, (int)this.wantedY, (int)this.wantedZ))) {
                this.operation = Operation.WAIT;
                return; 
            }

            // Standard Move Logic...
            // For now, simpler than vanilla
            Vec3 target = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
            Vec3 velocity = target.subtract(this.bat.position()).normalize().scale(this.speedModifier);
            this.bat.setDeltaMovement(velocity);
            
            // Look at target
             double d0 = this.wantedX - this.bat.getX();
             double d1 = this.wantedZ - this.bat.getZ();
             float f = (float)(net.minecraft.util.Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
             this.bat.setYRot(this.rotlerp(this.bat.getYRot(), f, 90.0F));
        }
    }
}
