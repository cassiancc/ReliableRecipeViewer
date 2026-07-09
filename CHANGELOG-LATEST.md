### Added
- Support for 26.3-snapshot-3 and its data-driven brewing recipes.
- RRV's index is now populated from both the creative mode search tab and the registry.
- Creative mode tabs can now be used as a search parameter via the `%` prefix.
- Suspicious stew recipes now show up correctly.
- Config screens now display a background and are more correctly sized on lower GUI scales.

### Changed
- Internal changes have been made to unify `ItemStack` comparisons. An API for adding checks will be added later.
- Internal changes have been made to unify searching between the Item View and Side Panel.
- Item and block recipe IDs have been changed.

### Fixed
- Component-less versions of Suspicious Stew and Tipped Arrows no longer appear in the index.
- Overly strict ID search.
- Filtering the inventory now accounts for enchanted books and other items with tooltips (thanks @Maganoos!)
- Crashing from updating item slots off thread.
- Incorrectly placed previous tab button in Cut resize mode.