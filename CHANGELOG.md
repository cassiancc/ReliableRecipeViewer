## [8.7.3]

### Changed
- Stack group translation keys are now shown in development.
- Reintroduced a fixed version of the performance optimization removed in 8.7.2.

### Fixed
- Crash from incorrectly generated Identifier in burning recipes.
- Partial search matches for multiple words now work correctly.
- Typo preventing entity tooltip from working.
- Support NeoForge's redesigned mod menu on 26.2.

## [8.7.2]

### Added
- Support for 26.3-snapshot-7.
- Added stack group for Cloneable Maps.

### Fixed
- Removed broken optimization causing creative search to not work. Will be further tested for v8.8.

## [8.7.1]

### Fixed
- Unique recipe output index source now works correctly.

## [8.7.0]

### Added
- RRV is now partially compatible with JEI. This is still in an experimental stage and is not currently recommended, but should at least offer some way to see recipes from mods with only one integration.
  - With JEI present, RRV recipes will be bridged to JEI's recipe manager, and JEI's recipe screen will be accessible from RRV's recipe screen in a similar fashion to the existing Polydex integration.
  - You can also now switch between RRV and JEI's panels and recipe screens in the config. The default is RRV's panels and JEI's recipe screen. These can be used interchangeably, with the default setting clicking an item in RRV's panel will open a JEI recipe screen.
  - Note: Even with JEI's recipe screen enabled, Polymer items, items with custom item models, and items from mods with RRV integrations will still use the RRV recipe screen, as JEI does not handle these as well as RRV does. RRV's recipe screen can also be manually viewed by clicking the RRV button next to the recipe.
  - Recipes implementing `renderRecipe` should now use `renderRecipe(RecipeScreenContext)` to allow rendering recipes without an RRV `RecipeViewScreen`.
  - With JEI present, RRV can search by item colour with the ^ prefix.
  - Known issues:
    - Bookmarking recipes does not work correctly.
    - Optional slot renderers with custom textures only display normal slots.
    - Animations do not tick.
    - Any recipes reliant on a `RecipeViewScreen` will not render correctly.
    - Any mods that require mixins into RRV's recipe screen will not render on JEI.
    - Recipes that are synchronized via `ReliableServerRecipe` are not displayed in JEI, as they do not exist when JEI's plugin is initialized.
- Quick crafting keybind can now be rebound from Left Control.
- Recipe outputs with unique components can now be added to the index by enabling the Unique Recipe Output index source. This should improve the experience when playing with datapacks like Matcha Flavoured.
- Index sources can now be mixed and matched from their own screen.
- Recipe type buttons can now be scrolled through.
- Add-ons can now safely extend `RecipeSlot` without losing their custom additions.
- Backport to 1.21.11.
- 26.3 will now show Fuel Recipes again.
- Added a client recipe type for Composting.
- Side panel will now show previous/next buttons if there's space.
- Previous/next buttons will now become greyed out when unusable.
- Mob drops can now be modified with the API via `ItemView#modifyMobDrops`.
- Full stack list is now computed in the background rather than when the inventory is first opened.
- Added an API for adding default checks to items.

### Changed
- Client config file has been reorganized to match the categories found in the ingame config screen. Configs from 8.6.4 and below will be safely upgraded to this new format.
- Integrations failing to load will now provide a full stacktrace.

### Fixed
- Incorrectly set unknown keybind causing OpenGL spam.
- Incorrect mouse button bindings on 26.3.
- Next/prev page buttons no longer show up if they do not have enough space to render.
- Ingredients used without access to a world now partially work.
- Stack groups not matching correctly on items added via creative tabs or when the index has been modified.
- Crash sharing recipes without any outputs.
- Stack group config can no longer be opened if stack groups are disabled.

## [8.6.4]

### Added
- Support for 26.3-snapshot-5. This version has initially been released as a beta.
  - Fuel recipes are not currently supported.

### Fixed
- Crash from duplicated recipes when using Launchpad (thanks Su5eD!)

## [8.6.3]

