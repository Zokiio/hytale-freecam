# Freecam for Hytale

A server-side camera mod for Hytale that allows players to fly around freely, inspired by the [Minecraft Freecam mod](https://github.com/MinecraftFreecam/Freecam).

## Features

- **Toggle freecam mode** with `/freecam` or `/fc` commands
- **Free-flying camera** that detaches from your player and starts 2.5 blocks above for clear visibility
- **Adjustable speed control** (1-10 range) with `--speed` parameter (default: 5)
- **Third-person view** by default - see your player model while flying (toggle with `--show-player`)
- **Prevents block interaction** while in freecam mode
- **Position restoration** when disabling freecam
- **Lightweight server-side implementation**

## Commands

| Command | Description |
|---------|-------------|
| `/freecam` | Toggle freecam mode on/off |
| `/freecam --speed 7` | Set speed to 7 (applies on next toggle) |
| `/freecam --show-player false` | Disable player visibility (first-person view) |
| `/freecam --show-player` | Toggle player visibility on/off |
| `/fc` | Short alias for `/freecam` |
| `/fc --speed 3 --show-player true` | Combine multiple settings |

**Note:** Speed and show-player settings are saved per-player and apply on the next freecam toggle.

## Installation

### For Server Owners

1. Download the latest `Freecam-x.x.x.jar` from [Releases](https://github.com/Zokiio/hytale-freecam/releases)
2. Copy it to your Hytale server's `mods/` folder
3. Start the server (it auto-loads mods from `mods/`)
4. Players can use `/freecam` or `/fc` to toggle freecam mode

### Building from Source

1. Clone the repository: `git clone https://github.com/Zokiio/hytale-freecam.git`
2. Set environment variables (or use default paths):
   ```bash
   export HYTALE_SERVER_JAR=/path/to/HytaleServer.jar
   export HYTALE_ASSETS_ZIP=/path/to/Assets.zip
   export HYTALE_MODS_DIR=/path/to/mods
   ```
3. Build: `./gradlew build`
4. The output JAR will be in `build/libs/`

## Hytale APIs Used

This plugin uses the following Hytale server APIs:

### Core Plugin System
- `JavaPlugin` - Base class for Java plugins
- `CommandRegistry` - For registering commands
- `AbstractPlayerCommand` - Base class for player commands
- `CommandContext` - Command execution context
- `OptionalArg` - Optional command arguments

### Camera System
- `SetServerCamera` - Packet to enable/disable custom camera with full settings
- `ServerCameraSettings` - Camera configuration (speed, position, rotation, view mode)
- `ClientCameraView` - Camera view modes (Custom, FirstPerson, etc.)
- `PlayerRef.getPacketHandler().writeNoCache()` - Send packets to players

### Entity System
- `Player` - Player entity class with game mode control
- `PlayerRef` - Player reference component with transform and packet handling
- `Transform` - Player position in world space
- `Vector3f` - 3D vector for position and rotation
- `EntityStore` - Entity component storage system

### Event System
- `EntityEventSystem` - Base class for entity event handlers
- `BreakBlockEvent` - Event fired when a block is broken
- `DamageBlockEvent` - Event fired when a block is damaged

### Logging
- `HytaleLogger` - Logging system for plugins

## Project Structure

```
src/main/java/com/zottik/hytale/
├── FreecamPlugin.java          # Main plugin entry point
├── FreecamState.java           # Per-player state management
├── command/
│   ├── FreecamCommand.java             # Main toggle command with arguments
│   ├── FreecamSpeedArgumentType.java   # Speed argument validator (1-10)
│   └── ShowPlayerArgumentType.java     # Boolean argument parser
└── event/
    ├── FreecamBreakBlockEventSystem.java   # Prevent block breaking in freecam
    └── FreecamDamageBlockEventSystem.java  # Prevent block damage in freecam
```

## Building

Requires Java 25 and Gradle.

```bash
./gradlew build
```

The output JAR will be in `build/libs/`.

## Requirements

### For Users
- Hytale Server with mods support

### For Development
- Hytale Server JAR and Assets.zip
- Java 25+
- Gradle 9.x (included via wrapper)

## License

MIT

## Links

- GitHub: [github.com/Zokiio/hytale-freecam](https://github.com/Zokiio/hytale-freecam)