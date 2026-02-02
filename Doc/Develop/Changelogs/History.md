# Bat Ecology History

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
