package com.vanillaoutsider.batecology.social.core;

import net.minecraft.world.entity.LivingEntity;

public abstract class SocialEvent {
    protected final LivingEntity entity;
    protected final int priority;

    public SocialEvent(LivingEntity entity, int priority) {
        this.entity = entity;
        this.priority = priority;
    }

    public abstract boolean canStart();

    public abstract void start();

    public abstract void tick();

    public abstract boolean isFinished();

    public abstract void end();
}
