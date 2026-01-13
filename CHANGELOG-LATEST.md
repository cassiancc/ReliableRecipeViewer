### Added
- 26.1-snapshot-3 support. `ItemStackTemplate` can now be used directly in `SlotContent`, or easily converted to a normal `ItemStack` via `RrvTagUtil`.
- `ServerRecipeManager` and `ClientRecipeManager` now have helper methods to create a serialization context.
- Advanced filtering options, allowing for the item view to be filtered by name, mod, and tag at the same time.

### Changed
- Icon has been adjusted.

### Fixed
- "Show all recipes" hitbox extending into the previous recipe button.
- Bookmark panel not correctly switching to the right in wrap mode.