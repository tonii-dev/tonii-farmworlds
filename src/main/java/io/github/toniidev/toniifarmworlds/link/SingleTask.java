package io.github.toniidev.toniifarmworlds.link;

import io.github.toniidev.toniifarmworlds.factories.ItemStackFactory;
import io.github.toniidev.toniifarmworlds.utils.IntegerUtils;
import io.github.toniidev.toniifarmworlds.utils.InventoryUtils;
import io.github.toniidev.toniifarmworlds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class SingleTask extends GameTask {
    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 4;
    private static final double REWARD_MULTIPLIER = 20.0;
    private static final double BASE_REWARD = 1.0;

    private final Material material;
    private final String requestName;
    private final int amount;
    private final double reward;
    private final String clientName;

    /**
     * Constructs a SingleTask with the given material and request name.
     *
     * @param material    The material required for the task.
     * @param requestName The name of the request.
     */
    public SingleTask(Material material, String requestName) {
        this.material = material;
        this.requestName = requestName;
        this.amount = generateRandomAmount();
        this.reward = calculateReward(this.amount);
        this.clientName = selectRandomClientName();
    }

    /**
     * Constructs a SingleTask by randomly selecting a task from DefaultList.BASIC_REQUESTS.
     */
    public SingleTask() {
        SingleTask randomTask = getRandomTaskFromBasicRequests();
        this.material = randomTask.getMaterial();
        this.requestName = randomTask.getRequestName();
        this.amount = randomTask.getAmount();
        this.reward = randomTask.getReward();
        this.clientName = randomTask.getClientName();
    }

    /**
     * Retrieves a random task from DefaultList.BASIC_REQUESTS.
     *
     * @return A randomly selected SingleTask from the list.
     */
    private SingleTask getRandomTaskFromBasicRequests() {
        return DefaultList.BASIC_REQUESTS.get(
                ThreadLocalRandom.current().nextInt(DefaultList.BASIC_REQUESTS.size())
        );
    }

    /**
     * @return The material required for the task.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return The name of the request.
     */
    public String getRequestName() {
        return requestName;
    }

    /**
     * @return The amount of material required for the task.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return The reward for completing the task.
     */
    public double getReward() {
        return IntegerUtils.roundCompletely(reward);
    }

    /**
     * @return The name of the client for the task.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Determines if the task can be completed by the player.
     *
     * @param player The player attempting to complete the task.
     * @return True if the player has the required materials, otherwise false.
     */
    @Override
    public boolean canComplete(Player player) {
        return player.getInventory().contains(material, amount);
    }

    /**
     * Completes the task, applying necessary effects or rewards to the player.
     *
     * @param player The player completing the task.
     */
    @Override
    public void complete(Player player) {
        if (canComplete(player)) {
            InventoryUtils.removeItems(this.getMaterial(), this.getAmount(), player);
            ServerPlayer.getInstance(player).addMoney(this.getReward())
                    .removeTask(this);
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Vendita] &aSuccesso: &7Hai venduto &f" + this.getAmount() + "x "
                    + this.getRequestName() + "&7 per &f" + this.getReward() + "$&7."));
        } else
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Vendita] &cErrore: &7Non hai abbastanza oggetti per poter completare questo incarico."));
    }

    /**
     * Gets the task's icon to display in the UI or inventory.
     *
     * @param player The player viewing the task.
     * @return An ItemStack representing the task.
     */
    @Override
    public ItemStack getIcon(Player player) {
        ItemStackFactory factory = new ItemStackFactory(new ItemStack(this.getMaterial(), this.getAmount()))
                .setName(StringUtils.formatColorCodes('&', "&e" + this.getAmount() +
                        "x &a" + this.getRequestName()))
                .addLoreLine(StringUtils.formatColorCodes('&', "&8" + this.getClientName()))
                .addBlankLoreLine()
                .addLoreLine("Qualcuno ha una richiesta per te!")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&fPrezzo: &6" + this.getReward() + "$"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&fOggetto: &6" + this.getRequestName()))
                .addLoreLine(StringUtils.formatColorCodes('&', "&fQuantità: " + (this.canComplete(player) ? "&a" : "&c") +
                        this.getAmount() + "x"))
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', (this.canComplete(player) ? "&eClicca per accettare!" : "&cNon hai abbastanza oggetti!")));

        return factory.get();
    }

    @Override
    public String toString() {
        return "singletask" + "@" + this.material.name() + "," + this.amount + "," + this.requestName + "," +
                this.reward + "," + this.clientName;
    }

    public SingleTask(Material material, String requestName, int amount, double reward, String clientName) {
        this.material = material;
        this.requestName = requestName;
        this.amount = amount;
        this.reward = reward;
        this.clientName = clientName;
    }

    /**
     * Generates a random amount of material required for the task.
     *
     * @return A random integer between MIN_AMOUNT and MAX_AMOUNT (inclusive).
     */
    private int generateRandomAmount() {
        return ThreadLocalRandom.current().nextInt(MIN_AMOUNT, MAX_AMOUNT + 1);
    }

    /**
     * Calculates the reward based on the task's material amount.
     *
     * @param amount The amount of material required.
     * @return The calculated reward.
     */
    private double calculateReward(int amount) {
        return amount * (ThreadLocalRandom.current().nextDouble(REWARD_MULTIPLIER) + BASE_REWARD);
    }

    /**
     * Selects a random client name from DefaultList.CLIENT_NAMES.
     *
     * @return A random client name as a String.
     */
    private String selectRandomClientName() {
        return DefaultList.CLIENT_NAMES.get(
                ThreadLocalRandom.current().nextInt(DefaultList.CLIENT_NAMES.size())
        );
    }


}