### Added
- Support for running mods with RRV integration via Launchpad and Sinytra Connector.

## [8.6.2]

### Fixed
- Performance issues when searching.

## [8.6.1]

### Fixed
- Crash on dedicated servers.

## [8.6.0]

### Added
- Stack groups, a new way to group similar items together to make navigating the index easier. Stack groups are highly configurable, and have their own config screen. (thanks @evanbones!)

### Fixed
- Creative index source causing Polymer items to be added to the index incorrectly.
- Log spam and potential broken behaviour on 26.3.
- Firework rocket recipe displays are now more accurate.

## [8.5.0]

### Added
- Support for 26.3-snapshot-3 and its data-driven brewing recipes.
- RRV's index is now populated from both the creative mode search tab and the registry.
- Creative mode tabs can now be used as a search parameter via the `%` prefix.
- Suspicious stew recipes now show up correctly.
- Config screens now display a background and are more correctly sized on lower GUI scales.

### Changed
- Internal changes have been made to unify `ItemStack` comparisons. An API for adding checks will be added later.
- Internal changes have been made to unify searching between the Item View and Side Panel.
- Item and block recipe IDs have been changed.

### Fixed
- Component-less versions of Suspicious Stew and Tipped Arrows no longer appear in the index.
- Overly strict ID search.
- Filtering the inventory now accounts for enchanted books and other items with tooltips (thanks @Maganoos!)
- Crashing from updating item slots off thread.
- Incorrectly placed previous tab button in Cut resize mode.

## [8.4.2]

### Added
- Support for 26.3-snapshot-2.
- Component-driven block transformers now automatically generate world interaction recipes.
- `ItemView#hideItemStack` can now be used to hide a distinct `ItemStack` from the index, rather than using `ItemView#hideItem` which excludes all itemstacks of that item's type.

## [8.4.1]

### Fixed
- Trim recipes do not show the correct output.
- Crash when opening an RRV screen on a non-English locale.
- Crash when attempting to view a GeckoLib entity.

## [8.4.0]

### Added
- Support for 26.2 NeoForge

### Changed
- Craftables logic has been moved off-thread to improve performance.

### Fixed
- Issues with namespace tooltips.

## [8.3.1]

### Added
- Recipe fallback feature is now available for NeoForge.
- Built-in synchronized recipe types (NeoForge) and recipe serializers (Fabric) are now config and command driven. This allows for modpack authors to add additional recipe serializers or recipe types to mods that have fully functional recipes, but did not opt to synchronize their recipes themselves.

### Fixed
- NeoForge now shows proper warning when connecting to a vanilla server.
- Bug causing namespace search to be overly strict.
- Namespace tooltips not displaying in recipe view screens with Jade present.
- Out of bounds exception when using Inventory Item Groups (thanks @Fox2Code!)

## [8.3.0]

### Changed
- Craftables panel now responds to the search bar.
- Client fallback mode is now automatically initialized when on a server without RRV installed.
- Recipe transfer and sharing features are now properly hidden on vanilla servers, rather than being nonfunctional.

### Fixed
- Fluid stacks now handle data component patches.
- Client integration no longer assumes fluids have buckets.
- Crash rendering entities on 26.2.

## [8.2.1]

### Fixed
- Potential crash from Jade causing client configs to load on the server.

## [8.2.0]

### Added
- Search aliases, used to allow for mods wanting to make their items searchable from other names.
- Recipe sharing, all recipes types now have a button used to share the recipe to the chat.
  - The recipe sharing button can be manually positioned by overriding `placeRecipeShareButton` in your `ReliableClientRecipeType`.
  - Recipes can also be shared via the `/rrv share_recipe` command.
  - Recipe sharing has a serverside opt-out via the `/rrv_admin recipe_sharing false` command.
- The recipe transfer button can now be manually positioned by overriding `placeRecipeTransferButton` in your `ReliableClientRecipeType`.

### Changed
- Recipe type backgrounds have been retextured to add stronger borders.
- RRV commands are now snake cased, and subcommands requiring administrator permissions are now under `rrv_admin`.

