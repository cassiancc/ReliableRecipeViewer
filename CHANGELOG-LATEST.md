### Changed
- All tooltips are now indexed by search, instead of just enchantment tooltips.
- Search, indexing, and similar logic to update item slots is now completely handled off the main thread. This cuts down on large lag spikes, though the item view may take a second to appear.