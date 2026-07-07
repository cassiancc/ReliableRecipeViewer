# Reliable Recipe Viewer

<a href='https://modrinth.com/mod/rrv/versions?l=fabric'><img alt="fabric" height="56" src="https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/supported/fabric_vector.svg"></a>
<a href='https://modrinth.com/mod/rrv/versions?l=neoforge'><img alt="neoforge" height="56" src="https://raw.githubusercontent.com/cassiancc/Cassians-Badges/refs/heads/main/cozy/NeoForge.svg"></a>

A recipe viewer for the latest versions of Minecraft, rebuilt from [EIV](https://modrinth.com/mod/eiv) and designed to be configured.

**NOTE: Since 1.21.2, all recipe viewers must be installed on both the client and server for full functionality.**

## Features

* **Recipe Viewing:** See recipes for all vanilla recipe types, as well as [supported modded recipe types](https://modrinth.com/collection/divCExF5) as well!
* **Local Recipe Support**: On vanilla servers, RRV will automatically pull recipe data from the client's `recipe` folder. Note that if possible RRV should still be installed on the server.
* **Powerful Configuration:** Reorder and disable recipe types, rearrange the item index, rearrange the overlays, group similar items with stack groups, and more config options to fit your needs.
* **Bookmarking and Craftables Panel:** Bookmark items and recipes with the `A` key, or use the side panel to see what you can craft with the items in your inventory.
* **Recipe Transfer:** Click the transfer button on supported recipes to quickly transfer items from your inventory to the workstation.

Reliable Recipe Viewer is a fork of [Extended Item View](https://modrinth.com/mod/eiv). A summary of changes made from the original project can be found below.

<details>

<summary>Why Reliable Recipe Viewer?</summary>

- A reworked API designed around the recipe synchronization APIs present in Fabric and NeoForge, cutting down on redundant work and allowing some parts of RRV to function even when the server has a different recipe viewer or uses a plugin like [JustEnoughPaper](https://github.com/cassiancc/JustEnoughPaper/releases/tag/1.0-26.1-SNAPSHOT).
- On Fabric, a local fallback mode allowing for partial functionality on vanilla servers.
- Support for the latest versions of Minecraft, including snapshots when possible.
- New API options to hide recipes, recipe categories, enchantments, potions, and more.
- Ability to bookmark individual recipes, rather than only stacks.
- An option to show craftable items from your inventory.
- Additional configuration options, including switching the side of the item index, centering the search bar, reorganizing and hiding recipe categories, editing the index, grouping similar items into Stack Groups, and much, much more.
- Additional GUI improvements, like a scroll bar for the item index, a way to see all recipes by clicking on the recipe type, buttons to change the item index page, and more.
- Additional recipe types for special crafting recipes, item tags, repairing, and resource pack-driven info, world interaction, and anvil combining recipes.
- Compatibility improvements, showing mod name translations, tag translations, support for component ingredients, and more.
- Improved mod compatibility with [Reliable Remover](https://modrinth.com/mod/reliable-remover), [Polydex](https://modrinth.com/mod/polydex), [Controlify](https://modrinth.com/mod/controlify), and more.
- Fixes for multiple bugs seen in the original project.

</details>

## Documentation

Developers wishing to integrate their mods with Reliable Recipe Viewer or configure it for their modpack can make use of RRV's easy to use API. You can find more info on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs). Unlike the original mod, this fork provides its sources through [Modrinth Maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven#h_233c0ebd50) so that API Javadocs can be easily used.

## FAQ

- Will this mod be ported to other versions/loaders?
  - This port will be kept up to date with the latest version of Minecraft, and I have opted to support the primary loaders on 26.1, Fabric and NeoForge. No backports are planned/necessary, please use the original mod.
- How do I get started adding compatibility with RRV?
  - Please see the docs on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs)!
- What mods are compatible with RRV?
  - Please see the [Modrinth Collection](https://modrinth.com/collection/divCExF5), which lists every mod that I know has RRV integration. If you are a developer, and you've added support for RRV, please reach out in the Discord and I'll add your mod!
- Where can I ask questions about RRV?
  - I am happy to answer RRV related questions in my [Discord](https://discord.cassian.cc/).

## License
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](https://github.com/cassiancc/reliablerecipeviewer)

Reliable Recipe Viewer is available under the open source MIT License, matching the original mod.

## Credits

This began as a port of [Extended Item View](https://modrinth.com/mod/eiv) to Fabric 1.21.11 and beyond with the goal of improving the mod and making it available on newer versions. I was not able to make these changes to EIV directly, as the developer does not seem to respond to pull requests. EIV is available under [MIT License](https://github.com/liushmn/ExtendedItemView/blob/26.1/LICENSE).