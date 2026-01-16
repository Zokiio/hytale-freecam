package net.freecam.hytale;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages freecam state for all players.
 * Tracks who has freecam enabled, their settings, and original positions.
 */
public class FreecamState {

    private static final FreecamState INSTANCE = new FreecamState();
    private static final int DEFAULT_SPEED = 5;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 10;

    // Player UUID -> FreecamData
    private final Map<UUID, FreecamData> playerStates = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> playerSpeeds = new ConcurrentHashMap<>();

    private FreecamState() {}

    public static FreecamState getInstance() {
        return INSTANCE;
    }

    /**
     * Check if a player has freecam enabled.
     */
    public boolean isFreecamEnabled(UUID playerId) {
        FreecamData data = playerStates.get(playerId);
        return data != null && data.isEnabled();
    }

    /**
     * Enable freecam for a player, storing their original position.
     */
    public void enableFreecam(UUID playerId, double x, double y, double z, float yaw, float pitch) {
        FreecamData data = playerStates.computeIfAbsent(playerId, id -> new FreecamData());
        data.setEnabled(true);
        data.setOriginalPosition(x, y, z);
        data.setOriginalRotation(yaw, pitch);
    }

    /**
     * Disable freecam for a player.
     */
    public void disableFreecam(UUID playerId) {
        FreecamData data = playerStates.get(playerId);
        if (data != null) {
            data.setEnabled(false);
        }
    }

    /**
     * Get freecam data for a player.
     */
    public FreecamData getData(UUID playerId) {
        return playerStates.computeIfAbsent(playerId, id -> new FreecamData());
    }

    /**
     * Remove all data for a player (on disconnect).
     */
    public void removePlayer(UUID playerId) {
        playerStates.remove(playerId);
        playerSpeeds.remove(playerId);
    }

    /**
     * Get freecam speed for a player.
     */
    public int getSpeed(UUID playerId) {
        return playerSpeeds.getOrDefault(playerId, DEFAULT_SPEED);
    }

    /**
     * Set freecam speed for a player.
     */
    public void setSpeed(UUID playerId, int speed) {
        int clamped = clampSpeed(speed);
        playerSpeeds.put(playerId, clamped);
    }

    /**
     * Clamp speed to valid range.
     */
    private int clampSpeed(int speed) {
        if (speed < MIN_SPEED) return MIN_SPEED;
        if (speed > MAX_SPEED) return MAX_SPEED;
        return speed;
    }

    /**
     * Data class holding freecam state for a single player.
     */
    public static class FreecamData {
        private boolean enabled = false;
        
        // Original position when freecam was enabled
        private double originalX, originalY, originalZ;
        private float originalYaw, originalPitch;
        
        // Settings
        private float horizontalSpeed = 1.0f;
        private float verticalSpeed = 1.0f;
        private boolean showPlayer = true;
        private boolean disableOnDamage = true;
        private boolean freezePlayer = true;
        private boolean ignoreCollision = true;
        private InitialPerspective initialPerspective = InitialPerspective.FIRST_PERSON;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setOriginalPosition(double x, double y, double z) {
            this.originalX = x;
            this.originalY = y;
            this.originalZ = z;
        }

        public void setOriginalRotation(float yaw, float pitch) {
            this.originalYaw = yaw;
            this.originalPitch = pitch;
        }

        public double getOriginalX() { return originalX; }
        public double getOriginalY() { return originalY; }
        public double getOriginalZ() { return originalZ; }
        public float getOriginalYaw() { return originalYaw; }
        public float getOriginalPitch() { return originalPitch; }

        public float getHorizontalSpeed() { return horizontalSpeed; }
        public void setHorizontalSpeed(float speed) { this.horizontalSpeed = speed; }

        public float getVerticalSpeed() { return verticalSpeed; }
        public void setVerticalSpeed(float speed) { this.verticalSpeed = speed; }

        public boolean isShowPlayer() { return showPlayer; }
        public void setShowPlayer(boolean show) { this.showPlayer = show; }

        public boolean isDisableOnDamage() { return disableOnDamage; }
        public void setDisableOnDamage(boolean disable) { this.disableOnDamage = disable; }

        public boolean isFreezePlayer() { return freezePlayer; }
        public void setFreezePlayer(boolean freeze) { this.freezePlayer = freeze; }

        public boolean isIgnoreCollision() { return ignoreCollision; }
        public void setIgnoreCollision(boolean ignore) { this.ignoreCollision = ignore; }

        public InitialPerspective getInitialPerspective() { return initialPerspective; }
        public void setInitialPerspective(InitialPerspective perspective) { this.initialPerspective = perspective; }
    }

    /**
     * Initial camera perspective options.
     */
    public enum InitialPerspective {
        FIRST_PERSON,
        THIRD_PERSON,
        INSIDE
    }
}
