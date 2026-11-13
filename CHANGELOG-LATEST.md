### Added
- Background threading has been refactored and should now work more reliably.
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

### Removed
- Temporarily removed sidebar workstation display while refactors are ongoing.