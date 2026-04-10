### Added
- Vanilla recipes are now synchronized through the Fabric Recipe Synchronization API. This allows for using built-in codecs and stream codecs for synchronization, allows for some of RRV's features to function with another recipe viewer on the server (e.g. JEI), and deduplicates effort for modded recipes.
- Client integrations can now make use of `ItemView.addClientRecipeProvider` to provide a list of client recipes directly rather than expecting every recipe to exist on the server. This is expected to be used in tandem with the recipe synchronization API.
- Client recipes are now expected to provide a valid `Identifier` by overriding `ClientRecipe#getId`.
- Recipe ids can be seen by hovering over the result with the "Show Recipe ID" config enabled.
- TODO: Client recipes can now be hidden by their ID.