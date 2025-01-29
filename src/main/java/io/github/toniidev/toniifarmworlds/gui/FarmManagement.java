package io.github.toniidev.toniifarmworlds.gui;

import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.classes.HistoryAccess;
import io.github.toniidev.toniifarmworlds.classes.HistoryAction;
import io.github.toniidev.toniifarmworlds.commands.CreateFarm;
import io.github.toniidev.toniifarmworlds.factories.InventoryFactory;
import io.github.toniidev.toniifarmworlds.factories.ItemStackFactory;
import io.github.toniidev.toniifarmworlds.factories.MultipleInventoryFactory;
import io.github.toniidev.toniifarmworlds.utils.InventoryUtils;
import io.github.toniidev.toniifarmworlds.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FarmManagement {
    /**
     * Generates the home inventory for managing the player's farms.
     * This includes options like creating a farm, managing whitelists, and accessing other farms.
     *
     * @param player The player for whom the inventory is generated.
     * @param plugin The plugin instance for inventory management.
     * @return An Inventory instance representing the player's farm management home.
     */
    public static Inventory getHome(Player player, Plugin plugin) {
        // Item for accessing other farms where the player is whitelisted
        ItemStackFactory otherFarmsFactory = new ItemStackFactory(Material.PLAYER_HEAD)
                .setName(StringUtils.formatColorCodes('&', "&9Altre fattorie"))
                .addLoreLine("Accedi alle fattorie in cui sei stato")
                .addLoreLine("invitato.")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&fPuoi accedere a &b" + Farm.getFarmsPlayerIsWhitelistedIn(player)
                        .size() + "&f fattorie."));

        // Initialize the base inventory for farm management
        InventoryFactory base = new InventoryFactory(1, "Gestione terreni", plugin)
                .setItem(8, otherFarmsFactory.get()) // Set other farms item at index 8
                .setClicksAllowed(false) // Disable clicks on the background
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get()); // Fill the inventory with empty panes

        // Add action for opening other farms inventory if the player is whitelisted in any farm
        if (!Farm.getFarmsPlayerIsWhitelistedIn(player).isEmpty()) {
            otherFarmsFactory.addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per saperne di più!"));

            base.setAction(8, e -> {
                // Open the other farms home inventory when clicked
                e.getWhoClicked().openInventory(FarmManagement.getOtherFarmsHome(player, plugin));
            });
        }

        // Check if the player already has a farm
        if (Farm.reverse(player).isEmpty()) {
            // Item for creating a new farm if the player doesn't have one
            base.setItem(4, new ItemStackFactory(Material.GREEN_STAINED_GLASS_PANE)
                            .setName(StringUtils.formatColorCodes('&', "&aCrea fattoria"))
                            .addLoreLine("Crea una tua fattoria. Il tuo posto tranquillo,")
                            .addLoreLine("dove nessuno ti può disturbare. Solo tu e le tue")
                            .addLoreLine("mucche.")
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per creare una fattoria!"))
                            .get())
                    .setAction(4, e -> new CreateFarm().onCommand(player, Bukkit.getPluginCommand("creafattoria"), null, null));
        } else {
            // Item for teleporting to the player's existing farm
            base.setItem(2, new ItemStackFactory(Material.CYAN_STAINED_GLASS_PANE)
                            .setName(StringUtils.formatColorCodes('&', "&bVai alla fattoria"))
                            .addLoreLine("Vai nella tua fattoria")
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per teletrasportarti!"))
                            .get())
                    .setAction(2, e -> {
                        // Teleport the player to their farm's spawn location
                        System.out.println(
                                Farm.reverse((Player) e.getWhoClicked()).get().getWorldName());
                        e.getWhoClicked().teleport(Farm.reverse((Player) e.getWhoClicked()).get().getWorld().getSpawnLocation());
                    });

            // Item for managing the player's farm
            base.setItem(5, new ItemStackFactory(Material.FILLED_MAP)
                            .setName(StringUtils.formatColorCodes('&', "&6Gestisci fattoria"))
                            .addLoreLine("Apri il menu di gestione della tua")
                            .addLoreLine("fattoria.")
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per aprire l'inventario!"))
                            .get())
                    .setAction(5, e -> {
                        // Open the farm management home inventory
                        e.getWhoClicked().openInventory(FarmManagement.getFarmManagementHome(player, plugin));
                    });
        }

        // Return the final inventory for the player's farm management home
        return base.get();
    }


    /**
     * Generates an inventory for managing a player's farm.
     *
     * @param player The player who owns the farm.
     * @param plugin The plugin instance for inventory management.
     * @return An Inventory instance for farm management with various options.
     */
    public static Inventory getFarmManagementHome(Player player, Plugin plugin) {
        ItemStackFactory historyFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&bCronologia accessi"))
                .addLoreLine("Ti hanno stuprato le mucche? Nessun problema!")
                .addLoreLine("fai l'investigatore e vedi chi è stato, che gli")
                .addLoreLine("facciamo i complimenti");

        InventoryFactory factory = new InventoryFactory(1, "Gestione fattoria", plugin)
                .setInventoryToShowOnClose(FarmManagement.getHome(player, plugin))
                .setClicksAllowed(false)
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get())
                .setItem(3, new ItemStackFactory(Material.FILLED_MAP)
                        .setName(StringUtils.formatColorCodes('&', "&6Gestisci whitelist"))
                        .addLoreLine("Gestisci i giocatori che possono entrare")
                        .addLoreLine("nella tua fattoria. Attento, se sono")
                        .addLoreLine("comunisti potrebbero proporre sistemi")
                        .addLoreLine("economici di merda.")
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per saperne di più!"))
                        .get())
                .setAction(3, e -> e.getWhoClicked().openInventory(getWhitelistHome(player, plugin)));

        if (!Farm.reverse(player).get().getHistory().isEmpty()) {
            historyFactory.addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per saperne di più!"));

            factory.setAction(5, e -> e.getWhoClicked().openInventory(getFarmHistory(Farm.reverse((Player) e.getWhoClicked()).get(), plugin))).get();
        }

        return factory.setItem(5, historyFactory.get()).get();
    }

    /**
     * Creates an inventory menu that displays all farms where the player is whitelisted.
     * Players can click an item to teleport to the corresponding farm's spawn location.
     *
     * @param player the player for whom the inventory is being created.
     * @param plugin the plugin instance used to register inventory actions and other operations.
     * @return an Inventory object representing the menu for accessing other farms.
     */
    public static Inventory getOtherFarmsHome(Player player, Plugin plugin) {
        // Map ItemStacks to their corresponding farms
        Map<ItemStack, Farm> farmItemMap = Farm.getFarmsPlayerIsWhitelistedIn(player).stream()
                .collect(Collectors.toMap(
                        farm -> new ItemStackFactory(Material.PLAYER_HEAD)
                                .setName(StringUtils.formatColorCodes('&', "&aFattoria di &e" + farm.getOwnerAsPlayer().getDisplayName()))
                                .addLoreLine("Vai nella sua fattoria e fai quello che vuoi.")
                                .addBlankLoreLine()
                                .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per teletrasportarti!"))
                                .get(),
                        farm -> farm
                ));

        // Extract ItemStacks for inventory display
        List<ItemStack> items = new ArrayList<>(farmItemMap.keySet());

        // Create the inventory factory
        InventoryFactory base = new InventoryFactory(6, "Altre fattorie", plugin)
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(FarmManagement.getHome(player, plugin))
                .setGlobalAction(e -> {
                    if (!InventoryUtils.checkPresence(e)) return;

                    // Teleport the player if the clicked item matches a farm
                    Farm clickedFarm = farmItemMap.get(e.getCurrentItem());
                    if (clickedFarm != null) {
                        e.getWhoClicked().teleport(clickedFarm.getWorld().getSpawnLocation());
                    }
                });

        // Create the inventory with support for multiple pages if needed
        return new MultipleInventoryFactory(items, base).get();
    }

    /**
     * Generates an inventory showing the history of access and exit actions for a given farm.
     *
     * @param farm   The farm whose history is to be displayed.
     * @param plugin The plugin instance required for inventory creation and management.
     * @return An Inventory instance displaying the farm's access and exit history.
     */
    public static Inventory getFarmHistory(Farm farm, Plugin plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();

        // Define a formatter for LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

        // Loop through the farm's history and create corresponding item stacks
        for (HistoryAction action : farm.getHistory()) {
            String formattedDate = action.getDate().format(formatter);

            // Determine the item type and details based on the action type
            itemStacks.add(new ItemStackFactory(
                    action instanceof HistoryAccess ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .setName(StringUtils.formatColorCodes(
                            '&', (action instanceof HistoryAccess ? "&aAccesso" : "&cUscita")))
                    .addLoreLine(StringUtils.formatColorCodes('&', "&8Cronologia"))
                    .addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&fGiocatore: &6" + action.getName()))
                    .addLoreLine(StringUtils.formatColorCodes(
                            '&', "&fData: &6" + formattedDate))
                    .get());
        }

        // Create a base inventory for the history with a fallback inventory for when it is closed
        InventoryFactory factory = new InventoryFactory(6, "Cronologia", plugin)
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(getFarmManagementHome(farm.getOwnerAsPlayer(), plugin));

        // Generate and return the inventory using the created item stacks
        return new MultipleInventoryFactory(itemStacks, factory).get();
    }

    public static Inventory getWhitelistHome(Player player, Plugin plugin){
        Farm farm = Farm.reverse(player).get();

        InventoryFactory base = new InventoryFactory(1, "Whitelist", plugin)
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(getFarmManagementHome(player, plugin))
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get());

        ItemStackFactory manageFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&6Gestisci giocatori whitelistati"))
                .addLoreLine("Visualizza i giocatori attualmente whitelistati")
                .addLoreLine("e, se vuoi, rimuovili dalla whitelist")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&fCi sono &b" + farm.getWhitelist().size() + "&f giocatori attualmente"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&fwhitelistati nella tua fattoria"));

        if(!farm.getWhitelist().isEmpty()){
            manageFactory.addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per gestire!"));

            base.setAction(3, e -> e.getWhoClicked().openInventory(getManageWhitelist(player, plugin)));
        }

        base.setItem(3, manageFactory.get());

        ItemStackFactory addFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&9Aggiungi giocatore alla whitelist"))
                .addLoreLine("Scegli tra la lista dei giocatori attualmente")
                .addLoreLine("online qualcuno da aggiungere alla whitelist")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&fCi sono &b" + (Bukkit.getOnlinePlayers().size() - 1) + "&f giocatori attualmente online nel server"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&foltre a te. In questa lista non compariranno"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&fi giocatori già whitelistati."));

        if(!(Bukkit.getOnlinePlayers().size() - 1 == 0)){
            addFactory.addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per visualizzare!"));

            base.setAction(5, e -> e.getWhoClicked().openInventory(getAddPlayerToWhitelist(player, plugin)));
        }

        base.setItem(5, addFactory.get());

        return base.get();
    }

    public static Inventory getManageWhitelist(Player player, Plugin plugin){
        Farm farm = Farm.reverse(player).get();

        Map<ItemStack, Player> items = farm.getWhitelist().stream()
                .filter(x -> farm.isWhitelisted(Bukkit.getPlayer(x)))
                .collect(HashMap::new, (map, onlinePlayer) -> map.put(
                        new ItemStackFactory(Material.PLAYER_HEAD)
                                .setName(StringUtils.formatColorCodes('&', "&6" + Bukkit.getPlayer(onlinePlayer).getDisplayName()))
                                .addLoreLine("Questo giocatore è whitelistato. Può entrare nella")
                                .addLoreLine("tua fattoria in qualsiasi momento.")
                                .addBlankLoreLine()
                                .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per rimuovere!"))
                                .get(),
                        Bukkit.getPlayer(onlinePlayer)
                ), HashMap::putAll);

        InventoryFactory factory = new InventoryFactory(6, "Giocatori whitelistati", plugin)
                .setClicksAllowed(false)
                .setGlobalAction(e -> {
                    if(!InventoryUtils.checkPresence(e)) return;
                    if(!items.containsKey(e.getCurrentItem())) return;

                    Player playerToRemove = items.get(e.getCurrentItem());
                    if(!farm.isWhitelisted(playerToRemove)) return;

                    farm.removeFromWhitelist(playerToRemove);
                    player.sendMessage(StringUtils.formatColorCodes('&', "&e[Fattoria] &aWhitelist: &f" + playerToRemove.getDisplayName() + "&7 è stato rimosso dalla whitelist."));
                });

        return new MultipleInventoryFactory(items.keySet().stream().toList(), factory)
                .get();
    }

    public static Inventory getAddPlayerToWhitelist(Player player, Plugin plugin){
        Farm farm = Farm.reverse(player).get();

        Map<ItemStack, Player> items = Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> !farm.isWhitelisted(onlinePlayer) &&
                        !farm.getOwnerAsPlayer().equals(onlinePlayer))
                .collect(HashMap::new, (map, onlinePlayer) -> map.put(
                        new ItemStackFactory(Material.PLAYER_HEAD)
                                .setName(StringUtils.formatColorCodes('&', "&6" + onlinePlayer.getDisplayName()))
                                .addLoreLine("Aggiungi questo giocatore alla whitelist")
                                .addLoreLine("della tua fattoria. Potrà entrarci in qualsiasi")
                                .addLoreLine("momento.")
                                .addBlankLoreLine()
                                .addLoreLine(StringUtils.formatColorCodes('&', "&eClicca per aggiungere!"))
                                .get(),
                        onlinePlayer
                ), HashMap::putAll);

        InventoryFactory factory = new InventoryFactory(6, "Giocatori online", plugin)
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(getWhitelistHome(player, plugin))
                .setGlobalAction(e -> {
                    if(!InventoryUtils.checkPresence(e)) return;
                    if(!items.containsKey(e.getCurrentItem())) return;

                    Player playerToAdd = items.get(e.getCurrentItem());
                    if(farm.isWhitelisted(playerToAdd)) return;

                    farm.whitelistPlayer(playerToAdd);
                    player.sendMessage(StringUtils.formatColorCodes('&', "&e[Fattoria] &aWhitelist: &f" + playerToAdd.getDisplayName() + "&7 è stato whitelistato."));
                });

        return new MultipleInventoryFactory(items.keySet().stream().toList(), factory)
                .get();
    }
}
