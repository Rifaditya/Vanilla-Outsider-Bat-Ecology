# Bat Ecology

## Philosophy Fit

**Collection:** Vanilla Outsider (VO)
**Reasoning:**

- **Enhance, Don't Skip:** Enhances the natural behavior of bats without bypassing gameplay loops. It encourages the player to engage with the ecosystem (farming) rather than automating it away entirely.
- **Diegetic Design:** Reuses vanilla assets (Bone Meal particles from bees/crops) and uses natural behaviors (sleeping, flying).
- **1:1 Input/Output:** Bats digest food (inferred) and drop guano. It's a biological process, not magic.

## Mechanics

1. **The Hive Mind (SocialCoreMaster Integration):**
    - **Architecture:** Uses `EntitySocialScheduler` (from SocialCoreMaster) to manage states.
    - **Highlander Rule:** A single `GlobalSocialSystem` pulses the "Hive Mind" to save performance using `AtomicLong` guards.
    - **Behavior Tracks:**
        - **Mood (High Priority):** Roosting (Day), Hunting (Night), Swaming (Lantern nearby).
        - **Ambient (Low Priority):** Social Squeaks, Guano Drops.
        - **Mood (High Priority):** Roosting (Day), Hunting (Night), Orbiting (Lantern nearby).
        - **Ambient (Low Priority)::** Social Squeaks, Guano Drops.
    - **Leader-Follower:**
        - The Hive Mind lazily elects a "Leader" for each chunk/group.
        - **Optimization:** Only the **Leader** runs expensive logic (Lantern scanning, Crop searching). Followers just boid-flock to the Leader.

2. **Behavior Changes:**
    - **Daytime (Roosting):** Bats seek dark, solid ceilings. Logic checks `World.isDay()` and `!canSeeSky()`.
        - **Dynamic Home:** Roost spots are not permanent; bats pick any valid spot nearby.
        - **Condition:** Must not have skylight (indoors/caves).
    - **Nighttime (Hunting):**
        - **Exit Hiding:** Bats actively seek areas with skylight or open air to leave their roosts.
        - **Patrol Mode:** If no Lanterns, Leader orbits home range (Radius: `bd_bat_roost_range`).
        - **Lantern Orbit:** If Lanterns (tagged `minecraft:lanterns` or modded) exist, Leader plots path to orbit them.
    - **Mode B: Lantern Present (Orbit Mode)**
        - **Trigger:** When the Leader arrives at a Lantern.
        - **Orbiting:** The Leader (and swarm) will orbit the lantern tightly for **10 seconds**.
        - **Height Rule:** The swarm **maintains its 5-block height** while orbiting.
        - **Stacking:** Efficiency increases with more lanterns (more stops = more coverage).
    - **Flight Safety:**
        - **Height:** Preferences `Y + 5` blocks above ground (Soft Minimum). This is not a hard limit; they can fly higher if needed for targets, but will actively push up if below this threshold.
        - **Lava:** Standard `PathNodeType.LAVA` penalty increased to -1.0 (Blocked).

3. **Guano (Fertilizer):**
    - **Logic:** Social track event.
    - **Trigger**: Every night (`level.isNight()`).
    - **Interval**: Fixed every **10 seconds** (200 ticks).
    - **Condition**: No hard limit or probability.
    - **Effect**: Bonemeal block below AND plays `minecraft:happy_villager` particles.
    - **Config**: `bd_bat_guano_interval` (int, default 200).

4. **Breeding & Growth:**
    - **Manual Breeding:** Player feeds **Spider Eye** to 2 bats.
        - **Interaction:** Handled via `interactMob` Mixin.
        - **Result:** Spawns Baby Bat.
    - **Baby Bats:**
        - **Tech:** Uses `Attributes.SCALE` = 0.5 * colonyScale.
        - **Hitbox:** Dynamically resized.
        - **Growth:** Linear interpolation to 1.0 * colonyScale over 20 minutes.
    - **Colony Regulation:**
        - Manual breeding has no limit (player investment).
        - Auto-spawning ONLY occurs if `ColonySize < gamerule bd_bat_swarm_max` (Default 10).

5. **Colony Scaling (Dynamic):**
    - **Visual Growth:** Bats grow larger as colony grows.
        - **Formula:** `scale = baseScale * sqrt(colonySize / 10)`
        - **Cap:** Maximum 1.5x scale.
        - **Example:** 10 bats = 1.0x, 200 bats = 1.41x
    - **Dynamic Spread:** Larger colonies spread out more (2-16 blocks → 9-72 blocks for 200 bats).
    - **Dynamic Height:** Leader flies higher (5 blocks → 22 blocks for 200 bats).
    - **Optimization:** Uses swarm cache (10 sec rebuild), hysteresis prevents flicker.

## Configuration (GameRules)

All magic numbers are mapped to Native Game Rules (Protocol 12):

- `bd_bat_roost_range` (int, 64)
- `bd_bat_guano_rate` (int, 2)
- `bd_bat_swarm_max` (int, 10)
- `bd_bat_pollinate_chance` (int, 100)

## Assets Needed

- **None:** Purely code-driven using vanilla resources.

- **Particles:** `minecraft:happy_villager`.
- **Sound:** Vanilla Bat sounds (Pitch 1.5x for Babies).
