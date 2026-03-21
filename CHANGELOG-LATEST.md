### Added
- Support for Fabric and NeoForge's Data Component Ingredients.
- Side panel visibility can now be individually set rather than always following the Item View.
- Searching with filters now changes the colour of the text.
- Searching without a result now changes the colour of the searchbar.
- Various strings have been made more translatable.

### Changed
- Recipe types now serialize a `SlotContent` rather than a raw `Ingredient` or `ItemStack`. This allows for a more flexible and smooth experience when creating and serializing modded content.
- Mod name filter now filters based on namespace rather than mod name.
- On NeoForge, integrations are now processed  after the registry is frozen (in `FMLCommonSetupEvent`) to guarantee modded items exist.
- Fluid stack rendering now uses smarter 26.1 exclusive logic (Fixes #15)

### Fixed
- Tag search causing items to appear twice in the index.

### Removed
- Deprecated `SlotContent.Type` enum, use `ActionType` instead.
- Support for versions 1.21.8-1.21.11.