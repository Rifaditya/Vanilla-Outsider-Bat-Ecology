package net.vanillaoutsider.bat_ecology.social.events;

import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import java.util.WeakHashMap;

public class BatGuanoEvent implements SocialEvent {

    // Fix Shared State: Key is SocialEntity, Value is the tickCounter for that
    // specific entity.
    // WeakHashMap automatically cleans up entries when entities are garbage
    // collected.
    private final WeakHashMap<SocialEntity, Integer> entityCounters = new WeakHashMap<>();

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
            int stagger = Math.abs(bat.getUUID().hashCode()) % 200;
            entityCounters.put(context.entity(), stagger);
        } else {
            entityCounters.put(context.entity(), 0);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        SocialEntity socialEntity = context.entity();
        int tickCounter = entityCounters.getOrDefault(socialEntity, 0);
        tickCounter++;
        entityCounters.put(socialEntity, tickCounter);

        if (socialEntity.dasik$asEntity() instanceof Bat bat && !bat.level().isClientSide()) {
            if (bat.level() instanceof ServerLevel serverLevel) {
                // Feature 4 (Guano): Nightly Cycle
                long time = serverLevel.getOverworldClockTime() % 24000;
                boolean isNight = time >= 13000 && time < 23000;

                if (isNight) {
                    int interval = serverLevel.getGameRules().get(BatEcologyRules.BAT_GUANO_INTERVAL);

                    if (tickCounter >= interval) {
                        entityCounters.put(socialEntity, 0);

                        // Visual particles trail and Raycast
                        net.minecraft.core.BlockPos.MutableBlockPos cursor = bat.blockPosition().mutable();
                        boolean hitGround = false;

                        for (int i = 0; i < 20; i++) {
                            if (!serverLevel.getBlockState(cursor).isAir()) {
                                hitGround = true;
                                break;
                            }
                            // Visual trail
                            if (i % 2 == 0) {
                                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                        cursor.getX() + 0.5, cursor.getY() + 0.5, cursor.getZ() + 0.5,
                                        1, 0.1, 0.1, 0.1, 0.0);
                            }
                            cursor.move(0, -1, 0);
                        }

                        if (hitGround) {
                            net.minecraft.core.BlockPos ground = cursor.immutable();
                            net.minecraft.world.item.ItemStack boneMeal = new net.minecraft.world.item.ItemStack(
                                    net.minecraft.world.item.Items.BONE_MEAL);
                            if (net.minecraft.world.item.BoneMealItem.growCrop(boneMeal, serverLevel, ground)) {
                                serverLevel.levelEvent(2005, ground, 0); // Bonemeal sound/particles
                            }
                        }
                    }
                }
            }
        }
        return false; // Continuous ambient event
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        entityCounters.remove(entity);
    }
}
