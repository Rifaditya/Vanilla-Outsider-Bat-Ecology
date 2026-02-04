# Changelog

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
