# Backrooms Level 0

Fabric 1.20.1 mod prototype for QuestCraft that adds **Level 0 - The Lobby** as a Backrooms-style dimension.

## Features

- Teleports a player to `backrooms_level0:level_0` when their head is inside a falling suffocation block, such as sand or gravel.
- Shows a short title card: `Level 0` / `The Lobby`.
- Generates an endless Level 0 layout chunk-by-chunk with yellow wallpaper, damp carpet, ceiling tiles, doorways, and fluorescent lights.
- Uses vanilla Minecraft texture references for custom block models so the repository contains no binary image assets.
- Adds `THE BACKROOMS` text above the Minecraft logo on the main menu.

## Build

This project targets Minecraft `1.20.1`, Fabric Loader `0.16.x`, Fabric API `0.92.x`, and Java 17.

```bash
gradle build
```

The downloadable mod jar is produced under `build/libs/` when dependencies can be resolved.
