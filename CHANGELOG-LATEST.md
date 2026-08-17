### Added
- If present, sprites provided to a Field Guide resource pack will now override the renders shown in entity recipes.

### Changed
- Recipe screen is no longer an `AbstractContainerScreen`, fixing many compatibility issues from mods expecting it to work like a standard container (#5, #37).
- Maximum recipes per screen are now calculated based on the screen's height, fixing rendering issues on smaller GUI scales (#61).
- Background rendering is now handled through Fabric/NeoForge events, fixing compatibility with mods that cancel or do not render the background texture (#65)
- Opening new recipe screens no longer causes the search bar/buttons to flicker.