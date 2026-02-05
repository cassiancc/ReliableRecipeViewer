### Added
- Craftables can now be seen in the left panel. To quickly switch to the craftable panel, just click on the title bar.
- Direct integration with Polydex, based on the now archived Polydex2EIV.
- Support for 26.1-snapshot-6 and its new item tags.
- The Item View can now be filtered by item ID using the `:` prefix (thanks @fireboy637)
- The client recipe type icon can now be rendering dynamically using `renderIcon`. If this is not overriden, it will render an `ItemStack` as usual.

### Changed
- Internal changes to unify interactions with the item view and slot contents.
- Internal changes to fluid rendering.
- Internal changes to the bookmark manager, decoupling it from the overlay system.

### Fixed
- Log message about missing model.
- Remaining missing tag translations.
- Item View scrollbar no longer overflows when set to the left panel.
- Multiple effects clipping with the Item View.