### Fixed
- Workstation slot is no longer offscreen when using the Center Recipe Screen config.
- Switching to a new page of recipe types now changes the current screen.
- Log spam caused by unbinding the cheatmode.
- Shortened tag translations are now supported.
- Rewrote tag search for performance benefits and deduplication.

## [8.1.3]

### Added
- Tags now show proper Item Descriptions.

### Changed
- Tags no longer show their ID if Advanced Tooltips is not enabled.

### Fixed
- Tags not displaying in recipes.
- Tags not displaying proper namespaces.
- Buttons and clickables now change the cursor correctly.

## [8.1.2]

### Changed
- If an `item_model` component is set and `getCreatorNamespace` is not overridden, the `item_model`'s namespace is now used.

### Fixed
- Crash loading an exclusions file without `block` and `item` keys.
- Exception caused by logging onto a server with a different entity registry than the client.
- Cleanup log spam from newly registered categories.

## [8.1.1]

### Fixed
- Recipe keybinds on footer workstation slot.
- Potential missing categories from client recipe providers.
- Bug preventing index modification from working with normal items.
- Bug allowing scrolling onto a blank page.

## [8.1.0]

### Changed
- Textures for recipe type tabs have been reworked to better match the Creative Mode and Advancements screens.
- Workstations are now displayed below the recipe. This can be reverted in the config.

### Fixed
- Recipe view screens no longer render compasses/clocks incorrectly.
- Previous/next recipe buttons now wrap scroll.
- Further Polymer mod loaded check fixes.
- Overlays persisting between screens.

## [8.0.3]

### Fixed
- Improvements to null safety on `SlotDisplay`
- Improvements to error handling (thanks @Fox2Code!)

## [8.0.2]

### Fixed
- Polymer mod loaded check.
- Incorrect bookmark title hover check on classic theme.
- Crash loading modified data-driven villager trades.

## [8.0.1]

### Added
- `SlotContent` now accepts a `SlotDisplay`.

### Fixed
- Major improvements to Polymer integration.
- Recipe book persisting between screens.

## [8.0.0]

### Added
- RRV's overlays have been given a fresh new theme, inspired by the Recipe Book. This can be reverted in the config to restore a more classic appearance.
- Vanilla recipes are now synchronized through the Fabric and NeoForge Recipe Synchronization APIs. This allows for using built-in codecs and stream codecs for synchronization, allows for some of RRV's features to function with another recipe viewer on the server (e.g. JEI), and deduplicates effort for synchronizing modded recipes.
  - In your server integration (or earlier), call `ServerRecipeManager.synchronizeRecipeType` with your recipe serializer and recipe type. This will automatically be supplied to NeoForge and Fabric's APIs when they request them.
  - In your client integration, call `ClientRecipeManager.getRecipesForType` to retrieve all synchronized recipes.
  - Client integrations can now make use of `ItemView.addClientRecipeProvider` to provide a list of client recipes directly rather than expecting every recipe to exist on the server. This is expected to be used in tandem with the recipe synchronization API.
  - For more details, see the newly revised v8.0.0 docs.
- Client recipes are now expected to provide a valid `Identifier` by overriding `ClientRecipe#getId`.
  - Recipe ids can be seen by hovering over the result with the "Show Recipe ID" config enabled.
  - Client recipes can now be hidden by their ID via the `rrv:exclusions` resource pack file and the API method `ItemView#excludeRecipe`, see the docs for more information.
- On Fabric, a partial client fallback mode is now used that pulls recipes from the client's recipe folder.
- Pressing the Bookmark key while hovering over a recipe result will now bookmark that recipe. Clicking on that bookmark will open the recipe directly.
- The craftables panel now shows results associated with recipes.
- CTRL-Clicking on an item slot with an associated recipe will now attempt to quick craft it.
- Additional special recipe types are available in RRV.
  - Support for imbuing, book cloning, map extending, firework rocket, shield decoration, repairing, and decorated pot crafting recipes.
  - Fluid bucketing recipes are now included in RRV.
  - World interaction recipes for Concrete are now included.
