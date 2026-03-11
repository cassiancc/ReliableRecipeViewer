### Added
- Side panel visibility can now be individually set rather than always following the Item View.
- Various strings have been made more translatable.

### Changed
- Various recipes have been refactored to return `ItemStackTemplate`s rather than `ItemStack`s.

### Fixed
- Tag search causing items to appear twice in the index.

### Removed
- Deprecated `SlotContent.Type` enum, use `ActionType` instead.
- Support for versions 1.21.8-1.21.11.