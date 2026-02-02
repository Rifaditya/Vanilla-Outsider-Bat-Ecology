# Bat Ecology: User Guide

> "Make the night sky alive and your farms thrive."

## The Concept

Bats are no longer useless ambient mobs. They are living, swarming pollinators that can be farmed to automate crop growth.

## Mechanics

### 1. The Colony

Bats now form **Swarms**.

- **Visuals**: A large colony will have physically larger bats.
- **Leaders**: You can spot the Leader (often the largest) guiding the flock.

### 2. Farming with Lanterns (Pollination)

Bats are attracted to Light at night.

1. **Setup**: Place **Lanterns** or **Soul Lanterns** near your crops.
2. **Effect**: At night, bats will "orbit" the lanterns.
3. **Bonus**: While orbiting, they release "pollen/dust" that **Bonemeals** crops in a 9x9 area below the lantern.
4. **Strategy**: Create a "Bat Highway" of lanterns to automate your farm overnight.

### 3. Guano (Fertilizer)

Bats occasionally drop **Guano**.

- If it lands on crops/dirt, it acts as mild fertilizer.
- Rate controlled by GameRule: `bd_bat_guano_rate`.

### 4. Breeding

You can breed bats!

- **Item**: **Spider Eye**.
- **Method**: Feed two adult bats.
- **Growth**: Babies take 20 minutes to grow (influenced by Colony Size).

## Configuration

Admins can tweak these GameRules:

- `bd_bat_pollinate_chance`: Chance of bonemeal effect (Default: 100%).
- `bd_bat_swarm_max`: Max natural colony size (Default: 10).
