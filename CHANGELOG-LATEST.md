### Added
- Port to 26.1-snapshot-2.
- Networking is now handled through loader APIs and will warn users upon connecting to incompatible servers.
- Users of split sources can now make use of `ReliableRecipeViewerClientPlugin` and the `rrv_client` entrypoint. This is currently functionally identical to the existing `ReliableRecipeViewerPlugin` and `rrv` entrypoint, but may change in the future as the mod is updated to better handle split sources.
- Added deprecated helpers for old EIV style methods to help point people towards the new API.
- NeoForge support has been re-enabled for 26.1-snapshot-2 and above.
- Bookmarks panel can now be hidden in the config.
- Items hidden from the index no longer appear in the item tag view.
- RRV now respects `c:hidden_from_recipe_viewers` for fluids, enchantments, and potions.
- Resource pack driven info recipes.
- Reworked documentation, the new docs can be found on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs).

### Changed
- `ItemView#registerClientRecipeWrapper` was renamed to `ItemView#addClientRecipeWrapper` for consistency with other methods in `ItemView`. The old method is retained to continue supporting mods designed for 6.0.4 and below.
- Further internal refactors to ensure side safety by moving internal integrations that rely on clientside code to `BuiltInReliableRecipeViewerClientIntegration` and moving item model code to a client package.

### Fixed
- Incorrect tag translations.
- Crash with VoxelMap.
- Performance issues when rendering a large quantity of items (thanks @pajic for help diagnosing!)
- Cleaned up typos in logs and clarified when a log is coming from RRV.
- Soft failure loading entity loot recipes when modded entities are present.
- Improved null safety on `SlotContent`.
- Searchbar not working when the player has a multiword effect.
- Added additional translations.
- Worked around crashes from rendering item tags.
- Issues clicking on slots behind the cutout Item View.
- Missing tag translations.
