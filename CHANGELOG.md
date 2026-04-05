## [6.6.1]

### Fixed
- Info recipe text persisting between screens.
- Scrollable layout has been swapped out for a standard layout that fits all config options and is still visible on all versions. Scrollable view will be revisited later.


## [6.6.0]

### Added
- The index can now be edited via resource packs. Any file in `assets/<namespace/rrv/index` will modify what items are shown in the item view, for more details see its docs page.
- Client configs screen now makes use of sections and scrolls to better handle the additional options.
- The Item View can now be exported to the format used by the index editor.
- Buttons can now be hidden.
- Dyeable item recipes are now present in RRV.


### Fixed
- Bug causing Villager Trades to be inaccurate on 1.21.11.
- Clock showing up as an input for world interaction recipes.
- Issues with Item View when searching option not correctly hiding buttons.
- Smithing recipes without templates now look nicer.

### Removed
- Buggy creative index source option - hopefully will return in a future update.

## [6.5.0]

### Added

- "Repairing" has been replaced with "Anvil Combining", a new resource-pack driven recipe format based on world interaction recipes.
- Resource driven recipes can now specify component data.

### Changed
- Info recipes and word interaction recipes can now both be put into the `rrv/recipe` folder rather than `rrv_world_interaction` or `rrv_info` folders. These legacy folders will become ignored in a future update.
- Next/previous page buttons have been resized and no longer hold the mouse's focus.
- Bookmarking an item now switches the overlay to the bookmark panel.
- Side panel is now hidden by default.

## [6.4.3]

### Added
- Wrap scrolling - enabled by default for next/previous page buttons, can be enabled for scrolling as well.
- Initial work to support Fabric custom ingredients.

### Fixed
- Scrolling issues from mixins.

## [6.4.2]

### Fixed
- Log spam from side panel.

## [6.4.1]

### Added
- Support for 26.1-snapshot-8.

### Fixed
- F3 menu showing RRV debug information.
- Minor improvements to craftables performance. This is still being worked on and improved.

## [6.4.0]

### Added
- Craftables can now be seen in the left panel. To quickly switch to the craftable panel, just click on the title bar.
- Direct integration with Polydex, based on the now archived Polydex2EIV.
- Support for 26.1-snapshot-7 and its new item tags.
- The Item View can now be filtered by item ID using the `:` prefix (thanks @fireboy637!)
- The client recipe type icon can now be rendering dynamically using `renderIcon`. If this is not overridden, it will render an `ItemStack` as usual.
- Translation to Simplified Chinese (thanks @moqyng!)

### Changed
- Internal changes to unify interactions with the item view and slot contents.
- Internal changes to fluid rendering.
- Internal changes to the bookmark manager, decoupling it from the overlay system.

### Fixed
- Log message about missing model.
- Remaining missing tag translations.
- Item View scrollbar no longer overflows when set to the left panel.
- Multiple effects clipping with the Item View.
- Info recipes can no longer overflow.
- Search bar no longer is hidden upon first launch with "Display when searching" is enabled.

## [6.3.3]

### Added
- A new option in the settings to only display RRV when actively searching.
- Ported to 26.1-snapshot-5.

### Fixed
- Overlay hidden state now persists between game reloads.
- Modded menus with high width values no longer cause the searchbar to be larger than half the screen.

## [6.3.2]

### Added
- Support for 26.1-snapshot-4.
- When using Controlify, the bumpers can now be used to switch tabs.

### Changed
- Temporarily set the default index source back to registry due to issues on survival servers.

### Fixed
- Index source config not saving.

## [6.3.0]

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

## [6.2.1]

### Added
- Lists can now be used in info recipes.

### Changed
- Merged Shapeless Crafting and Shaped Crafting into one Crafting tab.
- `ItemView.excludeEnchantments` now works on collections.

### Fixed
- NeoForge now loads recipes correctly.

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