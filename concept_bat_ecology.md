# Bat Ecology

## Philosophy Fit

**Collection:** Vanilla Outsider (VO)
**Reasoning:**

- **Enhance, Don't Skip:** Enhances the natural behavior of bats without bypassing gameplay loops. It encourages the player to engage with the ecosystem (farming) rather than automating it away entirely.
- **Diegetic Design:** Reuses vanilla assets (Bone Meal particles from bees/crops) and uses natural behaviors (sleeping, flying).
- **1:1 Input/Output:** Bats digest food (inferred) and drop guano. It's a biological process, not magic.

## Mechanics

1. **Behavior Changes:**
    - **Daytime (Roosting):** Bats seek dark, covered areas (caves, overhangs) to "sleep." They hang upside down and remain stationary unless disturbed.
    - **Nighttime (Hunting):** At dusk, bats wake up and fly out into the open sky.
    - **Swarm Intelligence (Optimization):**
        - **Leader-Follower System:** To save performance, only **one** "Leader Bat" per group actively scans for Lanterns.
        - **Followers:** All other bats pathfind to the Leader but maintain a **swarm radius** (don't stack on top of each other). They move like a cloud, not a line.
        - **Home Range:** Like Turtles, bats remember their "Home" (daytime roost). They will not travel further than **64 blocks** from home.
        - **Patrol Mode (Orbiting):** If **NO Lanterns** are found, the Leader enters "Patrol Mode."
            - It flies in a wide **360-degree orbit** around the Home position.
            - It utilizes the full **64-block range** to maximize coverage (footprint), effectively "scanning" the perimeter.
        - **Failover:** If the Leader dies or unloads, a new Leader is instantly promoted.
        - **Colony Maintenance (Event-Driven Optimization):**
            - **optimization:** Leader does NOT constantly check colony size.
            - **Trigger 1 (Member Death):** Dying bat "broadcasts" to Leader: *"I died, check count."* Leader checks size -> If < 5, queues a spawn for the next dawn/roost.
            - **Trigger 2 (Leader Death):** Leader dies -> New Leader promoted -> New Leader says *"I am leader, checking count"* -> If < 5, queues a spawn.
            - **Action:** Spawns **1 new bat** per day, ONLY when roosting at home (to prevent combat farming).
    - **Social Scheduler (Hive Mind Integration):**
        - **System:** Adopts the **Modular Scheduler** architecture from *Better Dogs* (`EntitySocialScheduler`, `SocialEvent`).
        - **Architecture (The "Highlander" Pattern):**
            - **Open Standard:** The system is designed to handle **ANY** mod that adopts this scheduler protocol, not just Vanilla Outsider mods.
            - **Election Logic:** At startup, the mods handshake. Only **ONE** Master Scheduler becomes active (usually the one with the highest internal engine version).
            - **Slave Mode:** All other instances disable their loop and register their entities to the elected Master.
            - **Result:** Third-party mods can simply copy this package to join the unified Hive Mind loop automatically.
        - **Event: Lone Bat (Isolation)**
            - **Trigger:** If a bat is flying **alone** (no neighbors within 10 blocks) AND is far from the Colony Leader/Home.
            - **Constraints:**
                - **Chance:** **10%** chance per night.
                - **Frequency:** Checks **once per night** (specifically when exiting the cave/roost).
            - **Priority:** HIGH (Interrupts standard flocking).
            - **Behavior:**
                - **Panic Search:** Increases flight speed by 50%.
                - **Echolocation:** Emits louder/faster squeaks (simulated) to "ping" for the colony.
                - **Goal:** Pathfinds directly to `HomePos` or nearest `Lantern` to regroup.
        - **Event: Late Flyer (Overslept)**
            - **Trigger:** Small random chance (**5%**) at dusk.
            - **Behavior:** Bat wakes up **1 minute (1200 ticks)** after the Leader/Colony has departed.
            - **Interaction:** The bat does **not** panic immediately. Instead, it **broadcasts** to the Social Scheduler: *"I am alone, requesting Lone Bat behavior."*
            - **Decision:** The Scheduler checks chances (10%) and constraints. If approved, it **injects** the Lone Bat event. If denied, the bat simply flies normally to catch up.
    - **Flight Rules:**
        - **Hazard Prevention (Lava Avoidance):** Bats explicitly **avoid** pathfinding through or near blocks tagged as `minecraft:fire` or `minecraft:lava`. This fixes the vanilla AI bug where they suicide into lava.
        - **Height Restriction:** When outside (seeing sky), bats must stay at least **5 blocks above the ground**. This prevents them from interfering with players and keeps them "in the sky."
        - **Attraction (Lantern Circuit):**
            - Bats detect **all** Lanterns within their 64-block Home Range.
            - **Routing:** The Leader creates a "Flight Path" visiting them sequentially:
                1. **Start:** Closest Lantern to Home.
                2. **Next:** Next closest, moving outwards.
                3. **End:** Farthest Lantern.
            - **Visitation:** Each lantern must be visited at least once per night (or cycle). Bats will not efficiently "camp" one lantern if others are ignored.

2. **Guano & Pollination Roles:**
    - **Mode A: No Lanterns (Patrol Mode)**
        - Bats flying in "Patrol Mode" will randomly drop Guano.
        - **Rate:** ~2 drops per bat per night (low efficiency, broad coverage).
        - **Effect:** Standard Bonemeal effect on the block below.

    - **Mode B: Lantern Present (Circuit Mode)**
        - **Trigger:** When the Leader arrives at a Lantern.
        - **Orbiting:** The Leader (and swarm) will orbit the lantern tightly for **1-2 seconds**.
        - **Pollination Burst:** During this orbit, the swarm activates a "Pollination Effect."
            - **Duration:** 2 seconds per Lantern visit.
            - **Effect:** Applies Bonemeal effect to crops/grass in the area (**9x9 radius** centered on the lantern).
            - **Height Rule:** The swarm **maintains its 5-block height** while orbiting. The effect is "projected" downwards (like a dusting), so they never fly near the ground or get in the player's way.
            - **Stacking:** Efficiency increases with more lanterns (more stops = more 2-second bursts across the farm).

    - **Asset Reuse:**
        - **Particle:** Use the vanilla "Happy Villager" particle (`minecraft:happy_villager`) like bonemeal/bees.
        - **Sound:** Standard bat sounds. No new sound files.

3. **Spawn Rates:**
    - **Colony Size:** Minimum **5-10 bats** per spawn event.
    - **Pack Spawning (Injection):**
        - **Logic:** When the game spawns a single bat (Vanilla rules), the mod intercepts.
        - **Check:** Is there enough **Air Volume** (3x3x3 space)? (Favors large caves).
        - **Action:** If yes, immediately force-spawns **4-9 extra bats** around the host to form an instant colony.
        - **Biome:** **Any** biome (Universal), respecting vanilla light/height levels.
    - **Colony Extinction (The "Last Bat" Rule):**
        - **Rule:** If a colony drops below **2 bats**, the **Automatic Regrowth** system is **DISABLED**. Auto-spawning requires a pair to "breeding" concept.
    - **Manual Breeding (Player Agency):**
        - **Mechanism:** Identical to vanilla animals.
        - **Item:** **Spider Eye**.
        - **Action:** Feed to 2 bats -> Love Mode -> **Baby Bat**.
        - **Cap:** **NONE.** The player can breed 100 bats if they want (and crash their game).
        - **Automatic Logic:** The system *only* automatically spawns new bats if `Count < 10` AND `Count >= 2`. If you have 50 bats, the Leader does nothing.
        - **Resurrection:** This effectively allows players to restart a dead colony by finding 2 survivors and breeding them manually.

## Assets Needed

- **None:** Purely code-driven using vanilla resources.
