### Added
- Side panel visibility can now be individually set rather than always following the Item View.
- Searching with filters now changes the colour of the text.
- Searching without a result now changes the colour of the searchbar.
- Various strings have been made more translatable.

### Changed
- Various recipes have been refactored to return `ItemStackTemplate`s rather than `ItemStack`s.
- Mod name filter now filters based on namespace rather than mod name.

### Fixed
- Tag search causing items to appear twice in the index.

### Removed
- Deprecated `SlotContent.Type` enum, use `ActionType` instead.
- Support for versions 1.21.8-1.21.11.