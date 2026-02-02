package net.vanillaoutsider.bat_ecology.social.events;

import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.ai.BatHuntGoal;

public class BatHuntEvent implements SocialEvent {

    @Override
    public String getId() {
        return "bat_ecology:hunt";
    }

    @Override
    public int getPriorityValue() {
        return 50; // Higher than roost/forage
    }

    @Override
    public String getTrackId() {
        return "mood";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return true;
    }

    private BatHuntGoal huntGoal;

    @Override
    public void onStart(TickContext context) {
        if (context.entity().dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            this.huntGoal = new BatHuntGoal(bat);
            bat.goalSelector.addGoal(2, this.huntGoal);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        return false; // Continuous during night
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (this.huntGoal != null && entity.dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            bat.goalSelector.removeGoal(this.huntGoal);
        }
    }
}
