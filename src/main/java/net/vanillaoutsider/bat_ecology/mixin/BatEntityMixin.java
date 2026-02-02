package net.vanillaoutsider.bat_ecology.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.bat_ecology.ai.BatFollowLeaderGoal;
import net.vanillaoutsider.bat_ecology.config.BatEcologyRules;
import net.dasik.social.core.EntitySocialScheduler;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.core.SocialRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager;

import java.util.List;

@Mixin(Bat.class)
public abstract class BatEntityMixin extends AmbientCreature
        implements SocialEntity, net.vanillaoutsider.bat_ecology.BatExtensions {

    protected BatEntityMixin(EntityType<? extends AmbientCreature> entityType, Level level) {
        super(entityType, level);
    }

    // ... (Fields omitted for brevity, assuming they are matched by context or I
    // should include them?)
    // I need to be careful with replace_file_content.
    // I already replaced imports.

    // I'll update the breed method casting first.

    // --- Fields ---

    @Unique
    private EntitySocialScheduler bat_ecology$scheduler;

    @Unique
    private String bat_ecology$species = "bat";

    @Unique
    private int bat_ecology$inLove;

    @Unique
    private java.util.UUID bat_ecology$loveCause;

    @Unique
    private int bat_ecology$breedCooldown;

    @Unique
    private int bat_ecology$age = 24000; // Default to adult; 0 = baby, 24000 = adult (20 min)

    @Unique
    private int bat_ecology$guanoTimer;

    @Unique
    private int bat_ecology$lastColonySize = 10; // For hysteresis check

    // --- Scheduler ---

    @Override
    public EntitySocialScheduler dasik$getScheduler() {
        if (this.bat_ecology$scheduler == null) {
            this.bat_ecology$scheduler = new EntitySocialScheduler(this);
            SocialRegistry.register(this);
        }
        return this.bat_ecology$scheduler;
    }

    @Override
    public String dasik$getSpeciesId() {
        return this.bat_ecology$species;
    }

    public void bat_ecology$setSocialSpecies(String species) {
        this.bat_ecology$species = species;
    }

    @Override
    public long dasik$getDNA() {
        return this.bat_ecology$loveCause != null ? this.bat_ecology$loveCause.getLeastSignificantBits()
                : this.random.nextLong();
    }

    @Override
    public net.minecraft.world.entity.LivingEntity dasik$asEntity() {
        return (net.minecraft.world.entity.LivingEntity) (Object) this;
    }

    @Override
    public float dasik$getSocialScale() {
        return 1.0f;
    }

    // --- SocialEntity Implementation ---

    @Override
    public void bat_ecology$setInLove(int ticks) {
        this.bat_ecology$inLove = ticks;
    }

    @Override
    public boolean bat_ecology$isInLove() {
        return this.bat_ecology$inLove > 0;
    }

    @Override
    public void bat_ecology$setBaby(boolean isBaby) {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        if (scale != null) {
            scale.setBaseValue(isBaby ? 0.5 : 1.0);
        }
        // Reset age when set as baby
        if (isBaby) {
            this.bat_ecology$age = 0;
        } else {
            this.bat_ecology$age = 24000;
        }
    }

    @Override
    public boolean bat_ecology$isBaby() {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        return scale != null && scale.getBaseValue() < 1.0;
    }

    /**
     * Baby bats have 1.5x higher pitch for squeaks (Concept L80).
     */
    @Override
    public float getVoicePitch() {
        return this.bat_ecology$isBaby() ? 1.5F : super.getVoicePitch();
    }

    @Override
    public void bat_ecology$setBreedCooldown(int ticks) {
        this.bat_ecology$breedCooldown = ticks;
    }

    // --- Ticking & Logic ---

    @Unique
    private boolean bat_ecology$isHoldingTemptItem(Player player) {
        return player.getMainHandItem().is(net.minecraft.world.item.Items.SPIDER_EYE)
                || player.getOffhandItem().is(net.minecraft.world.item.Items.SPIDER_EYE);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void bat_ecology$registerGoals(CallbackInfo ci) {
        // Add Follow Leader goal for non-leaders (Concept L20-22)
        this.goalSelector.addGoal(3, new BatFollowLeaderGoal((Bat) (Object) this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // === Love Timer ===
        if (this.bat_ecology$inLove > 0) {
            this.bat_ecology$inLove--;

            // Client-side heart particles while in love
            if (this.level().isClientSide() && this.bat_ecology$inLove % 10 == 0) {
                double xa = this.random.nextGaussian() * 0.02;
                double ya = this.random.nextGaussian() * 0.02;
                double za = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART,
                        this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0),
                        xa, ya, za);
            }
        }

        // === Breed Cooldown ===
        if (this.bat_ecology$breedCooldown > 0) {
            this.bat_ecology$breedCooldown--;
        }

        // === Server-side Logic ===
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {

            // --- Baby Growth (Concept L58-61) ---
            if (this.bat_ecology$isBaby()) {
                this.bat_ecology$age++;

                // Interpolate scale: 0.5 → 1.0 over 24000 ticks (20 minutes)
                double progress = Math.min(1.0, this.bat_ecology$age / 24000.0);
                double newScale = 0.5 + (progress * 0.5);

                AttributeInstance scale = this.getAttribute(Attributes.SCALE);
                if (scale != null) {
                    scale.setBaseValue(newScale);
                }

                // Fully grown at 24000 ticks
                if (this.bat_ecology$age >= 24000) {
                    this.bat_ecology$setBaby(false);
                }
            }

            // --- Lava Avoidance (Concept L39) ---
            bat_ecology$avoidLava();

            // --- Colony Scaling (Visual/Attribute only) ---
            if (!this.bat_ecology$isBaby()) {
                BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm((Bat) (Object) this, serverLevel);

                // Colony-based scale update with hysteresis (±3)
                int colonySize = swarm.colonySize();
                if (Math.abs(colonySize - bat_ecology$lastColonySize) >= 3) {
                    bat_ecology$lastColonySize = colonySize;
                    int maxScalePermille = serverLevel.getGameRules().get(BatEcologyRules.BAT_MAX_COLONY_SCALE);
                    double maxScale = maxScalePermille / 100.0;
                    double colonyScale = Math.min(maxScale, Math.sqrt(colonySize / 10.0));
                    if (colonyScale < 1.0)
                        colonyScale = 1.0;

                    AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
                    if (scaleAttr != null && scaleAttr.getBaseValue() >= 1.0) {
                        scaleAttr.setBaseValue(colonyScale);
                    }
                }
            }

            // --- Temptation Logic ---
            Player nearestPlayer = this.level().getNearestPlayer(this, 10.0);
            if (nearestPlayer != null && bat_ecology$isHoldingTemptItem(nearestPlayer)) {
                double dx = nearestPlayer.getX() - this.getX();
                double dy = (nearestPlayer.getY() + nearestPlayer.getEyeHeight()) - this.getY();
                double dz = nearestPlayer.getZ() - this.getZ();

                double distSq = dx * dx + dy * dy + dz * dz;
                if (distSq > 2.5 * 2.5) {
                    Vec3 movement = this.getDeltaMovement();
                    Vec3 newMovement = movement.add(
                            (Math.signum(dx) * 0.5 - movement.x) * 0.15,
                            (Math.signum(dy) * 0.7 - movement.y) * 0.15,
                            (Math.signum(dz) * 0.5 - movement.z) * 0.15);
                    this.setDeltaMovement(newMovement);

                    if (((Bat) (Object) this).isResting()) {
                        ((Bat) (Object) this).setResting(false);
                    }
                }
            }

            // --- Breeding Logic ---
            if (this.bat_ecology$inLove > 0 && this.bat_ecology$breedCooldown <= 0 && !this.bat_ecology$isBaby()) {
                bat_ecology$tryBreed();
            }
        }
    }

    /**
     * Avoid lava by applying upward/away velocity when near lava blocks.
     */
    @Unique
    private void bat_ecology$avoidLava() {
        BlockPos pos = this.blockPosition();
        int range = 3;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (this.level().getBlockState(checkPos).is(Blocks.LAVA)) {
                        // Calculate direction away from lava
                        double awayX = this.getX() - (checkPos.getX() + 0.5);
                        double awayY = this.getY() - (checkPos.getY() + 0.5);
                        double awayZ = this.getZ() - (checkPos.getZ() + 0.5);

                        double dist = Math.sqrt(awayX * awayX + awayY * awayY + awayZ * awayZ);
                        if (dist > 0) {
                            // Normalize and apply strong avoidance
                            double strength = 0.3 / dist;
                            Vec3 current = this.getDeltaMovement();
                            this.setDeltaMovement(current.add(
                                    awayX * strength,
                                    Math.max(0.1, awayY * strength), // Always push up a bit
                                    awayZ * strength));
                        }

                        // Wake up if resting near lava
                        if (((Bat) (Object) this).isResting()) {
                            ((Bat) (Object) this).setResting(false);
                        }
                        return; // One lava block is enough to react
                    }
                }
            }
        }
    }

    /**
     * Find a nearby bat in love and spawn a baby bat.
     */
    @Unique
    private void bat_ecology$tryBreed() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB searchBox = this.getBoundingBox().inflate(8.0);
        List<Bat> nearbyBats = serverLevel.getEntitiesOfClass(Bat.class, searchBox, bat -> {
            if (bat == (Object) this)
                return false;
            // Use BatExtensions for checking love/baby status
            if (!(bat instanceof net.vanillaoutsider.bat_ecology.BatExtensions social))
                return false;
            if (!social.bat_ecology$isInLove())
                return false;
            if (social.bat_ecology$isBaby())
                return false;
            return this.distanceToSqr(bat) < 9.0;
        });

        if (!nearbyBats.isEmpty()) {
            Bat partner = nearbyBats.getFirst();
            net.vanillaoutsider.bat_ecology.BatExtensions partnerSocial = (net.vanillaoutsider.bat_ecology.BatExtensions) partner;

            Bat baby = EntityType.BAT.create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
            if (baby != null) {
                baby.snapTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);

                if (baby instanceof net.vanillaoutsider.bat_ecology.BatExtensions babySocial) {
                    babySocial.bat_ecology$setBaby(true);
                }

                serverLevel.addFreshEntityWithPassengers(baby);

                // Reset parents
                this.bat_ecology$inLove = 0;
                partnerSocial.bat_ecology$setInLove(0);
                this.bat_ecology$breedCooldown = 6000;
                partnerSocial.bat_ecology$setBreedCooldown(6000); // Use interface method instead of mixin cast

                // Heart particles
                serverLevel.broadcastEntityEvent((Bat) (Object) this, (byte) 18);
                serverLevel.broadcastEntityEvent(partner, (byte) 18);
            }
        }
    }

    /**
     * Handle entity events - byte 18 = heart particles
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 18) {
            for (int i = 0; i < 7; i++) {
                double xa = this.random.nextGaussian() * 0.02;
                double ya = this.random.nextGaussian() * 0.02;
                double za = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART,
                        this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0),
                        xa, ya, za);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        if (!this.level().isClientSide() && hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            net.minecraft.world.item.ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(net.minecraft.world.item.Items.SPIDER_EYE)) {
                if (this.bat_ecology$inLove <= 0 && !this.bat_ecology$isBaby() && this.bat_ecology$breedCooldown <= 0) {
                    this.bat_ecology$inLove = 600;
                    this.bat_ecology$loveCause = player.getUUID();
                    this.level().broadcastEntityEvent((Bat) (Object) this, (byte) 18);
                    itemStack.shrink(1);
                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    // --- Persistence ---

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void bat_ecology$saveBrain(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("BatInLove", this.bat_ecology$inLove);
        compound.putInt("BatBreedCooldown", this.bat_ecology$breedCooldown);
        compound.putInt("BatAge", this.bat_ecology$age);
        compound.putInt("BatGuanoTimer", this.bat_ecology$guanoTimer);
        if (this.bat_ecology$loveCause != null) {
            compound.putIntArray("BatLoveCause", UUIDUtil.uuidToIntArray(this.bat_ecology$loveCause));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void bat_ecology$loadBrain(CompoundTag compound, CallbackInfo ci) {
        // bat_ecology$getScheduler().load(compound);
        this.bat_ecology$inLove = compound.getInt("BatInLove").orElse(0);
        this.bat_ecology$breedCooldown = compound.getInt("BatBreedCooldown").orElse(0);

        if (compound.contains("BatAge")) {
            this.bat_ecology$age = compound.getInt("BatAge").orElse(24000);
        } else {
            this.bat_ecology$age = 24000;
        }
        this.bat_ecology$guanoTimer = compound.getInt("BatGuanoTimer").orElse(0);

        if (compound.contains("BatLoveCause")) {
            // Unwrap Optional<int[]> -> map to UUID -> orElse(null)
            this.bat_ecology$loveCause = compound.getIntArray("BatLoveCause")
                    .map(UUIDUtil::uuidFromIntArray)
                    .orElse(null);
        }

        // Restore scale from age if baby
        if (this.bat_ecology$age < 24000) {
            double progress = this.bat_ecology$age / 24000.0;
            double scale = 0.5 + (progress * 0.5);
            AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(scale);
            }
        }
    }
}
