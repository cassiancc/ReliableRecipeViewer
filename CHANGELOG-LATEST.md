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