# Mechanics & Ecology

## The Cycle of the Bat

### 1. Diurnal Rhythms

Unlike vanilla bats which flail aimlessly 24/7, **Bat Ecology** imposes a circadian rhythm:

- **Daytime (06:00 - 18:00)**: Bats enter **Roosting State**. They seek dark ceilings (Solid Blocks) and hang upside down.
  - **Optimization**: Roosting bats have their AI ticked only once every 20 ticks. They do not pathfind.
  - **Regeneration**: Roosting heals the bat.
- **Nighttime (18:00 - 06:00)**: Bats wake up. If they are in a cave, they fly toward the sky.

### 2. Colony Dynamics

Bats are social creatures.

- **Pack Spawning**: When a bat spawns in a large cave, it automatically triggers a "Pack Spawn," creating 4-9 companions.
- **Leader System**: Every colony elects a Leader.
  - The Leader performs complex pathfinding (checking for Lanterns, avoiding lava).
  - Followers simply boid-flock to the Leader. This drastically reduces CPU usage compared to 10 individual pathfinders.
- **Home Range**: A colony remembers its daytime roost. They will not stray further than 64 blocks from home.

## Utility Mechanics

### Guano (Passive)

While flying in "Patrol Mode" (no nearby Lanterns), bats have a chance to drop Guano.

- **Effect**: Applies a Bone Meal event to the block directly below the bat.
- **Rate**: Low. Good for slow, passive growth of natural flora.

### Pollination (Active)

Players can guide swarms using **Lanterns**.

1. Place a Lantern near your crops.
2. Ensure the Lantern is within 64 blocks of a Bat Colony (e.g., a nearby cave entrance).
3. At night, the Leader detects the Lantern and guides the swarm to it.
4. **Pollination Burst**: As the swarm orbits the Lantern, they generate a high-potency "dusting" effect.
    - **Radius**: 9x9 blocks centered on the Lantern.
    - **Duration**: 2-5 seconds per visit.
    - **Effect**: Rapidly advances crop growth stages.

## Technical Details for Modpackers

- **Entity**: `minecraft:bat` (Mixin-based). No new entities.
- **Compatibility**: Compatible with most biome mods.
- **Performance**: Designed with the "1KB Haiku" philosophy. Heavy logic is offloaded to the Leader or suspended during roosting.
