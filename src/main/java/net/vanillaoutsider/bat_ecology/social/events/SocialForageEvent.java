package net.vanillaoutsider.bat_ecology.social.events;

import net.minecraft.world.entity.Mob;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.ai.BatForageGoal;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;

public class SocialForageEvent implements SocialEvent {
    private BatForageGoal forageGoal;

    @Override
    public String getId() {
        return "bat_ecology:forage";
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
            this.forageGoal = new BatForageGoal(bat);
            bat.goalSelector.addGoal(1, this.forageGoal);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        return false; // Continuous
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (this.forageGoal != null && entity.dasik$asEntity() instanceof net.minecraft.world.entity.ambient.Bat bat) {
            bat.goalSelector.removeGoal(this.forageGoal);
        }
    }
}
