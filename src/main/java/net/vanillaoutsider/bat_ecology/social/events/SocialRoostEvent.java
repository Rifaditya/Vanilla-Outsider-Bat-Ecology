package net.vanillaoutsider.bat_ecology.social.events;

import net.minecraft.world.entity.Mob;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.ai.BatRoostGoal;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;

public class SocialRoostEvent implements SocialEvent {
    private BatRoostGoal roostGoal;

    @Override
    public String getId() {
        return "bat_ecology:roost";
    }

    @Override
    public int getPriorityValue() {
        return 10;
    }

    @Override
    public String getTrackId() {
        return "mood";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return true;
    }

    @Override
    public void onStart(TickContext context) {
        if (context.entity().dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            this.roostGoal = new BatRoostGoal(bat);
            bat.goalSelector.addGoal(1, this.roostGoal);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        return false; // Continuous
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (this.roostGoal != null && entity.dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            bat.goalSelector.removeGoal(this.roostGoal);
        }
    }
}
