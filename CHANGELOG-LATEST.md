### Added
- Recipe fallback feature is now available for NeoForge.
- Built-in synchronized recipe types (NeoForge) and recipe serializers (Fabric) are now config and command driven. This allows for modpack authors to add additional recipe serializers or recipe types to mods that have fully functional recipes, but did not opt to synchronize their recipes themselves.

### Fixed
- NeoForge now shows proper warning when connecting to a vanilla server.
- Bug causing namespace search to be overly strict.
- Namespace tooltips not displaying in recipe view screens with Jade present.
- Out of bounds exception when using Inventory Item Groups (thanks @Fox2Code!)