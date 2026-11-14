### Added
- Background threading has been refactored and should now work more reliably.
- Slots can now be added and bound in a client recipe class via `ReliableClientRecipe#addSlots`.
  - `ReliableClientRecipeType#placeSlots` and `ReliableClientRecipe#bindSlots` have been deprecated.
  - Mods using these methods designed to integrate with RRV 8 remain compatible, though note that to port to 26.4 you will need to update your integration to RRV 9.
- Excluded items now accept and are based on predicates.
- Support for 26.4-snapshot-8.
  - Auto-completion for recipe serializers does not yet work.
  - Polymer support is not complete, Polymer does not yet support 26.4.
  - RRV 9 on 26.4 removes RRV 8 backwards compatibility to make the experience developing new mods and integrations on 26.4 smoother.
- `ClientRecipeManager` now allows querying recipes by key.
- New workstations can now be added to existing recipe types via `ItemView.addWorkstation`.

### Changed
- Recipe sync now uses Fabric's packet splitter, should resolve #93.

### Fixed
- Inventory changes now update the craftables page correctly.
- Moving `BlockingGuiComponent`s no longer cause lag and update correctly.
- JEI support now renders optional slots correctly.
- Updated JEI integration with compatibility with the latest beta builds.

### Removed
- Temporarily removed sidebar workstation display while refactors are ongoing.