package io.github.toniidev.toniifarmworlds.link;

import io.github.toniidev.toniifarmworlds.factories.ItemStackFactory;
import io.github.toniidev.toniifarmworlds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MultipleTask extends GameTask {
    private final List<SingleTask> tasks;
    private final String client;

    /**
     * Default constructor that generates a MultipleTask instance with 1-4 random tasks
     * and a random client name.
     */
    public MultipleTask() {
        this.tasks = generateRandomTasks();
        this.client = selectRandomClient();
    }

    /**
     * Constructor that accepts a predefined list of tasks and assigns a random client name.
     *
     * @param tasks List of SingleTask objects.
     */
    public MultipleTask(List<SingleTask> tasks) {
        this.tasks = new ArrayList<>(tasks); // Defensive copy for immutability.
        this.client = selectRandomClient();
    }

    /**
     * Generates a list of random tasks from DefaultList.BASIC_REQUESTS.
     *
     * @return List of SingleTask objects.
     */
    private List<SingleTask> generateRandomTasks() {
        int taskCount = ThreadLocalRandom.current().nextInt(1, 5); // 1 to 4 tasks.
        List<SingleTask> taskList = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            SingleTask randomTask = DefaultList.BASIC_REQUESTS.get(
                    ThreadLocalRandom.current().nextInt(DefaultList.BASIC_REQUESTS.size())
            );
            taskList.add(randomTask);
        }
        return taskList;
    }

    /**
     * Selects a random client name from DefaultList.DESTINATION_NAMES.
     *
     * @return A random client name as a String.
     */
    private String selectRandomClient() {
        return DefaultList.DESTINATION_NAMES.get(
                ThreadLocalRandom.current().nextInt(DefaultList.DESTINATION_NAMES.size())
        );
    }

    /**
     * @return List of tasks in this MultipleTask instance.
     */
    public List<SingleTask> getTasks() {
        return Collections.unmodifiableList(tasks); // Immutable list to preserve encapsulation.
    }

    /**
     * @return The client name associated with this MultipleTask instance.
     */
    public String getClient() {
        return client;
    }

    public double getReward() {
        return tasks.stream()
                .mapToDouble(SingleTask::getReward)
                .sum();
    }

    @Override
    public boolean canComplete(Player player) {
        return this.getTasks().stream().allMatch(task -> task.canComplete(player));
    }

    @Override
    public void complete(Player player) {
        if (!canComplete(player)) {
            player.sendMessage(StringUtils.formatColorCodes('&',
                    "&e[Vendita] &cErrore: &7Non hai abbastanza oggetti per poter completare questo incarico."));
            return;
        }

        double totalReward = 0;
        int totalItemsSold = 0;

        for (SingleTask task : this.getTasks()) {
            task.complete(player);
            totalReward += task.getReward();
            totalItemsSold += task.getAmount();
        }

        ServerPlayer.getInstance(player).removeTask(this);
        player.sendMessage(StringUtils.formatColorCodes('&',
                "&e[Vendita] &aSuccesso: &7Hai venduto &f" + totalItemsSold +
                        "&7 oggetti per &f" + totalReward + "$&7."));
    }


    @Override
    public ItemStack getIcon(Player player) {
        ItemStackFactory factory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&aConsegna"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&8" + this.getClient()))
                .addBlankLoreLine()
                .addLoreLine("Hai una richiesta di consegna!")
                .addBlankLoreLine();

        for (SingleTask task : this.getTasks()) {
            factory.addLoreLine(StringUtils.formatColorCodes('&', "&e" + task.getAmount() + "x &a" + task.getRequestName() + " " +
                    (task.canComplete(player) ? "&a✔" : "&c❌")));
        }

        factory.addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&fRicompensa: &6" + this.getReward() + "$"))
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', (this.canComplete(player) ? "&eClicca per accettare!" : "&cNon hai abbastanza oggetti!")));

        return factory.get();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("multitask#");
        for(SingleTask task : this.getTasks()){
            stringBuilder.append(task.toString() + ";");
        }

        stringBuilder.append(":" + this.getClient());
        return stringBuilder.toString();
    }

    public MultipleTask(List<SingleTask> tasks, String client) {
        this.tasks = tasks;
        this.client = client;
    }
}
