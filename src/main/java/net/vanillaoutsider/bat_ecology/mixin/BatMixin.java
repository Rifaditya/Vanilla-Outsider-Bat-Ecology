package net.vanillaoutsider.bat_ecology.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.vanillaoutsider.bat_ecology.config.BatEcologyConfig;
import net.vanillaoutsider.bat_ecology.BatExtensions;
import net.vanillaoutsider.bat_ecology.ai.BatAmbientFlyGoal;
import net.vanillaoutsider.bat_ecology.ai.BatRoostGoal;
import net.dasik.social.api.breeding.UniversalBreedGoal;
import net.dasik.social.api.breeding.UniversalTemptGoal;
import net.dasik.social.ai.navigation.StandardAerialNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bat.class)
public abstract class BatMixin extends net.minecraft.world.entity.ambient.AmbientCreature implements BatExtensions {

    protected BatMixin(EntityType<? extends net.minecraft.world.entity.ambient.AmbientCreature> entityType,
            net.minecraft.world.level.Level level) {
        super(entityType, level);
        this.moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, 20, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        // Use the Standard Aerial Navigation from DasikLibrary
        // This handles all the pathing logic (Air=0.0F, Ground=4.0F penalty, hazardous
        // blocks blocked)
        return new StandardAerialNavigation(this, level);
    }

    /**
     * Redirect the heavy vertical drag in Bat.tick() which multiplies Y velocity by
     * 0.6.
     * We limit it to standard air friction (0.91) or no change, allowing upwards
     * flight.
     */
    @Redirect(method = "tick", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private net.minecraft.world.phys.Vec3 batEcology$removeVerticalDrag(net.minecraft.world.phys.Vec3 instance,
            double x, double y, double z) {
        // Original was multiply(1.0, 0.6, 1.0) -> heavily penalized Y
        // We replace it with identity (no extra drag beyond standard physics)
        // or just let FlyingMoveControl handle it.
        return instance;
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void dasik$addBreedingGoal(EntityType<? extends Bat> type, net.minecraft.world.level.Level level,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.goalSelector.addGoal(1, new UniversalBreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new UniversalTemptGoal(this, 1.0));
        this.goalSelector.addGoal(5,
                new net.dasik.social.ai.goal.FollowLeaderGoal((net.minecraft.world.entity.Mob) (Object) this));
        this.goalSelector.addGoal(6, new BatRoostGoal((net.minecraft.world.entity.Mob) (Object) this));
        // Low priority so higher-priority goals (tempt, breed, follow) override it
        this.goalSelector.addGoal(10, new BatAmbientFlyGoal((Bat) (Object) this));
    }

    /**
     * Suppress vanilla hardcoded AI - we use goals instead.
     * This injection cancels the hardcoded flying logic so goals take over.
     */
    @org.spongepowered.asm.mixin.injection.Inject(method = "customServerAiStep", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    private void batEcology$suppressHardcodedAi(ServerLevel level, CallbackInfo ci) {
        // If resting, let vanilla AI run (it handles waking up checks)
        // If not resting (flying), cancel vanilla AI so our Goals take over
        if (!((Bat) (Object) this).isResting()) {
            ci.cancel();
        }
    }

    private Bat bat_ecology$leader;

    @Override
    public Bat getLeader() {
        return this.bat_ecology$leader;
    }

    @Override
    public void setLeader(Bat leader) {
        this.bat_ecology$leader = leader;
    }

    @Override
    public boolean hasLeader() {
        return this.bat_ecology$leader != null && this.bat_ecology$leader.isAlive();
    }

    @Override
    public int getGroupSize() {
        return net.dasik.social.core.group.GroupManager.countGroupSize((Bat) (Object) this, Bat.class, 64.0);
    }

    @Override
    public net.dasik.social.api.group.strategy.FlockingStrategy getFlockingStrategy() {
        return net.dasik.social.api.group.strategy.Strategies.AERIAL;
    }

    @Override
    public net.dasik.social.api.group.GroupParameters getGroupParameters() {
        return net.dasik.social.api.group.GroupParameters.DEFAULT_AERIAL;
    }

    @Override
    public void onSocialGoalStart() {
        ((Bat) (Object) this).setResting(false);
    }

    /**
     * @author Antigravity (Zenith Protocol)
     * @reason Add FLYING_SPEED for social AI movement and implement Colony
     *         Regulation
     */
    @Overwrite
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 6.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED, 0.4);
    }

    /**
     * @author Antigravity (Zenith Protocol)
     * @reason Implement Colony Regulation and Relaxed Rules for Spawn Multiplier
     */
    @Overwrite
    public static boolean checkBatSpawnRules(EntityType<Bat> type, LevelAccessor level, EntitySpawnReason spawnReason,
            BlockPos pos, RandomSource random) {

        int mult = BatEcologyConfig.getSpawnMult();
        boolean relaxed = mult > 1;

        if (pos.getY() >= level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY()) {
            return false;
        } else if (!relaxed && random.nextBoolean()) {
            return false;
        } else {
            int items = level.getMaxLocalRawBrightness(pos);
            int threshold = relaxed ? 7 : random.nextInt(4);
            if (items > threshold) {
                return false;
            } else {
                return !level.getBlockState(pos.below()).is(BlockTags.BATS_SPAWNABLE_ON) ? false
                        : Bat.checkMobSpawnRules(type, level, spawnReason, pos, random);
            }
        }
    }
}
