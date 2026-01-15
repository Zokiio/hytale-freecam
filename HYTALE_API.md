# Hytale Server API Documentation

This document contains findings from exploring the Hytale server API for plugin development.

*Last Updated: January 14, 2026*

---

## Core Plugin System

### Base Classes
- **`JavaPlugin`** - Base class for all Java plugins
  - `setup()` - Called when plugin is being set up
  - `shutdown()` - Called when plugin is shutting down
  - `getCommandRegistry()` - Access to command registration
  - `getManifest()` - Access to plugin manifest data

- **`JavaPluginInit`** - Initialization data passed to plugin constructor

### Logging
- **`HytaleLogger`** - Logging system
  - `HytaleLogger.forEnclosingClass()` - Get logger for current class
  - `.atInfo().log(message)` - Log info messages
  - `.atWarn().log(message)` - Log warnings
  - `.atError().log(message)` - Log errors

---

## Command System

### Classes
- **`CommandBase`** - Base class for commands
  - Constructor: `CommandBase(String name, String description)`
  - `executeSync(CommandContext ctx)` - Override to implement command logic
  - `setPermissionGroup(GameMode)` - Set permission requirements

- **`CommandContext`** - Command execution context
  - `isPlayer()` - Check if sender is a player
  - `senderAs(Class<T>)` - Cast sender to specific type
  - `sendMessage(Message)` - Send message to command sender

- **`CommandRegistry`** - Registry for commands
  - `registerCommand(CommandBase)` - Register a command

### Message System
- **`Message`** - Message builder
  - `Message.raw(String)` - Create raw text message
  - `.color(String)` - Set color (e.g., "green", "red")

---

## Entity System

### Player
- **`Player`** - Player entity class
  - ⚠️ `getUuid()` - Get player UUID (deprecated, marked for removal)
  - ⚠️ `getPlayerRef()` - Get player reference (deprecated, marked for removal)

- **`PlayerRef`** - Player reference component
  - `getPacketHandler()` - Get packet handler for sending packets
  - `PacketHandler.write(Packet)` - Send packet to player

---

## Camera System

### Packets

#### Outgoing (Server → Client)
- **`SetFlyCameraMode`** - Enable/disable fly camera mode
  - Constructor: `SetFlyCameraMode(boolean enable)`
  - When `true`: Enables free camera movement
  - When `false`: Returns camera to normal player view

- **`SetServerCamera`** - Set camera to specific target
  - Location: `com.hypixel.hytale.protocol.packets.camera.SetServerCamera`

- **`CameraShakeEffect`** - Apply camera shake effect
  - Location: `com.hypixel.hytale.protocol.packets.camera.CameraShakeEffect`

#### Incoming (Client → Server)
- **`RequestFlyCameraMode`** - Client requests to toggle fly camera (F6 key)
  - Location: `com.hypixel.hytale.protocol.packets.camera.RequestFlyCameraMode`
  - Field: `boolean entering` - Whether entering or exiting fly camera mode
  - **Handled by**: `GamePacketHandler.handle(RequestFlyCameraMode)`
  - ⚠️ **Not currently interceptable by plugins** - no public event exposed

---

## Event System

### Core Event Classes
- **`IEventBus`** - Event bus interface
- **`EventBus`** - Main event bus implementation
- **`EventRegistry`** - Event registration system
- **`EventPriority`** - Priority levels for event handlers
- **`ICancellable`** - Interface for cancellable events
- **`IEvent`** - Base event interface
- **`IAsyncEvent`** - Marker for async events

### Event Registries
- **`SyncEventBusRegistry`** - Synchronous event bus
- **`AsyncEventBusRegistry`** - Asynchronous event bus

### Available Player Events
Located in `com.hypixel.hytale.server.core.event.events.player`:

- **`PlayerChatEvent`** - Player sends chat message
- **`PlayerReadyEvent`** - Player is ready to join world
- **`PlayerInteractEvent`** - Player interacts with entity/block
- **`PlayerMouseButtonEvent`** - Mouse button press
- **`PlayerMouseMotionEvent`** - Mouse movement
- **`PlayerSetupConnectEvent`** - Player connecting (setup phase)
- **`PlayerSetupDisconnectEvent`** - Player disconnecting (setup phase)
- **`AddPlayerToWorldEvent`** - Player being added to world

### Other Event Categories
- **Permission Events**: `PlayerPermissionChangeEvent`, `PlayerGroupEvent`
- **Asset Events**: `AssetPackRegisterEvent`, `AssetPackUnregisterEvent`, `LoadAssetEvent`
- **Plugin Events**: `PluginSetupEvent`
- **Window Events**: `Window.WindowCloseEvent`

