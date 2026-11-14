### Added
- Background threading has been refactored and should now work more reliably.
- Slots can now be added and bound in a client recipe class via `ReliableClientRecipe#addSlots`.
  - `ReliableClientRecipeType#placeSlots` and `ReliableClientRecipe#bindSlots` have been deprecated.
- Excluded items now accept and are based on predicates.
- Support for 26.4-snapshot-8.
  - Auto-completion for recipe serializers does not yet work.
  - Polymer support is not complete, Polymer does not yet support 26.4.
- `ClientRecipeManager` now allows querying recipes by key.
- New workstations can now be added to existing recipe types via `ItemView.addWorkstation`.

### Changed
- Recipe sync now uses Fabric's packet splitter, should resolve #93.

### Fixed
- Inventory changes now update the craftables page correctly.
- Moving `BlockingGuiComponent`s no longer cause lag and update correctly.
- JEI support now renders optional slots correctly.

### Removed
- Temporarily removed sidebar workstation display while refactors are ongoing.