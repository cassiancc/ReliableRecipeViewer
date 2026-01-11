## [6.2.1]

### Added
- Lists can now be used in info recipes.

### Changed
- Merged Shapeless Crafting and Shaped Crafting into one Crafting tab.

## [6.2.0]

### Added
- Resource pack driven info recipes.
- Reworked documentation, the new docs can be found on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs).

### Changed
- Further internal refactors to ensure side safety by moving internal integrations that rely on clientside code to `BuiltInReliableRecipeViewerClientIntegration` and moving item model code to a client package.

## [6.1.1]

### Fixed
- API warnings.

## [6.1.0]

### Added
- Users of split sources can now make use of `ReliableRecipeViewerClientPlugin` and the `rrv_client` entrypoint. This is currently functionally identical to the existing `ReliableRecipeViewerPlugin` and `rrv` entrypoint, but may change in the future as the mod is updated to better handle split sources.
- Added deprecated helpers for old EIV style methods to help point people towards the new API.
- NeoForge support has been re-enabled for 26.1-snapshot-2 and above.
- Bookmarks panel can now be hidden in the config.
- Items hidden from the index no longer appear in the item tag view.
- RRV now respects `c:hidden_from_recipe_viewers` for fluids, enchantments, and potions.

### Changed
- `ItemView#registerClientRecipeWrapper` was renamed to `ItemView#addClientRecipeWrapper` for consistency with other methods in `ItemView`. The old method is retained to continue supporting mods designed for 6.0.4 and below.

### Fixed
- Improved null safety on `SlotContent`.
- Searchbar not working when the player has a multiword effect.
- Added additional translations.
- Worked around crashes from rendering item tags.
- Issues clicking on slots behind the cutout Item View.
- Missing tag translations.

## [6.0.4]

### Fixed
- Leftover debug logs.

## [6.0.3]

### Fixed
- Performance issues when rendering a large quantity of items (thanks @pajic for help diagnosing!)
- Cleaned up typos in logs and clarified when a log is coming from RRV.
- Soft failure loading entity loot recipes when modded entities are present.

## [6.0.2]

### Added
- Port to 26.1-snapshot-2 and added its new tag.

### Fixed
- Incorrect tag translations.

## [6.0.1]

### Added
- Networking is now handled through Fabric and will warn users upon connecting to incompatible servers.

### Fixed
- Crash with VoxelMap.

## [6.0.0]
- Features
  - Clicking the recipe type title text now shows a complete recipe index.
  - Item tags can now be viewed in RRV.
- API Renames and Refactors
  - `EivTagUtil` -> `TagUtil`
  - `IExtendedItemViewIntegration`->`ReliableRecipeViewerPlugin`
  - `EivServerRecipe` -> `ReliableServerRecipe`
  - `EivRecipeType` -> `ReliableServerRecipeType`
  - `EivViewRecipe` -> `ReliableClientRecipe`
  - `EivRecipeViewType` `ReliableClientRecipeType`
- Additional configuration options
  - Mod namespace tooltips can now be disabled.
  - Item View position can now be swapped between the left and right panels. Fixes [#27](https://github.com/liushmn/ExtendedItemView/issues/27)
  - Added a toggle for RRV to the client settings screen.
- Tweaks
  - Hide Item View keybind is unset by default.
  - Added buttons to change the current page.
  - Added a "Search Item View" hint to the search bar.
  - Recipe view types (client recipe types) can safely provide `null` as a background.
- Compatibility
  - RRV's settings can be modified in Mod Menu.
  - Mod namespace tooltips respect `getCreatorNamespace` and can be translated in the Mod Menu format.
- Fixes
  - Overlay can no longer be toggled by typing in the Creative Inventory search. Fixes [#26](https://github.com/liushmn/ExtendedItemView/issues/26)
  - Clocks, compasses, and recovery compasses no longer work in the item view. Fixes [#28](https://github.com/liushmn/ExtendedItemView/issues/28)
  - Workaround for [#24](https://github.com/liushmn/ExtendedItemView/issues/24)