package com.zottik.hytale.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.zottik.hytale.FreecamState;
import com.zottik.hytale.FreecamState.FreecamData;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Main command to toggle freecam mode with optional speed control.
 * Usage: 
 *   /freecam - Toggle freecam on/off
 *   /freecam --speed 3 - Enable with speed 3
 *   /fc --speed 5 - Short alias with speed
 * 
 * When enabled, the player's camera detaches and can fly freely through the world.
 * When disabled, the camera returns to the player's original position.
 */
public class FreecamCommand extends AbstractPlayerCommand {

    private static final Message MSG_ENABLED = Message.raw("Freecam enabled! Fly freely with WASD. Use /freecam again to disable.").color("green");
    private static final Message MSG_DISABLED = Message.raw("Freecam disabled. Camera returned to player.").color("red");

    private final OptionalArg<Integer> speedArg;
    private final OptionalArg<Boolean> showPlayerArg;

    public FreecamCommand() {
        super("freecam", "Toggle freecam mode with optional speed control. Usage: /freecam [--speed 1-10] [--show-player true/false]");
        this.speedArg = withOptionalArg("speed", "Freecam speed (1-10)", new FreecamSpeedArgumentType());
        this.showPlayerArg = withOptionalArg("show-player", "Show player model (true/false)", new ShowPlayerArgumentType());
        this.addAliases("fc"); // Add short alias
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> entityRef,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {
        UUID playerId = playerRef.getUuid();
        FreecamState state = FreecamState.getInstance();

        // Get speed argument if provided
        Integer speed = context.provided(speedArg) ? context.get(speedArg) : null;
        
        // Get show-player argument if provided (requires explicit value)
        Boolean showPlayer = null;
        if (context.provided(showPlayerArg)) {
            showPlayer = context.get(showPlayerArg);
        } else {
            String input = context.getInputString();
            if (input != null && input.toLowerCase().contains("--show-player")) {
                context.sendMessage(Message.raw("Please provide a value for --show-player (true/false).").color("red"));
                return;
            }
        }

        boolean wasEnabled = state.isFreecamEnabled(playerId);
        // Update speed if provided
        if (speed != null) {
            state.setSpeed(playerId, speed);
            context.sendMessage(Message.raw("Freecam speed set to " + speed + ". Will apply on next toggle.").color("gray"));
        }
        
        // Update show-player preference if provided
        if (showPlayer != null) {
            state.setShowPlayer(playerId, showPlayer);
            context.sendMessage(Message.raw("Show player set to " + (showPlayer ? "enabled" : "disabled") + ". Will apply on next toggle.").color("gray"));
        }

        if (wasEnabled && speed == null && showPlayer == null) {
            // Disable freecam only if NO arguments were provided (toggle off)
            disableFreecam(playerRef, world, store, entityRef);
            context.sendMessage(MSG_DISABLED);
        } else if (!wasEnabled) {
            // Enable freecam - detach camera
            enableFreecam(playerRef, world, store, entityRef);
            context.sendMessage(MSG_ENABLED);
        }
    }

    /**
     * Enable freecam mode for a player using SetServerCamera with proper speed control.
     */
    private void enableFreecam(PlayerRef playerRef, World world, Store<EntityStore> store, Ref<EntityStore> entityRef) {
        UUID playerId = playerRef.getUuid();
        FreecamState state = FreecamState.getInstance();

        // Get current position and rotation to save
        Transform transform = playerRef.getTransform().clone();
        Vector3f headRotation = playerRef.getHeadRotation().clone();

        // Store original state
        state.enableFreecam(
            playerId, 
            transform.getPosition().x,
            transform.getPosition().y, 
            transform.getPosition().z,
            headRotation.getYaw(),
            headRotation.getPitch()
        );
        
        // Move camera up 2 blocks from player position
        transform.getPosition().y += 2.5;

        // Get player's current speed setting
        int speed = state.getSpeed(playerId);
        boolean showPlayer = state.getShowPlayer(playerId);

        // Set player to Adventure mode to prevent block breaking
        setGameMode(store, entityRef, GameMode.Adventure);

        // Build camera settings and send packet
        ServerCameraSettings settings = buildFreecamSettings(transform, headRotation, speed, showPlayer);
        SetServerCamera packet = new SetServerCamera(ClientCameraView.Custom, true, settings);
        playerRef.getPacketHandler().writeNoCache(packet);
    }

    /**
     * Disable freecam mode and restore player's original position and state.
     */
    private void disableFreecam(PlayerRef playerRef, World world, Store<EntityStore> store, Ref<EntityStore> entityRef) {
        UUID playerId = playerRef.getUuid();
        FreecamState state = FreecamState.getInstance();
        FreecamData data = state.getData(playerId);

        // Restore original position and rotation
        Transform transform = new Transform();
        transform.getPosition().x = data.getOriginalX();
        transform.getPosition().y = data.getOriginalY();
        transform.getPosition().z = data.getOriginalZ();
        
        Vector3f headRotation = new Vector3f(
            data.getOriginalYaw(),
            data.getOriginalPitch(),
            0.0f
        );

        // Disable custom camera
        SetServerCamera packet = new SetServerCamera(ClientCameraView.Custom, false, null);
        playerRef.getPacketHandler().writeNoCache(packet);

        // Update player position
        playerRef.updatePosition(world, transform, headRotation);

        // Mark as disabled
        state.disableFreecam(playerId);
    }

    /**
     * Set player's game mode.
     */
    private void setGameMode(Store<EntityStore> store, Ref<EntityStore> entityRef, GameMode mode) {
        if (mode == null) {
            return;
        }
        Player.setGameMode(entityRef, mode, store);
    }

    /**
     * Build ServerCameraSettings for freecam mode with speed control.
     * Based on Riloox's implementation with adjustable movement multipliers.
     */
    private ServerCameraSettings buildFreecamSettings(Transform transform, Vector3f headRotation, int speed, boolean showPlayer) {
        ServerCameraSettings settings = new ServerCameraSettings();
        
        // Basic settings
        settings.positionLerpSpeed = 1.0f;
        settings.rotationLerpSpeed = 1.0f;
        settings.speedModifier = 1.0f;
        settings.allowPitchControls = true;
        settings.displayCursor = false;
        settings.displayReticle = false;
        settings.mouseInputTargetType = MouseInputTargetType.Any;
        settings.sendMouseMotion = true;
        settings.skipCharacterPhysics = true;
        settings.isFirstPerson = !showPlayer;  // First-person hides player, third-person shows player
        settings.movementForceRotationType = MovementForceRotationType.CameraRotation;
        settings.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);
        
        // Attachment settings
        settings.attachedToType = AttachedToType.None;
        settings.attachedToEntityId = 0;
        settings.eyeOffset = true;
        settings.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        settings.positionOffset = new Position(0.0, 0.0, 0.0);
        settings.rotationOffset = new Direction(0.0f, 0.0f, 0.0f);
        
        // Set camera position and rotation
        settings.positionType = PositionType.Custom;
        settings.rotationType = RotationType.Custom;
        settings.position = new Position(
            transform.getPosition().x,
            transform.getPosition().y,
            transform.getPosition().z
        );
        settings.rotation = new Direction(
            headRotation.getYaw(),
            headRotation.getPitch(),
            headRotation.getRoll()
        );
        
        // Movement settings - this is where speed control happens
        settings.canMoveType = CanMoveType.Always;
        settings.applyMovementType = ApplyMovementType.Position;
        
        // Speed multipliers based on 1-10 range
        float horizontal = Math.max(1.0f, (float) speed);
        float vertical = Math.max(0.5f, 0.4f + (speed * 0.12f));
        settings.movementMultiplier = new com.hypixel.hytale.protocol.Vector3f(horizontal, vertical, horizontal);
        
        // Look settings
        settings.applyLookType = ApplyLookType.Rotation;
        settings.lookMultiplier = new Vector2f(1.0f, 1.0f);
        settings.mouseInputType = MouseInputType.LookAtTarget;
        settings.mouseInputTargetType = MouseInputTargetType.Any;
        
        // Plane normal for movement
        settings.planeNormal = new com.hypixel.hytale.protocol.Vector3f(0.0f, 1.0f, 0.0f);
        
        return settings;
    }
}
