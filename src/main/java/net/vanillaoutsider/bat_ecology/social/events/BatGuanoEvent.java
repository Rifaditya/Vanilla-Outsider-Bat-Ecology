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
        if (context.entity().dasik$asEntity() instanceof Bat bat) {
            // Stagger based on UUID to prevent global sync
            // Default 200 checks if rule missing, actual rule read in tick()
            int stagger = Math.abs(bat.getUUID().hashCode()) % 200;
            tickCounter = stagger;
        } else {
            tickCounter = 0;
        }
    }

    @Override
    public boolean tick(TickContext context) {
        tickCounter++;

        if (context.entity().dasik$asEntity() instanceof Bat bat && !bat.level().isClientSide()) {
            if (bat.level() instanceof ServerLevel serverLevel) {
                // Feature 4 (Guano): Nightly Cycle (Zenith Protocol 1.4.0)
                long time = serverLevel.getOverworldClockTime() % 24000;
                boolean isNight = time >= 13000 && time < 23000;

                if (!isNight) {
                    return false; // Only drop guano at night
                }

                int interval = serverLevel.getGameRules().get(BatEcologyRules.BAT_GUANO_INTERVAL);

                if (tickCounter >= interval) {
                    tickCounter = 0;
                    // Find block below and apply bonemeal effect
                    net.minecraft.core.BlockPos below = bat.blockPosition().below();
                    if (serverLevel.getBlockState(below).isSolid()) {
                        serverLevel.levelEvent(2005, below, 0); // Bonemeal sound/particles
                        // Visual feedback: Happy Villager particles (Green sparkles)
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, bat.getX(), bat.getY(), bat.getZ(), 5,
                                0.2,
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
