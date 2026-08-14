### Added
- Completely redesigned the category formerly known as "Mob Drops".
  - Renamed to Entities, and changed the category icon to spawn eggs rather than a sword.
  - Hovering over an entity will now show whether they are hostile via text colour, the name of the mod that adds the entity, and Item Descriptions if present.
  - Doubled the size of the rendered entity.
  - Mob drops have been moved to the side and a tooltip has been added to clarify drops.
  - Now shows information about animal food below the entity preview.
  - Animal food can be overridden in the client integration via `ItemView#addMobFood`.
- Client recipes can now override `addRecipeWidgets` and call `GuiWidgetAccess.widgets.addRecipeWidget` to add vanilla `Renderable`/`GuiEventListener` objects to the recipe layout. JEI support on this feature is still in development.

### Changed
- Creative tabs depending on synchronized recipes are now displayed in RRV correctly.

### Fixed
- Crash when spamming bookmarks key.
- Tag descriptions provided by Item Descriptions now wrap correctly.
- Potential crash loading worlds with Unique Recipe Output index source enabled.

**FIXME**: JEI support for `addRecipeWidgets`.