---

## Packet Handler System

### Packet Handler Classes
- **`PacketHandler`** - Main packet handler
- **`IPacketHandler`** - Packet handler interface
- **`GamePacketHandler`** - Game packet handler (handles player packets)
  - Extends `GenericPacketHandler`
  - Implements `IPacketHandler`

### GamePacketHandler Methods (Partial List)
The following `handle()` methods are available in GamePacketHandler:

- `handle(Disconnect)`
- `handle(MouseInteraction)`
- `handle(ClientMovement)`
- `handle(ChatMessage)`
- `handle(CustomPageEvent)`
- `handle(ClientPlaceBlock)`
- `handle(CloseWindow)`
- `handle(ClientReady)`
- **`handle(RequestFlyCameraMode)`** ⭐ - Fly camera request from client
- `handle(SyncInteractionChains)`
- `handle(MountMovement)`
- And many more...

⚠️ **Important**: These handlers are internal and not currently exposed to plugins for interception.

---

## Custom UI System

### UI Pages
- **`CustomUIPage`** - Base class for custom UI pages
- **`BasicCustomUIPage`** - Simple custom pages
- **`InteractiveCustomUIPage`** - Interactive pages with event handling

### UI Builders
- **`UICommandBuilder`** - Build UI elements
  - Set text, values, properties

- **`UIEventBuilder`** - Bind events to UI elements
  - Event types: `CustomUIEventBindingType`

### Custom UI Event Types
From `CustomUIEventBindingType` enum:
- `KeyDown` - Keyboard key press (only when UI has focus)
- `Activating` - Element activated
- `RightClicking` - Right click
- `DoubleClicking` - Double click
- `MouseEntered` - Mouse hover start
- `MouseExited` - Mouse hover end
- `ValueChanged` - Value changed
- `Dismissing` - UI dismissed

### HUD System
- **`CustomHud`** - Packet for HUD overlays
- **`PageManager.openCustomPage()`** - Display custom pages to players

⚠️ **Limitation**: KeyDown events only work when UI page has focus, not as global keybinds during gameplay.

---

## Asset System

### Asset Packets
Partial list of asset packet generators:
- `BlockTypePacketGenerator`
- `ItemCategoryPacketGenerator`
- `ParticleSystemPacketGenerator`
- `SoundEventPacketGenerator`
- `FluidTypePacketGenerator`
- And many more...

---

## Protocol Information

### Packet Interface
- **`com.hypixel.hytale.protocol.Packet`** - Base packet interface
  - `getId()` - Get packet ID
  - `serialize(ByteBuf)` - Serialize packet
  - `deserialize(ByteBuf, int)` - Deserialize packet
  - `computeSize()` - Calculate packet size

---

## Known Limitations & Gaps

### Missing Plugin APIs (as of Jan 2026)
1. **Packet Interception** - No public API to intercept/listen to incoming client packets
2. **Global Key Bindings** - No way to register global keybinds that work during gameplay
3. **Item Interaction Events** - Unclear if item right-click events are exposed
4. **Player Input Events** - No direct access to player input (WASD, sneak, etc.)

### Deprecated APIs
The following are marked for removal in future versions:
- `Player.getUuid()` - Use alternative (TBD)
- `Player.getPlayerRef()` - Use alternative (TBD)

---

## Recommendations

### For Plugin Developers
1. **Commands**: Use `CommandBase` and `CommandRegistry` - these are stable
2. **Camera Control**: `SetFlyCameraMode` packet works reliably
3. **Events**: Use the EventBus system for high-level game events
4. **Avoid**: Don't rely on deprecated methods or undocumented internal APIs

### Potential Feature Requests to Hypixel
1. **`PlayerRequestFlyCameraEvent`** - Event fired when player presses F6
2. **`PlayerItemInteractEvent`** - Event for item right-clicks
3. **Global Keybind API** - Register custom keybinds for plugins
4. **Packet Listener API** - Public API for intercepting specific packet types

---

## Exploration Commands

### Useful Commands for API Discovery
```bash
# List all classes in Hytale server JAR
jar -tf HytaleServer.jar

# Search for specific patterns
jar -tf HytaleServer.jar | grep -i "camera"
jar -tf HytaleServer.jar | grep -i "event"
jar -tf HytaleServer.jar | grep -i "packet"

# Inspect class structure
javap -cp HytaleServer.jar com.hypixel.hytale.protocol.packets.camera.RequestFlyCameraMode
```

---

## Version Info
- **Hytale Version**: Early Access (January 2026)
- **Server JAR**: HytaleServer.jar
- **Java Version**: 25
- **Status**: API is incomplete and subject to change
