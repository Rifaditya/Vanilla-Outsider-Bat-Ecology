package net.vanillaoutsider.bat_ecology.ai;

import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.bat_ecology.swarm.BatSwarmManager;
import net.vanillaoutsider.bat_ecology.BatExtensions;
import net.minecraft.server.level.ServerLevel;
import java.util.EnumSet;

public class BatFollowLeaderGoal extends Goal {
    private final Bat bat;

    public BatFollowLeaderGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(bat.level() instanceof ServerLevel level)) {
            return false;
        }
        // Baby bats always follow (handled in swarming logic?)
        // Or if we are a follower in a swarm
        // Check if we have a leader
        BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm(bat, level);
        Bat leader = swarm.leader();

        return leader != null && leader != bat && leader.isAlive();
    }

    @Override
    public void tick() {
        if (!(bat.level() instanceof ServerLevel level))
            return;

        BatSwarmManager.SwarmData swarm = BatSwarmManager.getSwarm(bat, level);
        Bat leader = swarm.leader();

        if (leader != null && leader.isAlive()) {
            BatSwarmManager.applyFollowerBehavior(bat, leader, swarm.colonySize());
        }
    }
}
