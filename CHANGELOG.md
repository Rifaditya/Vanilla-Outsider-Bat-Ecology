# Changelog

## [1.3.0] - 2026-01-31 (Colony Visual Scaling)

- **Feature**: Colony-based visual scaling - bats grow larger as colony grows (sqrt formula, capped by gamerule).
- **Feature**: New gamerule `bat_ecology:max_colony_scale` (100-200, default 150 = 1.5x max scale).
- **Feature**: Baby scale formula now uses colony scale (`0.5 * colonyScale` → `1.0 * colonyScale`).
- **Internal**: Hysteresis threshold (±3) prevents scale flickering when colony size changes.
- **Internal**: Added lang file with GameRule translations.

## [1.2.0] - 2026-01-31 (Dynamic Swarm Scaling)

- **Feature**: Dynamic swarm spread - larger colonies spread out more using sqrt scaling (10 bats = 2-16 blocks, 200 bats = 9-72 blocks).
- **Feature**: Dynamic leader height - leaders fly higher above ground as colony grows (10 bats = 5 blocks, 200 bats = 22 blocks).
- **Internal**: Removed swarm size cap - all bats in a colony now follow the leader with dynamic spread.
- **Internal**: Steering strength scales inversely with colony size for smoother large swarms.

## [1.1.1] - 2026-01-31 (Bug Fixes)

- **Fix**: Spawn eggs now spawn adult bats instead of babies (fixed default age initialization).
- **Fix**: Updated `SocialEvent` interface from deprecated `CompoundTag` to `ValueInput`/`ValueOutput` API.
- **Feature**: Baby bats now squeak at 1.5x higher pitch than adults (Concept L80).

## [1.1.0] - 2026-01-31 (Feature Completion)

- **Feature**: Baby bats now grow over 20 minutes with smooth scale interpolation (0.5 → 1.0).
- **Feature**: Guano drops - flying bats periodically apply bonemeal effect below them (rate configurable via GameRule).
- **Feature**: Lava avoidance - bats detect lava within 3 blocks and flee, waking up if resting.
- **Feature**: Lantern pollination - bats orbit lanterns and apply bonemeal to 9x9 crop area below.
- **Feature**: Leader-follower swarm AI - bats elect a leader per chunk and followers boid-flock toward leader.
- **Internal**: New `BatSwarmManager` class for swarm behavior.
- **Internal**: Age and guanoTimer persisted to NBT.

## [1.0.4] - 2026-01-31

- **Fix**: Heart particles now display when feeding bats Spider Eye (added `handleEntityEvent` override).
- **Fix**: Breeding now works - two bats in love mode will spawn a baby bat when close together.
- **Feature**: Continuous heart particles spawn while bat is in love mode.
- **Feature**: Baby bats spawn at 50% scale and cannot breed.
- **Feature**: Added 5-minute breed cooldown after successful breeding.

## [1.0.3] - 2026-01-31

- **Fix**: Changed breeding item from Glistering Melon Slice to Spider Eye (as per concept document).
- **Feature**: Added temptation behavior - bats now follow players holding Spider Eye within 10 blocks and wake up from resting when tempted.

## [1.0.2] - 2026-01-31

- **Fix**: Updated persistence API from deprecated `CompoundTag` to the new `ValueOutput`/`ValueInput` system in Minecraft 26.1.

## [1.0.1] - 2026-01-31

- **Fix**: Resolved critical startup crash on Minecraft 26.1 Snapshot 5. Replaced `@Inject` with `@Override` for `aiStep` and `mobInteract` in `BatEntityMixin` to comply with current mapping inheritance.
- **Fix**: Updated persistence API from deprecated `CompoundTag` to the new `ValueOutput`/`ValueInput` system in Minecraft 26.1.
- **Cleanup**: Removed unused Mixin imports and optimized internal logic.

## [1.0.0] - 2026-01-30

- Initial release for Minecraft 26.1 Snapshot.
- Added Bat social mechanics and hive mind integration.
- Implemented breeding behaviors and persistence.
