package com.vanillaoutsider.batecology.social.core;

import net.minecraft.world.entity.LivingEntity;
import java.util.PriorityQueue;

public class EntitySocialScheduler {
    private final LivingEntity owner;
    private SocialEvent currentEvent; // Currently running event
    private final PriorityQueue<SocialEvent> eventQueue;

    public EntitySocialScheduler(LivingEntity owner) {
        this.owner = owner;
        this.eventQueue = new PriorityQueue<>((e1, e2) -> Integer.compare(e2.priority, e1.priority)); // Higher prio
                                                                                                      // first
    }

    public void tick() {
        if (currentEvent != null) {
            if (currentEvent.isFinished()) {
                currentEvent.end();
                currentEvent = null;
            } else {
                currentEvent.tick();
            }
        }

        // Try to start new event if idle or higher priority exists (simplified)
        if (currentEvent == null && !eventQueue.isEmpty()) {
            SocialEvent next = eventQueue.poll();
            if (next != null && next.canStart()) {
                currentEvent = next;
                currentEvent.start();
            }
        }
    }

    public void requestEvent(SocialEvent event) {
        eventQueue.offer(event);
    }
}
