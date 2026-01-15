package net.freecam.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import net.freecam.hytale.command.FCCommand;
import net.freecam.hytale.command.FreecamCommand;

import javax.annotation.Nonnull;

/**
 * Freecam for Hytale - A camera mod that allows you to fly around freely.
 * Inspired by the Minecraft Freecam mod.
 * 
 * Features:
 * - Toggle freecam mode with /freecam command
 * - Adjustable flight speed
 * - Configurable settings per player
 */
public class FreecamPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static FreecamPlugin instance;

    public FreecamPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Freecam " + this.getManifest().getVersion().toString() + " is loading...");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up Freecam plugin...");
        
        // Register main freecam toggle command
        this.getCommandRegistry().registerCommand(new FreecamCommand());
        
        // Register short alias
        this.getCommandRegistry().registerCommand(new FCCommand());
        
        LOGGER.atInfo().log("Freecam plugin setup complete!");
        LOGGER.atInfo().log("Use /freecam or /fc to toggle freecam mode.");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Freecam plugin shut down.");
    }

    /**
     * Get the singleton instance of the plugin.
     */
    public static FreecamPlugin getInstance() {
        return instance;
    }
}
