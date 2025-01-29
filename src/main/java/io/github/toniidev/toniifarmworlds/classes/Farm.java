package io.github.toniidev.toniifarmworlds.classes;

import io.github.toniidev.toniifarmworlds.database.DatabaseItem;
import io.github.toniidev.toniifarmworlds.database.DatabaseManager;
import io.github.toniidev.toniifarmworlds.utils.InitializeUtils;
import io.github.toniidev.toniifarmworlds.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Farm extends DatabaseItem<Farm> implements Listener {
    // A thread-safe list to hold all the farms (terrains)
    private static final List<Farm> terrains = new ArrayList<>();

    // The UUID of the player who owns this farm
    private UUID owner;

    // A thread-safe list of UUIDs of players who are whitelisted to access this farm
    private final List<UUID> whitelist = new CopyOnWriteArrayList<>();

    // The name of the world's farm
    private String worldName;

    // A list to keep track of actions performed on the farm
    private final List<HistoryAction> history = new ArrayList<>();

    /**
     * Constructor for creating a new farm with a specific owner.
     * @param owner The owner of the farm (player).
     */
    public Farm(Player owner) {
        super(InitializeUtils.mainInstance, "farms", Farm.getTerrains());
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.owner = owner.getUniqueId();
        this.worldName = "farm_" + this.owner;

        // Add the created farm to the list of terrains
        Farm.terrains.add(this);
        this.save();
    }

    /**
     * Default constructor for registering the Listener.
     */
    public Farm() {
        super(InitializeUtils.mainInstance, "farms", Farm.getTerrains());
    }

    /**
     * Retrieves the list of all farms (terrains).
     * @return The list of terrains.
     */
    public static List<Farm> getTerrains() {
        return Farm.terrains;
    }

    /**
     * Loads farm data from the database.
     * @param plugin The plugin instance.
     */
    public static void load(Plugin plugin) {
        try {
            List<Farm> loadedFarms = DatabaseManager.load(new File(plugin.getDataFolder(), "farms.db"), Farm.class);
            Farm.getTerrains().addAll(loadedFarms);
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to load farms from database", e);
        }
    }

    /**
     * Gets the owner of the farm as a Player object.
     * @return The owner (player) of the farm.
     */
    public Player getOwnerAsPlayer() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * Gets the UUID of the owner of the farm.
     * @return The owner's UUID.
     */
    public UUID getOwner() {
        return this.owner;
    }

    /**
     * Sets the UUID of the owner of the farm.
     * @param owner The owner's UUID.
     */
    public void setOwner(UUID owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.owner = owner;
        this.save();
    }

    /**
     * Gets the list of UUIDs of players who are whitelisted to access the farm.
     * @return The list of whitelisted player UUIDs.
     */
    public List<UUID> getWhitelist() {
        return whitelist;
    }

    /**
     * Gets the name of the farm's world.
     * @return The world name.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the name of the farm's world.
     * @param worldName The world name.
     */
    public void setWorldName(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            throw new IllegalArgumentException("World name cannot be null or empty");
        }
        this.worldName = worldName;
        this.save();
    }

    /**
     * Gets the history of actions performed on the farm.
     * @return The history of actions.
     */
    public List<HistoryAction> getHistory() {
        return history;
    }

    /**
     * Gets the world associated with the farm.
     * If the world doesn't exist, it creates a new world with the farm's world name.
     * @return The world associated with the farm.
     */
    public World getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldName))
                .orElseGet(() -> Bukkit.createWorld(new WorldCreator(worldName)));
    }

    /**
     * Creates a new world for the farm.
     * @return The created farm world.
     */
    public World createWorld() {
        return Bukkit.createWorld(new WorldCreator(worldName));
    }

    /**
     * Creates a new world for the farm by cloning a template world.
     * @param templateWorldName The name of the world to clone.
     * @return The created farm world.
     */
    public World createWorld(String templateWorldName) {
        if (templateWorldName == null || templateWorldName.isEmpty()) {
            throw new IllegalArgumentException("Template world name cannot be null or empty");
        }
        try {
            WorldUtils.cloneWorld(templateWorldName, worldName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone world", e);
        }
        return Bukkit.createWorld(new WorldCreator(worldName));
    }

    /**
     * Checks if the player owns a farm.
     * @param player The player to check.
     * @return True if the player owns a farm, otherwise false.
     */
    public static boolean doesPlayerOwnATerrain(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return reverse(player).isPresent();
    }

    /**
     * Finds the farm that belongs to the specified player.
     * @param owner The player who owns the farm.
     * @return An Optional containing the farm, or empty if not found.
     */
    public static Optional<Farm> reverse(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        return Farm.getTerrains().stream()
                .filter(farm -> farm.getOwnerAsPlayer().equals(owner))
                .findFirst();
    }

    /**
     * Checks if the given world is a farm.
     * @param world The world to check.
     * @return True if the world is a farm, otherwise false.
     */
    public static boolean isFarm(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        return reverse(world).isPresent();
    }

    /**
     * Finds the farm associated with the given world.
     * @param world The world to check.
     * @return An Optional containing the farm, or empty if not found.
     */
    public static Optional<Farm> reverse(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        return Farm.getTerrains().stream()
                .filter(farm -> farm.getWorld().equals(world))
                .findFirst();
    }

    /**
     * Retrieves a list of farms that the specified player is whitelisted in.
     * @param player The player to check.
     * @return A list of farms the player is whitelisted in.
     */
    public static List<Farm> getFarmsPlayerIsWhitelistedIn(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return Farm.getTerrains().stream()
                .filter(farm -> farm.getWhitelist().contains(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    /**
     * Adds a player to the whitelist of the farm.
     * @param player The player to add.
     * @return The farm instance.
     */
    public Farm whitelistPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (!this.whitelist.contains(player.getUniqueId())) {
            this.whitelist.add(player.getUniqueId());
            this.save();
        }
        return this;
    }

    /**
     * Removes a player from the whitelist of the farm.
     * @param player The player to remove.
     * @return The farm instance.
     */
    public Farm removeFromWhitelist(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (this.whitelist.remove(player.getUniqueId())) {
            this.save();
        }
        return this;
    }

    /**
     * Checks if the specified player is whitelisted for this farm.
     * @param player The player to check.
     * @return True if the player is whitelisted, otherwise false.
     */
    public boolean isWhitelisted(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return this.whitelist.contains(player.getUniqueId());
    }

    /**
     * Registers a player's access to the farm.
     * @param player The player who accessed the farm.
     */
    public void registerAccess(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        history.add(new HistoryAccess(player.getDisplayName()));
        this.save();
    }

    /**
     * Registers a player's exit from the farm.
     * @param player The player who left the farm.
     */
    public void registerLeave(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        history.add(new HistoryLeave(player.getDisplayName()));
        this.save();
    }

    /**
     * Event handler for when a player switches worlds.
     * It registers the player leaving one farm and accessing another farm.
     * @param e The event triggered by a player changing worlds.
     */
    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        if (e == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        // Register leave from the world if the player was in a farm
        Farm.reverse(e.getFrom()).ifPresent(farm -> farm.registerLeave(e.getPlayer()));

        // Register access to the world if the player is entering a farm
        Farm.reverse(e.getPlayer().getWorld()).ifPresent(farm -> farm.registerAccess(e.getPlayer()));
    }
}