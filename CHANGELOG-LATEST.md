### Added
- Utility methods on `RecipeScreenContext` for rendering tooltips.
- `ReliableClientRecipeType#getSlotCount` is now calculated automatically, and no longer needs to be overridden.

### Changed
- Backported removal of `ReliableClientRecipe#getViewType` to 26.2.

### Fixed
- On NeoForge, fluid item names are now pulled from fluid types.
- On NeoForge, some recipes involving fluid items not working correctly.