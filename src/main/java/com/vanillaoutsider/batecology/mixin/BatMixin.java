package com.vanillaoutsider.batecology.mixin;

import com.vanillaoutsider.batecology.access.BatEcologyEntity;
import com.vanillaoutsider.batecology.ai.BatMoveControl; // [NEW] Import
import com.vanillaoutsider.batecology.ai.BatSwarmAI;
import com.vanillaoutsider.batecology.ai.SwarmLogic; // [NEW] Import
import com.vanillaoutsider.batecology.social.core.InternalBatRegistry;
import com.vanillaoutsider.batecology.spawn.BatSpawningLogic;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Bat.class)
public abstract class BatMixin extends AmbientCreature implements BatEcologyEntity {

    @Override
    public BatSwarmAI getSwarmAI() {
        return this.swarmAI;
    }

    @Override
    public int getColonyID() {
        return this.entityData.get(COLONY_ID);
    }

    @Override
    public void setColonyID(int id) {
        this.entityData.set(COLONY_ID, id);
    }

    // Snapshot 5: Uses OPTIONAL_LIVING_ENTITY_REFERENCE instead of OPTIONAL_UUID
    @Unique
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> LEADER_REF = SynchedEntityData
            .defineId(Bat.class,
                    EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    @Unique
    private static final EntityDataAccessor<Integer> COLONY_ID = SynchedEntityData.defineId(Bat.class,
            EntityDataSerializers.INT);

    @Unique
    public BatSwarmAI swarmAI;

    protected BatMixin(EntityType<? extends AmbientCreature> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineBatEcologyData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(LEADER_REF, Optional.empty());
        builder.define(COLONY_ID, -1); // -1 = No Colony / Loner
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.entityData.get(COLONY_ID) != -1) {
            output.putInt("BatColonyID", this.entityData.get(COLONY_ID));
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(COLONY_ID, input.getIntOr("BatColonyID", -1));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initAI(EntityType type, Level level, CallbackInfo ci) {
        if (!level.isClientSide()) {
            this.moveControl = new BatMoveControl((Bat) (Object) this); // [NEW] Chunk Tether
            this.swarmAI = new BatSwarmAI((Bat) (Object) this);
            InternalBatRegistry.register((Bat) (Object) this);
            
            // Connect Spawning Logic (Legacy Check) - Only triggers if conditions met
            BatSpawningLogic.checkPackSpawn((Bat) (Object) this, level);
        }
    }
    

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickSwarm(CallbackInfo ci) {
        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            if (this.swarmAI != null) {
                this.swarmAI.tick();
            }
        }
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void swarmAiStep(ServerLevel level, CallbackInfo ci) {
        // Realism Override: Call SwarmLogic (Stateless Boids)
        SwarmLogic.tickColony((Bat)(Object)this, level);
        ci.cancel(); 
    }
    @Override
    protected net.minecraft.world.InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(net.minecraft.world.item.Items.SPIDER_EYE)) {
            if (!this.level().isClientSide()) {
                // Heal bat
                ((Bat)(Object)this).heal(2.0F); // 1 Heart
                
                // Construct particle/sound logic (Optional polish)
                 this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT.value(), 1.0F, 1.0F);

                // Consume item
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
            return net.minecraft.world.InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }
}
