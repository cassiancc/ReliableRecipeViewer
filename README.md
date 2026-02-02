<h1 align="center">RRV</h1>

<p align="center">
  <img width="250" height="250" src="https://asphodel.cc/resources/modrinth/rrv/icon2.png">
</p>

## Overview

[Reliable Recipe Viewer](https://modrinth.com/mod/rrv) is a mod that provides recipe viewer functionality on modern Minecraft versions, from 1.21.11 to 26.1. It's based on [Extended Item View](https://modrinth.com/mod/eiv), which supported 1.21.4-1.21.10. Since EIV never made the jump to 1.21.11 and beyond and had a few issues I opted to provide this fork.

Currently supported functions are:

- recipe viewing
- bookmarking items
- item-transfer (fast-move items in crafting gui)
- hiding/showing overlay
- item highlighting (double-click on searchbar)
- cheatmode

In addition, this fork provides:
- Support for 1.21.11 and 26.1, including support for data-driven villager trading.
- Additional configuration options, including switching the side of the item index, centering the search bar.
- Additional GUI improvemenets, like a scroll bar for the item index, a way to see all recipes by clicking on the recipe type, buttons to change the item index page, and more.
- Additional recipe types for item tags.
- Compatibility improvements, showing mod name translations, tag translations, and more.
- Fixes for multiple bugs seen in the original project.

For more details, see the original Modrinth page for [Extended Item View](https://modrinth.com/mod/eiv), as well as the changelog for a complete list of changes.

**NOTE: Since 1.21.2, all recipe viewers must be installed on both the client and server.**

## FAQ

- Will this mod be ported to other versions/loaders?
  - This port will be kept up to date with the latest version of Minecraft. No backports are planned/necessary, please use the original mod.
- How do I get started adding compatibility with RRV?
  - Please see the docs on the [Modded Minecraft Wiki](https://moddedmc.wiki/en/project/rrv/latest/docs)
- What mods are compatible with RRV?
  - Please see the [Modrinth Collection](https://modrinth.com/collection/divCExF5), which lists every mod that I know has RRV integration.
- Where can I ask questions about RRV?
  - I am happy to answer RRV related questions in my [Discord](https://discord.cassian.cc/).

## License
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](github.com/cassiancc/bygone-fortress)

RRV is available under the open source MIT License, matching the original mod.

## Credits
This started as a port of [Extended Item View](https://modrinth.com/mod/rrv) to Fabric 1.21.11 that I made for personal use. EIV is available under [MIT License](https://www.curseforge.com/minecraft/mc-mods/extended-itemview-eiv#license), but has not been worked on in two months, and due to changes in 1.21.11, previous versions cannot be compiled against. I have opted to redesign some elements of the mod with the goal to make it a more reliable recipe viewer to use.

Code from the now archived [Polydex2EIV](https://github.com/SAGUMEDREAM/Polydex2EIV/) is used under its [MIT License](https://github.com/SAGUMEDREAM/Polydex2EIV/blob/b75fc66d3a835db8f2f18bd42d8895038d74fdb5/src/main/resources/fabric.mod.json#L12).
