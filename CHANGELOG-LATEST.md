### Added
- RRV is now partially compatible with JEI. 
  - With JEI present, RRV recipes will be bridged to JEI's recipe manager, and JEI's recipe screen will be accessible from RRV's recipe screen in a similar fashion to the existing Polydex integration. 
  - With JEI present, RRV can search by item colour with the ^ prefix.
  - You can also now switch between RRV and JEI's panels and recipe screens in the config. The default is RRV's panels and JEI's recipe screen. These can be used interchangeably, with the default setting clicking an item in RRV's panel will open a JEI recipe screen.
  - Recipes implementing `renderRecipe` should now use `renderRecipe(RecipeScreenContext)` to allow rendering recipes without an RRV `RecipeViewScreen`.

### Fixed
- Incorrectly set unknown keybind causing OpenGL spam.
- Next/prev page buttons no longer show up if they do not have enough space to render.