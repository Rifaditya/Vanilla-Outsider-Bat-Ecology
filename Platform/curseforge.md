# Vanilla Outsider: Bat Ecology

**Bat Ecology** is a comprehensive overhaul of the vanilla Bat entity, transforming it from a useless lag-particle into a complex, integrated part of the Minecraft ecosystem.

![Banner](https://i.imgur.com/placeholder.png)

## 🌙 The Night Shift

Vanilla bats are chaotic and purposeless. **Bat Ecology** introduces a strict circadian rhythm and social hierarchy.

### Roosting (Day)

During the day, bats seek out dark ceilings in caves or under overhangs. They hang upside down and **sleep**.

- **Performance**: While roosting, bat AI is virtually disabled (0 tick impact).
- **Immersion**: Walking into a cave instantly wakes the colony, causing a panic scatter event with unique sound design.

### Swarming (Night)

At dusk, the colony wakes. A designated **Leader** guides the swarm out of the cave to hunt.

- **Fluid Movement**: Bats flock together, avoiding the "lone wanderer" behavior of vanilla mobs.
- **Echolocation**: Isolated bats emit pitch-shifted squeaks to locate their colony.

## 🌱 Farming Utility

Bats are now a vital tool for sustainable agriculture.

### Guano

Flying bats passively drop **Guano**, a weak fertilizer that accelerates plant growth. It accumulates slowly in caves or under heavily trafficked flight paths.

### Pollination Mechanics

You can harness the swarm for rapid crop growth.

1. **Lure**: Place a **Lantern** near your crops.
2. **Attract**: Ensure a colony is nearby (within 64 blocks).
3. **Pollinate**: At night, the swarm will orbit the Lantern.
4. **Reward**: Their frantic movement releases a **Pollination Burst**, applying a potent Bone Meal effect to a 9x9 area.

## ⚙️ Technical

- **License**: MIT
- **Loader**: Fabric
- **Modpack Friendly**: Uses standard entities (`minecraft:bat`) mixed with custom AI. Safe to remove (existing bats revert to vanilla behavior).

---
*Part of the Vanilla Outsider Collection. We don't change the game; we just fill in the gaps.*
