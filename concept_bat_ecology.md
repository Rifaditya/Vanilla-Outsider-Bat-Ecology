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
    - **Leader-Follower:**
        - The Hive Mind lazily elects a "Leader" for each chunk/group.
        - **Optimization:** Only the **Leader** runs expensive logic (Lantern scanning, Crop searching). Followers just boid-flock to the Leader.

2. **Behavior Changes:**
    - **Daytime (Roosting):** Bats seek dark, solid ceilings. Logic checks `World.isDay()` and `SkyLight`.
    - **Nighttime (Hunting):**
        - **Patrol Mode:** If no Lanterns, Leader orbits home range (Radius: `gamerule bd_bat_roost_range`, default 64).
        - **Circuit Mode:** If Lanterns exist, Leader plots path between them.
    - **Mode B: Lantern Present (Circuit Mode)**
        - **Trigger:** When the Leader arrives at a Lantern.
        - **Orbiting:** The Leader (and swarm) will orbit the lantern tightly for **1-2 seconds**.
        - **Pollination Burst:** During this orbit, the swarm activates a "Pollination Effect."
            - **Duration:** 2 seconds per Lantern visit.
            - **Effect:** Applies Bonemeal effect to crops/grass in the area (**9x9 radius** centered on the lantern).
            - **Height Rule:** The swarm **maintains its 5-block height** while orbiting. The effect is "projected" downwards (like a dusting), so they never fly near the ground or get in the player's way.
            - **Stacking:** Efficiency increases with more lanterns (more stops = more 2-second bursts across the farm).
    - **Flight Safety:**
        - **Height:** Maintains `Y + 5` above ground (Raycast check) to avoid player collision.
        - **Lava:** Standard `PathNodeType.LAVA` penalty increased to -1.0 (Blocked).

3. **Pollination (The "Dusting" Effect):**
    - **Trigger:** When Leader reaches a Lantern and orbits.
    - **Logic (Optimization):**
        - Runs **ONCE per second** (20 ticks).
        - Runs **ONLY on Leader**.
        - Effect: `World.playEvent` (Bonemeal) on random crops in 9x9 area.
    - **Config:** Chance defined by `gamerule bd_bat_pollinate_chance` (Default 100%).

4. **Guano (Fertilizer):**
    - **Logic:** Ambient track event.
    - **Rate:** Controlled by `gamerule bd_bat_guano_rate` (Default 2 per 1000 ticks).
    - **Effect:** Bonemeal block below.

5. **Breeding & Growth:**
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

6. **Colony Scaling (Dynamic):**
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
