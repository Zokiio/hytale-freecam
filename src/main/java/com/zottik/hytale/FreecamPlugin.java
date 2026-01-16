package com.zottik.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.zottik.hytale.command.FreecamCommand;
import com.zottik.hytale.event.FreecamBreakBlockEventSystem;
import com.zottik.hytale.event.FreecamDamageBlockEventSystem;

import javax.annotation.Nonnull;

/**
 * Freecam for Hytale - A camera mod that allows you to fly around freely.
 * Inspired by the Minecraft Freecam mod.
 * 
 * Features:
 * - Toggle freecam mode with /freecam or /fc command
 * - Adjustable flight speed (1-10 range) with --speed parameter
 * - Prevents block breaking while in freecam mode
 * - Restores player position when disabling freecam
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
        
        // Register freecam command with /fc alias
        this.getCommandRegistry().registerCommand(new FreecamCommand());
        
        // Register event systems to prevent block interactions during freecam
        EntityStore.REGISTRY.registerSystem(new FreecamBreakBlockEventSystem());
        EntityStore.REGISTRY.registerSystem(new FreecamDamageBlockEventSystem());
        
        LOGGER.atInfo().log("Freecam plugin setup complete!");
        LOGGER.atInfo().log("Use /freecam or /fc to toggle freecam mode.");
        LOGGER.atInfo().log("Use /freecam --speed <1-10> to adjust camera speed.");
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
