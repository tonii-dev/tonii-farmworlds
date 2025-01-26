package io.github.toniidev.toniifarmworlds.classes;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.*;

public class Farm implements Listener {
    // A list to hold all the terrains (farms)
    private static final List<Farm> terrains = new ArrayList<>();

    private UUID owner;
    private final List<UUID> whitelist = new ArrayList<>();
    private String worldName;
    private final List<HistoryAction> history = new ArrayList<>();

    /**
     * Constructor for creating a new farm with a specific owner.
     * @param owner The owner of the farm (player).
     */
    public Farm(Player owner) {
        this.owner = owner.getUniqueId();
        this.worldName = "farm_" + this.owner;

        // Add the created farm to the list of terrains
        Farm.terrains.add(this);
    }

    /**
     * Blank constructor for registering the Listener.
     */
    public Farm() {
    }

    /**
     * Retrieves the list of all farms (terrains).
     * @return The list of terrains.
     */
    public static List<Farm> getTerrains() {
        return Farm.terrains;
    }

    /**
     * Gets the owner of the farm.
     * @return The owner (player) of the farm.
     */
    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * Gets the whitelist of the farm (players who can access the farm).
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
     * Gets the history of actions on the farm.
     * @return The history of actions.
     */
    public List<HistoryAction> getHistory() {
        return history;
    }

    /**
     * Gets the world of the farm.
     * @return The world associated with the farm.
     */
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Creates the world for the farm.
     * @return The created farm world.
     */
    public World createWorld() {
        return Bukkit.createWorld(new WorldCreator(worldName));
    }

    /**
     * Checks if the player owns a farm.
     * @param player The player to check.
     * @return True if the player owns a farm, otherwise false.
     */
    public static boolean doesPlayerOwnATerrain(Player player) {
        return Farm.reverse(player).isPresent();
    }

    /**
     * Finds the farm that belongs to the specified player.
     * @param owner The player who owns the farm.
     * @return An Optional containing the farm, or empty if not found.
     */
    public static Optional<Farm> reverse(Player owner) {
        return Farm.getTerrains()
                .stream()
                .filter(x -> x.getOwner().equals(owner))
                .findFirst();
    }

    /**
     * Checks if the given world is a farm.
     * @param world The world to check.
     * @return True if the world is a farm, otherwise false.
     */
    public static boolean isFarm(World world) {
        return Farm.reverse(world).isPresent();
    }

    /**
     * Finds the farm associated with the given world.
     * @param world The world to check.
     * @return An Optional containing the farm, or empty if not found.
     */
    public static Optional<Farm> reverse(World world) {
        return Farm.getTerrains()
                .stream()
                .filter(x -> x.getWorld().equals(world))
                .findFirst();
    }

    /**
     * Retrieves a list of farms that the specified player is whitelisted in.
     * @param player The player to check.
     * @return A list of farms the player is whitelisted in.
     */
    public static List<Farm> getFarmsPlayerIsWhitelistedIn(Player player) {
        List<Farm> value = new ArrayList<>();
        for (Farm terrain : Farm.getTerrains()) {
            if (terrain.getWhitelist().contains(player.getUniqueId())) {
                value.add(terrain);
            }
        }
        return value;
    }

    /**
     * Adds a player to the whitelist of the farm.
     * @param player The player to add.
     * @return The farm instance.
     */
    public Farm whitelistPlayer(Player player) {
        if (!this.whitelist.contains(player.getUniqueId())) {
            this.whitelist.add(player.getUniqueId());
        }
        return this;
    }

    /**
     * Removes a player from the whitelist of the farm.
     * @param player The player to remove.
     * @return The farm instance.
     */
    public Farm removeFromWhitelist(Player player) {
        this.whitelist.remove(player.getUniqueId());
        return this;
    }

    /**
     * Registers a player's access to the farm.
     * @param player The player who accessed the farm.
     */
    public void registerAccess(Player player) {
        history.add(new HistoryAccess(player.getDisplayName()));
    }

    /**
     * Registers a player's exit from the farm.
     * @param player The player who left the farm.
     */
    public void registerLeave(Player player) {
        history.add(new HistoryLeave(player.getDisplayName()));
    }

    /**
     * Event handler for when a player switches worlds.
     * It registers the player leaving one farm and accessing another farm.
     */
    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        // Register leave from the world if the player was in a farm
        if (Farm.isFarm(e.getFrom())) {
            Farm.reverse(e.getFrom()).get().registerLeave(e.getPlayer());
        }

        // Register access to the world if the player is entering a farm
        if (Farm.isFarm(e.getPlayer().getWorld())) {
            Farm.reverse(e.getPlayer().getWorld()).get().registerAccess(e.getPlayer());
        }
    }
}
