### Changed
- If an `item_model` component is set and `getCreatorNamespace` is not overridden, the `item_model`'s namespace is now used.

### Fixed
- Crash loading an exclusions file without `block` and `item` keys.
- Exception caused by logging onto a server with a different entity registry than the client.
- Cleanup log spam from newly registered categories.