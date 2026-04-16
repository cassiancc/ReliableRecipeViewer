### Added
- Vanilla recipes are now synchronized through the Fabric and NeoForge Recipe Synchronization APIs. This allows for using built-in codecs and stream codecs for synchronization, allows for some of RRV's features to function with another recipe viewer on the server (e.g. JEI), and deduplicates effort for synchronizing modded recipes.
  - In your server integration (or earlier), call `ServerRecipeManager.synchronizeRecipeType` with your recipe serializer and recipe type. This will automatically be supplied to NeoForge and Fabric's APIs when they request them.
  - In your client integration, call `ClientRecipeManager.getRecipesForType` to retrieve all synchronized recipes.
  - Client integrations can now make use of `ItemView.addClientRecipeProvider` to provide a list of client recipes directly rather than expecting every recipe to exist on the server. This is expected to be used in tandem with the recipe synchronization API.
  - For more details, see the newly revised v8.0.0 docs.
- Client recipes are now expected to provide a valid `Identifier` by overriding `ClientRecipe#getId`.
  - Recipe ids can be seen by hovering over the result with the "Show Recipe ID" config enabled.
  - Client recipes can now be hidden by their ID via the `rrv:exclusions` resource pack file and the API method `ItemView#excludeRecipe`, see the docs for more information.
- Support for imbuing, book cloning, map extending, firework rocket, shield decoration, repairing, and decorated pot crafting recipes.
- Fluid bucketing is now visible in RRV.
- Namespace tooltips can now be shown in all contexts, instead of only in the Item View. This is configurable.
- A new button on the overlay screen that allows switching the current side panel (thanks @lurkywho for sprite help).
- Item tag translations for 26.2-snapshot-3.

### Changed
- Shaped recipes are now sorted before shapeless recipes.
- Crafting client recipes now make use of a builder.
- `ReliableClientRecipe#getViewType` was renamed to `ReliableClientRecipe#getType`, backwards compatible.
- Minor config reorganization.
- Namespaced tooltips will now default to being disabled if another mod is present that has the feature (WTHIT/Jade/Item Descriptions)

### Fixed
- Bug with overlay toggling.