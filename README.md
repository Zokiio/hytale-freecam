# Freecam for Hytale

A server-side camera mod for Hytale that allows players to fly around freely, inspired by the [Minecraft Freecam mod](https://github.com/MinecraftFreecam/Freecam).

## Features

- **Toggle freecam mode** with `/freecam` or `/fc` commands
- **Free-flying camera** that detaches from your player
- **Adjustable speed** (1-10 range) for camera movement
- **Block interaction prevention** while in freecam mode
- **Per-player state management** with position restoration
- **Lightweight server-side implementation**

## Commands

| Command | Description |
|---------|-------------|
| `/freecam` | Toggle freecam mode on/off |
| `/freecam <1-10>` | Enable freecam with specific speed (1=slow, 10=fast) |
| `/fc` | Short alias for `/freecam` |
| `/fc <1-10>` | Short alias with speed parameter |

## Installation

### For Server Owners

1. Download the latest `Freecam-x.x.x.jar` from [Releases](https://github.com/Zokiio/hytale-freecam/releases)
2. Copy it to your Hytale server's `mods/` folder
3. Start the server with mods enabled: `java -jar HytaleServer.jar --allow-op --mods=./mods`
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
- `CommandBase` - Base class for commands
- `CommandContext` - Command execution context

### Camera System
- `SetFlyCameraMode` - Packet to enable/disable fly camera mode on the client
- `PlayerRef.getPacketHandler().write()` - Send packets to players

### Entity System
- `Player` - Player entity class
- `PlayerRef` - Player reference component for packet handling

### Logging
- `HytaleLogger` - Logging system for plugins

## Project Structure

```
src/main/java/net/freecam/hytale/
├── FreecamPlugin.java      # Main plugin entry point
├── FreecamState.java       # Per-player state management
└── command/
    └── FreecamCommand.java  # Main toggle command
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