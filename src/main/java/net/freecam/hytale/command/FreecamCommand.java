package net.freecam.hytale.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

import net.freecam.hytale.FreecamState;
import net.freecam.hytale.FreecamState.FreecamData;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Main command to toggle freecam mode on/off with optional speed control.
 * Usage: /freecam or /freecam <speed (1-10)>
 * 
 * When enabled, the player's camera detaches and can fly freely through the world.
 * When disabled, the camera returns to the player's original position.
 */
public class FreecamCommand extends CommandBase {

    private static final Message MSG_ENABLED = Message.raw("Freecam enabled! Fly freely with WASD. Use /freecam again to disable.").color("green");
    private static final Message MSG_DISABLED = Message.raw("Freecam disabled. Camera returned to player.").color("red");
    private static final Message MSG_PLAYER_ONLY = Message.raw("This command can only be used by players.").color("red");
    private static final Message MSG_INVALID_SPEED = Message.raw("Speed must be a number between 1 and 10.").color("red");

    public FreecamCommand() {
        super("freecam", "Toggle freecam mode - fly your camera freely through the world. Usage: /freecam [speed 1-10]");
        this.setPermissionGroup(GameMode.Adventure); // Allow all players to use
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        // Ensure sender is a player
        if (!ctx.isPlayer()) {
            ctx.sendMessage(MSG_PLAYER_ONLY);
            return;
        }

        Player player = ctx.senderAs(Player.class);
        UUID playerId = player.getUuid();
        FreecamState state = FreecamState.getInstance();
        FreecamData data = state.getData(playerId);

        // Try to parse speed from arguments
        Integer speed = parseSpeed(ctx);

        if (state.isFreecamEnabled(playerId)) {
            // If speed argument provided and freecam is active, update speed
            if (speed != null) {
                state.setSpeed(playerId, speed);
                ctx.sendMessage(Message.raw("Freecam speed set to " + speed + ".").color("green"));
            } else {
                // Otherwise, disable freecam
                disableFreecam(player, data);
                ctx.sendMessage(MSG_DISABLED);
            }
        } else {
            // Enable freecam
            if (speed != null) {
                state.setSpeed(playerId, speed);
            }
            enableFreecam(player, data);
            ctx.sendMessage(MSG_ENABLED);
        }
    }

    /**
     * Try to parse speed from command arguments.
     */
    private Integer parseSpeed(@Nonnull CommandContext ctx) {
        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+");

        // Check for /freecam <number>
        if (parts.length > 1) {
            try {
                int speed = Integer.parseInt(parts[1]);
                if (speed >= 1 && speed <= 10) {
                    return speed;
                }
            } catch (NumberFormatException e) {
                // Not a number, ignore
            }
        }
        return null;
    }

    /**
     * Enable freecam mode for a player.
     * Uses the built-in fly camera mode which handles all the complex camera settings.
     */
    private void enableFreecam(Player player, FreecamData data) {
        UUID playerId = player.getUuid();
        FreecamState state = FreecamState.getInstance();

        // Store original position (simplified - position tracking can be enhanced later)
        state.enableFreecam(playerId, 0, 0, 0, 0, 0);

        // Use the built-in fly camera mode - this is the safest approach
        // The fly camera mode is designed for free camera movement
        SetFlyCameraMode flyMode = new SetFlyCameraMode(true);
        player.getPlayerConnection().write(flyMode);
    }

    /**
     * Disable freecam mode and return camera to player.
     */
    private void disableFreecam(Player player, FreecamData data) {
        UUID playerId = player.getUuid();
        FreecamState state = FreecamState.getInstance();

        // Disable fly camera mode - this returns to normal camera
        SetFlyCameraMode flyMode = new SetFlyCameraMode(false);
        player.getPlayerConnection().write(flyMode);

        // Mark as disabled
        state.disableFreecam(playerId);
    }
}
