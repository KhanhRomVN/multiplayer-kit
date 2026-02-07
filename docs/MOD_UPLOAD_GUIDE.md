# How to Upload Your Mod to Mindustry Mod Browser

This guide explains how to publish your mod to the in-game Mindustry mod browser.

## Prerequisites

1.  **GitHub Repository**: Your mod must be hosted on a public GitHub repository.
2.  **`mod.hjson`**: Your mod must have a valid `mod.hjson` file in the root directory.
3.  **Git Tags**: You should use git tags for versioning (e.g., `v1.0`, `v1.1`).

## Steps to Publish

### 1. Prepare Your Repository

Ensure your repository structure is correct. The `mod.hjson` should be at the root.

```
multiplayer-pause/
├── mod.hjson
├── README.md
├── build.gradle
└── src/
```

### 2. Verify `mod.hjson`

Make sure your `mod.hjson` has the correct `name` and `version`. The `name` must be unique.

```hjson
name: "pause-mod"
displayName: "Multiplayer Pause"
author: "KhanhRomVN"
description: "..."
version: "3.0"
minGameVersion: 154
hidden: true
```

> **Note**: `hidden: true` means it won't show up in the browser immediately until you add it, but for the main mod browser list, you usually want it `hidden: false` if it's a regular mod. However, the `pause-mod` seems to be a utility. If you want it to be finding in the browser, remove `hidden: true` or set it to `false`.

### 3. Commit and Push

Push all your changes to GitHub.

```bash
git add .
git commit -m "Prepare for release"
git push origin master
```

### 4. Create a Release (Optional but Recommended)

1.  Go to your GitHub repository -> **Releases** -> **Draft a new release**.
2.  Tag version: `v3.0` (matching your mod version).
3.  Title: `Release v3.0`.
4.  Description: Changelog.
5.  **Attach the JAR file**: Upload the `multiplayer-pause-pc-v2-v3.0.jar` from your `build/libs` folder.
6.  Publish release.

### 5. Submit to Mindustry Mod Browser

There are two ways to get your mod into the browser:

#### Method A: Direct GitHub Topic (Automatic)

Anuken's script scrapes GitHub for repositories with specific topics.

1.  Go to your repository **About** section (top right on main page).
2.  Click the ⚙️ gear icon.
3.  Add the topic: `mindustry-mod`.
4.  Save.

Wait for the crawler to pick it up (can take up to 24 hours).

#### Method B: Pull Request to MindustryMods

If Method A doesn't work or you want faster results:

1.  Fork the [MindustryMods/Mods](https://github.com/MindustryMods/Mods) repository.
2.  Edit the `mods.json` (or strictly following their contribution guide, usually they just use the topic method now, but checking their README is best).
3.  *Actually, the current standard is just using the `mindustry-mod` topic.*

### 6. Updating Your Mod

To update your mod in the browser:

1.  Update `version` in `mod.hjson`.
2.  Commit and Push.
3.  Create a new Release with the new tag.

The game will automatically detect the version change if users have verified the mod.
