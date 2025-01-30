package io.github.toniidev.toniifarmworlds.link;

import io.github.toniidev.toniifarmworlds.database.DatabaseItem;
import io.github.toniidev.toniifarmworlds.database.DatabaseManager;
import io.github.toniidev.toniifarmworlds.factories.InventoryFactory;
import io.github.toniidev.toniifarmworlds.factories.MultipleInventoryFactory;
import io.github.toniidev.toniifarmworlds.utils.InitializeUtils;
import io.github.toniidev.toniifarmworlds.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class ServerPlayer extends DatabaseItem<ServerPlayer> {
    private static final List<ServerPlayer> players = new ArrayList<>();

    private UUID playerId;
    private Plugin plugin;
    private String displayName;

    private int maxSingleTasks = 3;
    private final List<SingleTask> singleTasks = new ArrayList<>();

    private int maxMultipleTasks = 9;
    private final List<MultipleTask> multipleTasks = new ArrayList<>();

    private double money;

    /**
     * Constructs a new ServerPlayer instance.
     *
     * @param player The Bukkit Player.
     * @param plugin The main plugin instance.
     */
    public ServerPlayer(Player player, Plugin plugin) {
        super(plugin, "players", ServerPlayer.getPlayers());
        this.playerId = player.getUniqueId();
        this.plugin = plugin;
        this.displayName = player.getDisplayName();

        startTaskLoops();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ServerPlayer(){
        super(InitializeUtils.mainInstance, null, null);
    }

    /**
     * Increases the maximum number of single tasks allowed.
     *
     * @return This ServerPlayer instance for chaining.
     */
    public ServerPlayer increaseMaxSingleTaskAmount() {
        this.maxSingleTasks++;
        this.save();
        return this;
    }

    /**
     * Increases the maximum number of multiple tasks allowed.
     *
     * @return This ServerPlayer instance for chaining.
     */
    public ServerPlayer increaseMaxMultipleTaskAmount() {
        this.maxMultipleTasks++;
        this.save();
        return this;
    }

    /**
     * Starts the task generation loops for single and multiple tasks.
     */
    private void startTaskLoops() {
        startTaskLoop(singleTasks, maxSingleTasks, SingleTask::new);
        startTaskLoop(multipleTasks, maxMultipleTasks, MultipleTask::new);
    }

    /**
     * Starts a repeating task loop that adds tasks to the provided task list, ensuring that no duplicate tasks
     * (based on certain criteria) are added. The loop will stop once the task list reaches the specified maximum size.
     *
     * @param taskList The list of tasks to add new tasks to.
     * @param maxTasks The maximum number of tasks allowed in the list.
     * @param taskFactory A factory responsible for creating new tasks of type T.
     * @param <T> The type of task being added (either SingleTask or MultipleTask).
     */
    private <T> void startTaskLoop(List<T> taskList, int maxTasks, TaskFactory<T> taskFactory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Only proceed if the task list has fewer tasks than the maximum allowed
                if (taskList.size() < maxTasks) {
                    T newTask = taskFactory.create(); // Create a new task using the factory
                    boolean isUnique = true; // Flag to check if the task is unique

                    // Check if the task is a SingleTask and validate uniqueness
                    if (newTask instanceof SingleTask) {
                        for (SingleTask task : getSingleTasks()) {
                            // Check if a task with the same material and client name exists
                            if (task.getMaterial().equals(((SingleTask) newTask).getMaterial())
                                    && task.getClientName().equals(((SingleTask) newTask).getClientName())) {
                                isUnique = false;
                                break;
                            }
                        }
                    }

                    // Check if the task is a MultipleTask and validate uniqueness
                    if (newTask instanceof MultipleTask) {
                        for (MultipleTask task : getMultipleTasks()) {
                            // Check if a task with the same tasks and client exists
                            if (task.getTasks().equals(((MultipleTask) newTask).getTasks())
                                    && task.getClient().equals(((MultipleTask) newTask).getClient())) {
                                isUnique = false;
                                break;
                            }
                        }
                    }

                    // If the task is unique, add it to the task list
                    if (isUnique) {
                        taskList.add(newTask);
                        save();
                    }
                }
            }
        }.runTaskTimer(plugin, getRandomDelay(), getRandomDelay());
    }

    /**
     * Retrieves the Bukkit Player associated with this ServerPlayer.
     *
     * @return The Player object, or null if the player is offline.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    /**
     * @return The global list of all ServerPlayer instances.
     */
    public static List<ServerPlayer> getPlayers() {
        return players;
    }

    /**
     * Retrieves the ServerPlayer instance for a given Player.
     *
     * @param player The Bukkit Player.
     * @return The ServerPlayer instance, or null if not found.
     */
    public static ServerPlayer getInstance(Player player) {
        return players.stream()
                .filter(serverPlayer -> serverPlayer.getPlayer() != null && serverPlayer.getPlayerId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public static ServerPlayer getInstance(UUID uuid){
        return players.stream()
                .filter(serverPlayer -> serverPlayer.getPlayer() != null && serverPlayer.getPlayerId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Registers a new ServerPlayer instance if not already present.
     *
     * @param player The Bukkit Player.
     * @param plugin The main plugin instance.
     * @return The existing or newly created ServerPlayer.
     */
    public static ServerPlayer registerPlayer(Player player, Plugin plugin) {
        return players.stream()
                .filter(serverPlayer -> serverPlayer.getPlayer() != null && serverPlayer.getPlayer().equals(player))
                .findFirst()
                .orElseGet(() -> {
                    ServerPlayer newPlayer = new ServerPlayer(player, plugin);
                    players.add(newPlayer);
                    return newPlayer;
                });
    }

    /**
     * Removes a ServerPlayer instance from the global list.
     *
     * @param player The Bukkit Player to remove.
     */
    public static void removePlayer(Player player) {
        players.removeIf(serverPlayer -> serverPlayer.getPlayer() != null && serverPlayer.getPlayer().equals(player));
    }

    /**
     * @return The list of single tasks.
     */
    public List<SingleTask> getSingleTasks() {
        return singleTasks;
    }

    /**
     * @return The list of multiple tasks.
     */
    public List<MultipleTask> getMultipleTasks() {
        return multipleTasks;
    }

    /**
     * @return The maximum number of single tasks allowed.
     */
    public int getMaxSingleTasks() {
        return maxSingleTasks;
    }

    /**
     * @return The maximum number of multiple tasks allowed.
     */
    public int getMaxMultipleTasks() {
        return maxMultipleTasks;
    }

    /**
     * @return The player's money balance.
     */
    public double getMoney() {
        return money;
    }

    /**
     * Generates a random delay for task scheduling.
     *
     * @return A random long value between 60 and 120 ticks.
     */
    private long getRandomDelay() {
        return ThreadLocalRandom.current().nextLong(60, 120); // 60 to 120 ticks (3-6 seconds)
    }

    /**
     * Functional interface for task creation.
     *
     * @param <T> The type of task.
     */
    @FunctionalInterface
    private interface TaskFactory<T> {
        T create();
    }

    /**
     * Adds the specified amount of money to the player's balance.
     *
     * @param amount The amount to add (must be positive).
     * @return This ServerPlayer instance for chaining.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public ServerPlayer addMoney(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative.");
        }
        this.money += amount;
        return this;
    }

    /**
     * Removes the specified amount of money from the player's balance.
     *
     * @param amount The amount to remove (must be positive and not exceed current balance).
     * @return This ServerPlayer instance for chaining.
     * @throws IllegalArgumentException if the amount is negative or exceeds the current balance.
     */
    public ServerPlayer removeMoney(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to remove cannot be negative.");
        }
        if (amount > this.money) {
            throw new IllegalArgumentException("Insufficient balance. Cannot remove more money than available.");
        }
        this.money -= amount;
        return this;
    }

    /**
     * Creates and returns an inventory displaying the player's current multiple tasks,
     * with each task represented by its corresponding icon. The inventory allows
     * the player to interact with the tasks and complete them.
     *
     * @return The inventory representing the player's delivery tasks.
     */
    public Inventory getMultipleTasksInventory() {
        List<ItemStack> items = new ArrayList<>();
        for (MultipleTask task : this.getMultipleTasks()) {
            items.add(task.getIcon(this.getPlayer()));
        }

        InventoryFactory factory = new InventoryFactory(6, "Consegne", plugin)
                .setClicksAllowed(false)
                .setGlobalAction(e -> {
                    if(!InventoryUtils.checkPresence(e)) return;

                    MultipleTask task = (MultipleTask) reverse(e.getCurrentItem());
                    if(task == null) return;
                    task.complete((Player) e.getWhoClicked());
                });

        return new MultipleInventoryFactory(items, factory)
                .get();
    }

    /**
     * Creates and returns an inventory displaying the player's current single tasks,
     * with each task represented by its corresponding icon. The inventory allows
     * the player to interact with the tasks and complete them.
     *
     * @return The inventory representing the player's single tasks.
     */
    public Inventory getSingleTasksInventory(){
        List<ItemStack> items = new ArrayList<>();
        for (SingleTask task : this.getSingleTasks()) {
            items.add(task.getIcon(this.getPlayer()));
        }

        InventoryFactory factory = new InventoryFactory(6, "Consegne", plugin)
                .setClicksAllowed(false)
                .setGlobalAction(e -> {
                    if(!InventoryUtils.checkPresence(e)) return;

                    SingleTask task = (SingleTask) reverse(e.getCurrentItem());
                    if(task == null) return;
                    task.complete((Player) e.getWhoClicked());
                });

        return new MultipleInventoryFactory(items, factory)
                .get();
    }

    /**
     * Checks if the given ItemStack corresponds to any of the task icons for the player.
     * This method checks both SingleTask and MultipleTask icons to see if the player's
     * task icons match the provided ItemStack.
     *
     * @param task The ItemStack to check against the task icons.
     * @return true if the provided task icon matches any of the player's tasks, false otherwise.
     */
    public boolean isPlayerTaskIcon(ItemStack task) {
        return Stream.concat(
                        this.getMultipleTasks().stream(),
                        this.getSingleTasks().stream())
                .anyMatch(taskItem -> taskItem.getIcon(this.getPlayer()).equals(task));
    }

    /**
     * Reverses the task associated with the given ItemStack, determining whether it corresponds to
     * a SingleTask or MultipleTask for the player. If the itemStack matches a task icon,
     * the method will return the corresponding task.
     *
     * @param itemStack The ItemStack to check for a matching task icon.
     * @return The task associated with the ItemStack, either a SingleTask or MultipleTask,
     *         or null if no matching task is found.
     */
    public GameTask reverse(ItemStack itemStack) {
        if (!isPlayerTaskIcon(itemStack)) return null;

        for (MultipleTask t : this.getMultipleTasks()) {
            if (t.getIcon(this.getPlayer()).equals(itemStack)) return t;
        }

        for (SingleTask t : this.getSingleTasks()) {
            if (t.getIcon(this.getPlayer()).equals(itemStack)) return t;
        }

        return null;
    }

    /**
     * Removes a task from the player's task list.
     * This method checks whether the given task is a SingleTask or a MultipleTask
     * and removes it from the corresponding task list.
     *
     * @param task the task to be removed, either a SingleTask or a MultipleTask.
     * @return the current instance of ServerPlayer for method chaining.
     */
    public ServerPlayer removeTask(GameTask task) {
        if (task instanceof SingleTask singleTask) {
            singleTasks.remove(singleTask);
        } else if (task instanceof MultipleTask multipleTask) {
            multipleTasks.remove(multipleTask);
        }
        return this;
    }

    /**
     * Loads data from the database.
     * @param plugin The plugin instance.
     */
    public static void load(Plugin plugin) {
        try {
            List<ServerPlayer> loadedPlayers = DatabaseManager.load(new File("plugins/tonii-farms", "players.db"), ServerPlayer.class);
            ServerPlayer.getPlayers().addAll(loadedPlayers);
            System.out.println("Loaded server players");
        } catch (SQLException | InstantiationException | IllegalAccessException | IOException e) {
            throw new RuntimeException("Failed to load players from database", e);
        }
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setMaxSingleTasks(int maxSingleTasks) {
        this.maxSingleTasks = maxSingleTasks;
    }

    public void setMaxMultipleTasks(int maxMultipleTasks) {
        this.maxMultipleTasks = maxMultipleTasks;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}