package net.vanillaoutsider.bat_ecology.social.events;

import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

public class BatGuanoEvent implements SocialEvent {

    private int tickCounter;

    @Override
    public String getId() {
        return "bat_ecology:guano";
    }

    @Override
    public int getPriorityValue() {
        return 10; // Ambient is low priority
    }

    @Override
    public String getTrackId() {
        return "ambient";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return true;
    }

    @Override
    public void onStart(TickContext context) {
        tickCounter = 0;
    }

    @Override
    public boolean tick(TickContext context) {
        tickCounter++;

        if (context.entity().dasik$asEntity() instanceof Bat bat && !bat.level().isClientSide()) {
            if (bat.level() instanceof ServerLevel serverLevel) {
                int guanoRate = serverLevel.getGameRules().get(BatEcologyRules.BAT_GUANO_RATE);
                int threshold = guanoRate > 0 ? (1000 / guanoRate) : 20000; // Logic from Mixin

                if (tickCounter >= threshold) {
                    tickCounter = 0;
                    // Find block below and apply bonemeal effect (Concept L52)
                    net.minecraft.core.BlockPos below = bat.blockPosition().below();
                    if (serverLevel.getBlockState(below).isSolid()) {
                        serverLevel.levelEvent(2005, below, 0);
                        // Also spawn actual particles at the bat
                        serverLevel.sendParticles(ParticleTypes.COMPOSTER, bat.getX(), bat.getY(), bat.getZ(), 5, 0.2,
                                0.2, 0.2, 0.0);
                    }
                }
            }
        }
        return false; // Continuous ambient event
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        // Cleanup
    }
}
