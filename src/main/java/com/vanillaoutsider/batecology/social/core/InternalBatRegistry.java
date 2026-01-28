package com.vanillaoutsider.batecology.social.core;

import com.vanillaoutsider.batecology.access.BatEcologyEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ambient.Bat;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InternalBatRegistry {
    // Thread-safe concurrent set
    private static final Set<Bat> bats = ConcurrentHashMap.newKeySet();

    public static void register(Bat bat) {
        bats.add(bat);
    }

    public static void unregister(Bat bat) {
        bats.remove(bat);
    }

    public static int getColonyID(Bat bat) {
        if (bat instanceof BatEcologyEntity entity) {
            return entity.getColonyID();
        }
        return -1;
    }

    public static int getColonySize(int colonyId) {
        if (colonyId == -1)
            return 1;
        int count = 0;
        
        for (Bat b : bats) {
            if (getColonyID(b) == colonyId) {
                count++;
            }
        }
        return count;
    }

    public static Optional<Bat> getLeader(int colonyId, ServerLevel level) {
        if (colonyId == -1)
            return Optional.empty();

        for (Bat b : bats) {
            if (b.isAlive() && b.level() == level && getColonyID(b) == colonyId) {
                // For now, simple rule: First alive bat in list is leader
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }

    public static void promoteLeader(Bat bat) {
        // Simple logic for now: Registering automatically handles it via getLeader
    }

    public static void pulse(ServerLevel level) {
        if (bats.isEmpty())
            return;

        // Since ConcurrentHashMap doesn't support random access, we iterate.
        // For efficiency, we just grab the first valid one we see.
        for (Bat candidate : bats) {
            if (candidate != null && candidate.isAlive() && candidate.level() == level) {
                 // Future: Trigger scheduler events
                 // Break after finding one to simulate random pick without heavy conversion
                 break;
            }
        }
    }
}
