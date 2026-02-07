# Multiplayer Kit Mod

[![Mindustry Version](https://img.shields.io/badge/Mindustry-v154-blue.svg)](https://github.com/Anuken/Mindustry)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-3.0-orange.svg)](CHANGELOG.md)

A Mindustry mod that allows non-host players to pause the game in multiplayer mode.

## ✨ Features

- 🎮 **Multiplayer Pause**: Allows any player (not just the host) to pause the game.
- 🔔 **Toast Notifications**: Displays a notification when someone pauses/unpauses the game.
- 🔐 **Permission Control**: Host can restrict pausing to admins only.
- 🔄 **Auto-Sync**: Optional auto-sync on pause/unpause to reduce desync.
- ⚡ **Responsive**: Immediate pause state updates for all clients.

## 📋 Requirements

- **Mindustry**: Version 154 or higher
- **Installation**: Both the host and players who want to pause need to install this mod.

## 🚀 Installation

### Method 1: From JAR file
1. Download `multiplayer-kit-v154.jar` from [Releases](../../releases).
2. Open Mindustry.
3. Go to **Mods** → **Open Mod Folder**.
4. Copy the JAR file into the `mods` folder.
5. Restart Mindustry.

### Method 2: Build from source
```bash
# Clone repository
git clone https://github.com/KhanhRomVN/multiplayer-pause.git
cd multiplayer-pause

# Build mod
./gradlew jar

# Output file will be at: build/libs/multiplayer-kit-v154.jar
```

## 🎯 Usage

1. **Pause game**: Press the pause key (default: `Space` or `Pause Break`).
2. **Unpause**: Press the pause key again.

### Settings Configuration

Go to **Settings** → **Multiplayer Pause** to configure:

| Setting | Description | Default |
|---------|-------------|---------|
| **Toasts** | Show notification when pausing/unpausing | ✅ On |
| **Allow Any** | Allow anyone to pause (not just admins) | ❌ Off |
| **Sync on Pause** | Auto-sync when pausing | ❌ Off |
| **Sync on Unpause** | Auto-sync when unpausing | ❌ Off |
| **Schedule Sync** | Schedule sync if a sync happened recently | ❌ Off |

> **Note**: Enabling auto-sync can help reduce desync but may cause lag if abused.

## 🔧 How it Works

1. **Client sends request**: When pause is pressed, client sends `multiplayerpause-request` packet to server.
2. **Server processes**: Server checks permissions and toggles pause state.
3. **Broadcast state**: Server sends `multiplayerpause-updatestate` packet to all clients.
4. **Clients update**: Each client updates UI and shows toast notification.

Technical details available at [docs/API.md](docs/API.md).

## 📚 Documentation

- [API Documentation](docs/API.md) - Details on packet handlers and API.
- [Settings Guide](docs/SETTINGS.md) - Detailed guide on settings.
- [Contributing](CONTRIBUTING.md) - Contribution guidelines.
- [Changelog](CHANGELOG.md) - Change history.

## 🛠️ Development

### Build Requirements
- Java 8 or higher
- Gradle (wrapper included)

### Build Commands
```bash
# Build desktop version
./gradlew jar

# Build Android version (requires Android SDK)
./gradlew jarAndroid

# Build both
./gradlew deploy
```

### Project Structure
```
multiplayer-pause/
├── src/
│   └── pauseMod/
│       └── Main.java          # Main mod class
├── assets/
│   └── bundles/
│       └── bundle.properties  # Localization
├── docs/                      # Documentation
├── build.gradle               # Build configuration
├── mod.hjson                  # Mod metadata
└── README.md                  # This file
```

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

See [CONTRIBUTING.md](CONTRIBUTING.md) for how to contribute.

## 📝 License

This project is distributed under the [MIT License](LICENSE).

## 👤 Author

**KhanhRomVN**

- GitHub: [@KhanhRomVN](https://github.com/KhanhRomVN)

## 🙏 Credits

- Original mod by [buthed010203](https://github.com/buthed010203)
- [Mindustry](https://github.com/Anuken/Mindustry) by Anuken

## ⭐ Support

If this mod is helpful, please give it a ⭐ on GitHub!

---

<p align="center">Made with ❤️ for the Mindustry community</p>
