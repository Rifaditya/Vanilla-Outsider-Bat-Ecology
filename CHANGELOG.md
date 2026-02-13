# Changelog

## [1.8.8] - 2026-02-13

### Fixed

- **Gravity**: Enabled `hoversInPlace=true` in Bat movement control to prevent falling when idle.

## [1.8.7] - 2026-02-13

### Fixed

- **Pathing**: Refactored `BatAmbientFlyGoal` to use `UniversalRandomPos` for finding valid aerial targets, preventing ground walking.

## [1.8.6] - 2026-02-13

### Changed

- **Core Migration**: Migrated pathfinding logic to `StandardAerialNavigation` in `DasikLibrary`.

## [1.8.5] - 2026-02-13

### Fixed

- **Pathing**: Configured `PathNavigation` to penalize ground pathing (`WALKABLE` = 4.0F).

## [1.8.4] - 2026-02-13

### Fixed

- **Physics**: Removed custom vertical drag logic (`multiply(1.0, 0.6, 1.0)`) to let `FlyingMoveControl` handle flight.

## [1.8.3] - 2026-02-13

### Changed

- **AI**: Implemented `onSocialGoalStart` override to handle `setResting(false)` (Core Migration).
- **Cleanup**: Removed unused `bat_ecology$setSocialSpecies`.

## [1.8.2] - 2026-02-10

### Fixed

- **Baby Scaling**: Corrected baby bat scaling logic.

## [1.8.1] - 2026-02-10

### Added

- **Scaling**: Integrated `UniversalAgeable` scaling.

## [1.8.0] - 2026-02-04

### Added

- **Breeding**: First implementation of Bat Breeding system.
