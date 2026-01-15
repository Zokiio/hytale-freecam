package net.freecam.hytale.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

import net.freecam.hytale.FreecamState;
import net.freecam.hytale.FreecamState.FreecamData;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Short alias for the freecam command.
 * Usage: /fc
 * 
 * This is a convenience command that does exactly the same as /freecam
 * but is faster to type.
 */
public class FCCommand extends CommandBase {

    private static final Message MSG_ENABLED = Message.raw("Freecam enabled! Fly freely with WASD. Use /fc again to disable.").color("green");
    private static final Message MSG_DISABLED = Message.raw("Freecam disabled. Camera returned to player.").color("red");
    private static final Message MSG_PLAYER_ONLY = Message.raw("This command can only be used by players.").color("red");

    public FCCommand() {
        super("fc", "Toggle freecam mode (short alias)");
        this.setPermissionGroup(GameMode.Adventure);
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

        if (state.isFreecamEnabled(playerId)) {
            // Disable freecam
            disableFreecam(player, data);
            ctx.sendMessage(MSG_DISABLED);
        } else {
            // Enable freecam
            enableFreecam(player, data);
            ctx.sendMessage(MSG_ENABLED);
        }
    }

    private void enableFreecam(Player player, FreecamData data) {
        UUID playerId = player.getUuid();
        FreecamState state = FreecamState.getInstance();

        state.enableFreecam(playerId, 0, 0, 0, 0, 0);

        com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode flyMode = 
            new com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode(true);
        player.getPlayerConnection().write(flyMode);
    }

    private void disableFreecam(Player player, FreecamData data) {
        UUID playerId = player.getUuid();
        FreecamState state = FreecamState.getInstance();

        com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode flyMode = 
            new com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode(false);
        player.getPlayerConnection().write(flyMode);

        state.disableFreecam(playerId);
    }
}
