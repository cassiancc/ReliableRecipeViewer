### Added
- Support for 26.1-snapshot-3.
- Advanced filtering options, allowing for the item view to be filtered by name, mod, and tag at the same time.
- The item view index is now based on the creative inventory, rather than all registered items. This can be reverted in the config.
- A recipe type for repairing items.
- A resource-pack driven recipe type for world interaction recipes.
- When hovering over a recipe type, you can now see its namespace, as well as its identifier with Advanced Tooltips enabled.
- API additions:
  - `ServerRecipeManager` and `ClientRecipeManager` now have helper methods to create a serialization context.
  - `ItemStackTemplate` can now be used directly in `SlotContent`, or easily converted to a normal `ItemStack` via `RrvTagUtil`.

### Changed
- Icon has been adjusted.

### Fixed
- "Show all recipes" hitbox extending into the previous recipe button.
- Bookmark panel not correctly switching to the right in wrap mode.
- Locator map recipes not displaying correctly.
- Infinite loop caused by item tag view.
