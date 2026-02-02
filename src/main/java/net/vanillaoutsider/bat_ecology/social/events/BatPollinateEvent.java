package net.vanillaoutsider.bat_ecology.social.events;

import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.ai.BatPollinateGoal;

public class BatPollinateEvent implements SocialEvent {

    @Override
    public String getId() {
        return "bat_ecology:pollinate";
    }

    @Override
    public int getPriorityValue() {
        return 60; // Lantern interaction is top priority
    }

    @Override
    public String getTrackId() {
        return "mood";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return true;
    }

    private BatPollinateGoal pollinateGoal;

    @Override
    public void onStart(TickContext context) {
        if (context.entity().dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            this.pollinateGoal = new BatPollinateGoal(bat);
            bat.goalSelector.addGoal(1, this.pollinateGoal); // Priority 1 since it's Mood High
        }
    }

    @Override
    public boolean tick(TickContext context) {
        return false; // Continuous while lantern nearby
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (this.pollinateGoal != null
                && entity.dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            bat.goalSelector.removeGoal(this.pollinateGoal);
        }
    }
}