- Namespace tooltips can now be shown in all contexts, instead of only in the Item View. This is configurable.
- A new button on the overlay screen that allows switching the current side panel (thanks @lurkywho for sprite help).
- Item tag translations for 26.2-snapshot-3.
- A new API for registering keybinds when hovering over item slots (thanks @Fox2Code)

### Changed
- Shaped recipes are now sorted before shapeless recipes.
- Crafting client recipes now make use of a builder.
- `ReliableClientRecipe#getViewType` was renamed to `ReliableClientRecipe#getType`, backwards compatible.
- Minor config reorganization.
- Namespaced tooltips will now default to being disabled if another mod is present that has the feature (WTHIT/Jade/Item Descriptions)
- Decreased padding on item entries.
- Incompatible server warning is now displayed when attempting to see recipes to ensure recipe synchronization has completed.

### Fixed
- Bug with overlay toggling.
- Improvements to config handling.
- Bug causing keybinds to remove bookmarks.
- Info recipe rendering error with some resource packs.
- Improvements to fluid rendering and removal of hardcoded fluids.
- Performance issues opening the inventory (thanks @Fox2Code)

## [7.1.4]

### Fixed
- Effect blockers not being cleared correctly.

## [7.1.3]

### Fixed
- Error from search bar clearing being run too late.

## [7.1.2]

### Fixed
- Scrolling issue with Meowdding Lib.
- Typo in error message when connecting to vanilla servers.
- Search bar not being cleared when disconnecting from a world.

## [7.1.1]

### Fixed
- Overflowing progress bar.

## [7.1.0]

### Added
- The client can now configure what recipe types they wish to see in the index and in which order.
  - Categories are automatically saved to the `.minecraft/rrv/recipe_categories.json` file. A category can have an `enabled` value setting whether it should be visible, and a `priority` value for where to place in the index. When a new category is loaded, it will automatically be sorted by alphabetical priority.
  - Configuration can also be done in-game from the config screen, under Advanced.
  - Client recipe types can now override `getPriority` to ignore alphabetical sorting and place themselves manually.
  - By default, recipe types are sorted alphabetically, with Minecraft recipe types sorted before modded ones.
- The "Entity Fighting"  recipe type has been renamed to the Mob Drops recipe type.
  - Mob drops can be added via `ItemView.addMobDrops()`.
  - Goat horn drops are now included.
  - Chicken egg drops are now included.
- Fixes have been made to fluid units.
  - Fluid units can now be configured to show as droplets, Fabric's preferred unit.
  - Fluid units now include commas.
- The progress bar can now be hidden.
- The block tag recipe type is no longer enabled by default.
- The configuration screen has been overhauled and now includes additional descriptions for included options.
- Recipe screen position can now be set to vertically centered.
- Villager server recipes have been updated to be closer to upstream EIV, fixing some performance issues with the current implementation.
- SlotContent now has a codec, which is used for serialization and deserialization.


## [7.0.4]

### Fixed
- Porting bug causing backgrounds to display over other GUIs.

## [7.0.3]

### Added
- Info recipes can now be added via code.
- Updated Chinese translations (thanks @moqyng!)

## [7.0.2]

### Fixed
- World interaction recipes not being cleared correctly on `/reload`.
- A rare crash logging into worlds.

## [7.0.0]

### Added
- Support for Fabric and NeoForge's Data Component Ingredients and NeoForge's block tag ingredients.
- Side panel visibility can now be individually set rather than always following the Item View.
- Searching with filters now changes the colour of the text.
- Searching without a result now changes the colour of the searchbar.
- Various strings have been made more translatable.
- Block tags are now visible in the index.
- World interaction recipes can now be registered via the API.

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
- Controlify integration is temporarily disabled until it updates.

## [6.6.2]

### Fixed
- Issues with scaling in new config menu.
- On 26.1, an issue reloading worlds caused by the dye recipes.

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