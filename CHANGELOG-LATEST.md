### Added
- RRV is now partially compatible with JEI. This is still in an experimental stage and is not currently recommended, but should at least offer some way to see recipes from mods with only one integration.
  - With JEI present, RRV recipes will be bridged to JEI's recipe manager, and JEI's recipe screen will be accessible from RRV's recipe screen in a similar fashion to the existing Polydex integration.
  - You can also now switch between RRV and JEI's panels and recipe screens in the config. The default is RRV's panels and JEI's recipe screen. These can be used interchangeably, with the default setting clicking an item in RRV's panel will open a JEI recipe screen.
  - Note: Even with JEI's recipe screen enabled, Polymer items, items with custom item models, and items from mods with RRV integrations will still use the RRV recipe screen, as JEI does not handle these as well as RRV does. RRV's recipe screen can also be manually viewed by clicking the RRV button next to the recipe.
  - Recipes implementing `renderRecipe` should now use `renderRecipe(RecipeScreenContext)` to allow rendering recipes without an RRV `RecipeViewScreen`.
  - With JEI present, RRV can search by item colour with the ^ prefix.
  - Known issues: Bookmarking recipes does not work correctly. Any recipes reliant on a `RecipeViewScreen` will not render correctly, and any mods that require mixins into RRV's recipe screen will not render on JEI.
- Quick crafting keybind can now be rebound from Left Control.
- Recipe outputs with unique `ITEM_MODEL` components will now be added to the index. This should improve the experience when playing with datapacks like Matcha Flavoured.
- Index sources can now be mixed and matched from their own screen.
- Recipe type buttons can now be scrolled through.
- Add-ons can now safely extend `RecipeSlot` without losing their custom additions.
- Backport to 1.21.11.

### Changed
- Client config file has been reorganized to match the categories found in the ingame config screen. Configs from 8.6.4 and below will be safely upgraded to this new format.

### Fixed
- Incorrectly set unknown keybind causing OpenGL spam.
- Incorrect mouse button bindings on 26.3.
- Next/prev page buttons no longer show up if they do not have enough space to render.
- Ingredients used without access to a world now partially work.