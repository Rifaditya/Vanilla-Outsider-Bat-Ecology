# Bat Ecology: Technical Architecture

## Overview

Bat Ecology enhances the vanilla Bat entity with complex social behaviors, swarming mechanics, and interaction with the environment (Lanterns, Crops).

## Core Systems

### 1. Social Integration (Hive Mind)

**Component**: `BatEntityMixin` + `DasikLibrary`
**Description**:

- Bats are full `SocialEntity` participants.
- **Tracks**:
  - `mood` (Priority 40-60): Handles Roosting, Hunting, Pollinating.
  - `ambient` (Priority 10): Handles Guano drops.
- **Events**:
  - `BatHuntEvent` (Night): Patrols or Circuits.
  - `BatPollinateEvent` (Night + Lantern): The "Crop Dusting" mechanic.
  - `SocialRoostEvent` (Day): Ceiling seeking.

### 2. Swarm Logic & Leader Election

**Component**: `BatSwarmManager`
**Pattern**: Boid Flocking + Lazy Leader
**Logic**:

- **Leader**: 1 bat per group runs heavy logic (scanning for lanterns, calculating paths).
- **Followers**: Remaining bats uses `BatFollowLeaderGoal` to mimic leader movement offsets.
- **Scaling**:
  - Uses `BatSwarmManager.getSwarm()` to calculate colony size.
  - Applies `Attributes.SCALE` based on `sqrt(colony_size)`.

### 3. Crop Dusting (Pollination)

**Component**: `BatPollinateGoal`
**Trigger**: Leader finds `LanternBlock`.
**Effect**:

- Swarm orbits lantern.
- Every 1s (20 ticks), applies Bonemeal effect to random blocks in 9x9 radius.
- Height is clamped to `Y+5` (concept-safe) to avoid cluttering player view.

## Configuration

Mapped to GameRules:

- `bd_bat_roost_range`
- `bd_bat_guano_rate`
- `bd_bat_swarm_max`
- `bd_bat_pollinate_chance`
