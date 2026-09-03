### Added
- New events to allow addons to add exclusion areas without mixing RRV code into their main code, see `OverlayManagementEvents#registerExclusionArea`.
- On Fabric, fluid names and tooltips now use the fluid rendering APIs.
- Development option to see exclusion areas (only in config file).

### Changed
- All tooltips are now indexed by search, instead of just enchantment tooltips.
- Search, indexing, and similar logic to update item slots is now completely handled off the main thread. This cuts down on large lag spikes, though the item view may take a second to appear.