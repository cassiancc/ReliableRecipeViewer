<h1>Reliable Recipe Viewer</h1>

## Overview

[Reliable Recipe Viewer](https://modrinth.com/mod/rrv) is a mod that provides recipe viewer functionality on modern Minecraft versions, from 1.21.8 to 26.1. It's based on [Extended Item View](https://modrinth.com/mod/eiv), which at the time I forked supported 1.21.4-1.21.10. As I needed a 26.1 recipe viewer and thought I could improve it, I took the opportunity to fork it and make some improvements.

Currently supported functions are:

- recipe viewing
- bookmarking items
- item-transfer (fast-move items in crafting gui)
- hiding/showing overlay
- item highlighting (double-click on searchbar)
- cheatmode

In addition, this fork provides:
- Support for 26.1.1 and 26.2 snapshots.
- A reworked API designed around the recipe synchronization APIs present in Fabric and NeoForge, cutting down on redundant work and allowing some parts of RRV to function even when a different recipe viewer is present on the server.
- An option to show craftable items from your inventory.
- Additional configuration options, including switching the side of the item index, centering the search bar, reorganizing and hiding recipe categories, editing the index, and much much more.
- Additional GUI improvements, like a scroll bar for the item index, a way to see all recipes by clicking on the recipe type, buttons to change the item index page, and more.
- Additional recipe types for item tags, repairing, and resource pack-driven info and world interaction recipes.
- Compatibility improvements, showing mod name translations, tag translations, support for component ingredients, and more.
- Fixes for multiple bugs seen in the original project.

For more details, see the original Modrinth page for [Extended Item View](https://modrinth.com/mod/eiv), as well as the changelog for a complete list of changes.

**NOTE: Since 1.21.2, all recipe viewers must be installed on both the client and server.**

## FAQ

- Will this mod be ported to other versions/loaders?
  - This port will be kept up to date with the latest version of Minecraft, and I have opted to support the primary loaders on 26.1, Fabric and NeoForge. No backports are planned/necessary, please use the original mod.
- How do I get started adding compatibility with RRV?
  - Please see the docs on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs)!
- What mods are compatible with RRV?
  - Please see the [Modrinth Collection](https://modrinth.com/collection/divCExF5), which lists every mod that I know has RRV integration. If you are a developer, and you've added support for RRV, please reach out in the Discord and I'll add your mod!
- Where can I ask questions about RRV?
  - I am happy to answer RRV related questions in my [Discord](https://discord.cassian.cc/).

## Mod Compatibility

Mods that use Extended Item View are not compatible with this fork, as I want to ensure I can make direct changes to the mod. For a list of currently compatible mods, please see the [Modrinth Collection](https://modrinth.com/collection/divCExF5).

Developers wishing to use the mod can make use of RRV's easy to use API. You can find more info on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs). Unlike the original mod, this fork provides its sources through [Modrinth Maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven#h_233c0ebd50) so that API Javadocs can be easily used.

## License
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](github.com/cassiancc/bygone-fortress)

Reliable Recipe Viewer is available under the open source MIT License, matching the original mod.

## Credits
This began as a port of [Extended Item View](https://modrinth.com/mod/eiv) to Fabric 1.21.11 and beyond with the goal of improving the mod and making it available on newer versions. I was not able to make these changes to EIV directly, as the developer does not seem to respond to pull requests. EIV is available under [MIT License](https://www.curseforge.com/minecraft/mc-mods/extended-itemview-eiv#license).