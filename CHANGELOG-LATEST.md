### Added
- RRV is now partially compatible with JEI. 
  - With JEI present, RRV recipes will be bridged to JEI's recipe manager, and JEI's recipe screen will be accessible from RRV's recipe screen in a similar fashion to the existing Polydex integration.
  - You can also now switch between RRV and JEI's panels and recipe screens in the config. The default is RRV's panels and JEI's recipe screen. These can be used interchangeably, with the default setting clicking an item in RRV's panel will open a JEI recipe screen.
  - Note: Even with JEI's recipe screen enabled, Polymer items and items with custom item models will still use the RRV recipe screen, as JEI does not handle these as well as RRV does. RRV's recipe screen can also be manually viewed by clicking the RRV button next to the recipe.
  - Recipes implementing `renderRecipe` should now use `renderRecipe(RecipeScreenContext)` to allow rendering recipes without an RRV `RecipeViewScreen`.
  - With JEI present, RRV can search by item colour with the ^ prefix.
  - Known issues: Bookmarking recipes does not work correctly, previous/next page buttons do not show up with JEI's recipe screen enabled.
- Quick crafting keybind can now be rebound from Left Control.
- Recipe outputs with unique `ITEM_MODEL` components will now be added to the index. This should improve the experience when playing with datapacks like Matcha Flavoured.

### Fixed
- Incorrectly set unknown keybind causing OpenGL spam.
- Incorrect mouse button bindings on 26.3.
- Next/prev page buttons no longer show up if they do not have enough space to render.