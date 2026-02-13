# Changelog

## v1.8.6

- **Refactor**: Switched pathfinding logic to use `StandardAerialNavigation` from `DasikLibrary`. This ensures consistent "air-only" flight behavior.

## v1.8.5

- **Fixed Pathfinding**: Bats now fly properly instead of crawling on the ground.
  - Removed vanilla Y-velocity drag (0.6 multiplier) that prevented upward flight.
  - Configured pathfinder to treat air and ground equally.

## v1.8.4

- **Fixed Flight**: Bats can now wake up from resting state (vanilla AI suppression is now conditional).
- **Improved Roosting**: Enabled `BatRoostGoal` for smarter roosting behavior (dark spots).
- **Cleanup**: Removed primitive roosting logic from `BatAmbientFlyGoal`.

## [1.8.3] - 2026-02-10

### Changed

- **Social Hooks**: Implemented `onSocialGoalStart()` override in `BatMixin` to call `setResting(false)` when social goals activate. This moves bat-specific behavior from the library to the consumer.
- **Cleanup**: Removed dead `bat_ecology$setSocialSpecies` method from `BatExtensions` interface.
- **Dependency**: Updated to `DasikLibrary` v1.6.3.

## [1.7.3] - 2026-02-07

### Fixed

- **Critical Crash Fix**: Resolved `StackOverflowError` on startup caused by infinite recursion in `BatMixin.createAttributes()`.

## [1.7.2] - 2026-02-06

### Added

- **AI Movement**: Integrated `FlyingMoveControl` and fixed `FLYING_SPEED` attribute for Bats.

### Fixed

- **AI Conflict**: Resolved issue where ambient behavior overrode social goals.

## [1.7.1] - 2026-02-06

### Fixed

- **Snapshot 6 Compatibility**: Updated dependency to `DasikLibrary` 1.5.1 to resolve the attribute-related crash on Bats.

## [1.7.0] - 2026-02-06

### Added

- Added Breeding Interest: Bats now look at and follow players holding Spider Eyes.
- Fixed Guano Drops: Resolved shared timer bug; each bat now drops guano independently.
- Updated Social Engine dependency to v1.5.0.
- Generalized AI Suppression: Bats now properly stop legacy flying AI when following players or breeding (via DasikLibrary).

## [1.6.8] - 2026-02-06

### Fixed

- **Startup Crash**: Removed redundant `@Overwrite mobInteract` in `BatMixin`. The Snapshot 6 signature change and base class logic are now handled exclusively by `DasikLibrary` v1.4.5 at the `Mob.interact` level.

## [1.6.7] - 2026-02-06

### Fixed

- **Snapshot 6 Alignment**: Stabilized interaction logic and verified compatibility with `DasikLibrary` v1.4.5.
- **Cleanup**: Internal verification of guano event ticking.

## [1.6.6] - 2026-02-05

### Fixed

- **Breeding Logic**: Fully migrated to `DasikLibrary` 1.4.1 core.
- **Cleanup**: Removed redundant `UniversalAgeable` implementation from `BatMixin` to avoid core conflicts.

## [1.6.5] - 2026-02-05

### Fixed

- **Critical Crash Fix**: Updated `BatMixin` persistence logic to match Snapshot 6 `ValueOutput`/`ValueInput` API (Replacing `CompoundTag`).

## [1.6.4] - 2026-02-05

### Fixed

- **Guano Logic**: Fully implemented Particle Trail + Raycast Bonemeal effect (Essential for Gameplay).
- **Time Sync**: Fixed `BatGuanoEvent` using `getOverworldClockTime()` for Snapshot 6.
- **Config**: Registered missing `BAT_POLLINATE_CHANCE` and `BAT_GUANO_INTERVAL` gamerules.

## [1.6.3] - 2026-02-05

### Fixed (Emergency Patch)

- **Crash Fix**: Resolved `AbstractMethodError` crash by implementing `UniversalAgeable` logic directly in `BatMixin`. Restores breeding and interaction stability.

## [1.6.2] - 2026-02-05

### Assets

- **Icon**: Updated mod icon to new 512x512 High-Res design.

## [1.6.1] - 2026-02-04

### Fixed

- **Breeding Integration**: Fully refactored `Bat` to implement `UniversalAgeable` logic for robust breeding.
- **Client Render**: Added `BatRendererMixin` to correctly scale baby bats (50% size).
- **Code Cleanup**: Removed duplicate methods in `BatExtensions` favoring library inheritance.

## [1.6.0] - 2026-02-04

### Changed

- **API Migrated**: Migrated internal `BatFollowLeaderGoal` to `DasikLibrary`'s `FollowLeaderGoal`.
- **Core Update**: Updated dependency to `DasikLibrary` 1.3.0.

## [1.5.2] - 2026-02-03

### Fixed

- Fixed critical issue where Bats could not be bred.
- Changed breeding item to **Spider Eye** (was incorrectly Glow Berries).
- Updated dependency to `DasikLibrary` 1.2.0 to support aging.
- Fixed Spawn Egg interaction on adults to spawn babies correctly.

## [1.5.1] - 2026-02-03

### Added

- Detailed descriptions for all Bat Ecology gamerules in `en_us.json`.

---
