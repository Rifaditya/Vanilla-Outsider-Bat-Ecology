# Release History

## v1.8.7 - 2026-02-13

- **Bug Fix**: Rewrote ambient flight goal to respect aerial navigation constraints, fixing the ground movement bug.

## v1.8.6 - 2026-02-13

- **Core Migration**: Implemented `StandardAerialNavigation`. Logic for pathing is now centralized in DasikLibrary.

## v1.8.5 - 2026-02-13

- **Bug Fix**: Fixed "ground walking" behavior. Bats can now fly vertically and pathfind through air correctly.
  - Removed aggressive vanilla Y-velocity decay.
  - Updated `FlyingPathNavigation` configuration.

## v1.8.4 - 2026-02-13

- **Bug Fix**: Fixed issue where Bats would never wake up from resting state (conditional AI suppression).
- **Feature**: Enabled `BatRoostGoal` for better roost finding logic.
- **Refactor**: Cleaned up `BatAmbientFlyGoal`.

## v1.8.3 - 2026-02-10

- **Social Hooks**: Implemented `onSocialGoalStart()` override in `BatMixin` to call `setResting(false)` when social goals activate.
- **Cleanup**: Removed dead `bat_ecology$setSocialSpecies` from `BatExtensions`.
- **Dependency**: Updated to `DasikLibrary` v1.6.3.

## v1.7.3 - 2026-02-07

- **Critical Crash Fix**: Resolved `StackOverflowError` on startup caused by infinite recursion in `BatMixin.createAttributes()`.

## v1.7.2

- Fixed Bat AI movement by registering `FLYING_SPEED` and adding `FlyingMoveControl`.
- Resolved social goal suppression issues.

## [1.7.1] - 2026-02-06

### Fixed

- **Snapshot 6 Compatibility**: Dependency bump to `DasikLibrary` 1.5.1. Resolves `TEMPT_RANGE` attribute crash.

## [1.7.0] - 2026-02-06

### Added

- Added Breeding Interest: Bats now look at and follow players holding Spider Eyes.
- Fixed Guano Drops: Resolved shared timer bug; each bat now drops guano independently.
- Updated Social Engine dependency to v1.5.0.
- Generalized AI Suppression: Bats now properly stop legacy flying AI when following players or breeding (via DasikLibrary).

## [1.6.8] - 2026-02-06

### Fixed

- **Startup Crash**: Resolved `InvalidMixinException` by removing redundant `@Overwrite mobInteract`. Interaction logic is now fully managed by `DasikLibrary` v1.4.5 at the base `Mob` level, ensuring compatibility with Snapshot 6 signatures.

## [1.6.7] - 2026-02-06

### Fixed

- **Snapshot 6 Alignment**: Stabilized interaction logic and verified compatibility with `DasikLibrary` v1.4.5.
- **Cleanup**: Internal verification of guano event ticking.

## [1.6.6] - 2026-02-05

### Fixed

- **Breeding Core**: Redirected `mobInteract` to parent logic to allow `DasikLibrary`'s `interact` injection to handle breeding.
- **Library Integration**: Aligned with `DasikLibrary` v1.4.1+ for core breeding migration.

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

## [1.5.1] - 2026-02-03

### Added

- Detailed descriptions for all Bat Ecology gamerules in `en_us.json`.

### Added

- **Breeding**: Bats can now be bred using **Glow Berries**.
- **Baby Bats**: Added baby bats (50% smaller) via Universal Breeding system.
- **Spawn Eggs**: Using a spawn egg on a bat now spawns a baby bat.

## [1.4.2] - 2026-02-02

### Changed

- **Roosting**: Bats now enforce a "No Skylight" rule for roosting (seeking dark caves/indoors) and dynamic spot selection.
- **Hunting**: Bats actively seek skylight/open air when hunting at night to "get out" of hiding.
- **AI**: `BatRoostGoal` is now strictly Leader-driven to prevent swarm chaos.

## [1.3.5] - 2026-02-02

### Added

- Implemented "Soft Minimum Height" logic (5 blocks) for bat followers to avoid ground hugging.

## [1.3.4] - 2026-02-02

### Fixed

- Fixed crash due to persistence API changes (`CompoundTag` -> `ValueOutput`/`ValueInput`) in Minecraft 26.1 Snapshot 5.

## [1.3.3] - 2026-02-02

### Fixed

- Fixed startup crash due to missing `registerGoals` method in Bat entity (Mixin targeting error).

## [1.3.2]

- Initial release for 26.1 Snapshot 5.
