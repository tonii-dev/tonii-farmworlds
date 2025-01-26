package io.github.toniidev.toniifarmworlds.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    /**
     * Creates a new Inventory instance having the same exact properties of the
     * specified inventory. It allows us to edit already existing Inventories
     * and showing them to other players, without editing the real Inventory instance
     *
     * @param inventory The Inventory that must be cloned
     * @param title     The title of the new Inventory
     * @return A new Inventory instance having the same exact properties of the chosen Inventory
     * (except title, that can be the same, but it has to be specified)
     */
    public static Inventory cloneInventory(Inventory inventory, String title) {
        Inventory newInventoryInstance = Bukkit.createInventory(null, inventory.getSize(), title);

        for (int i = 0; i < inventory.getSize(); i++) {
            newInventoryInstance.setItem(i, inventory.getItem(i));
        }

        return newInventoryInstance;
    }

    /**
     * Removes the specified amount of items of a given material from the player's inventory.
     *
     * @param material The type of material to remove.
     * @param amount   The number of items to remove.
     * @param player   The player whose inventory will be modified.
     */
    public static void removeItems(Material material, int amount, Player player) {
        if (material == null || amount <= 0 || player == null) {
            throw new IllegalArgumentException("Invalid material, amount, or player.");
        }

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (amount <= 0) break; // Stop if all items are removed
            if (itemStack == null || !itemStack.getType().equals(material)) continue;

            int stackAmount = itemStack.getAmount();

            if (stackAmount > amount) {
                // Reduce the stack size and finish removal
                itemStack.setAmount(stackAmount - amount);
                break;
            } else {
                // Remove the entire stack and decrease remaining amount
                amount -= stackAmount;
                player.getInventory().remove(itemStack);
            }
        }
    }

    /**
     * Checks whether the clicked inventory and the current item in the inventory click event are non-null.
     * This method ensures that both the clicked inventory and the current item are valid for further processing.
     *
     * @param e The InventoryClickEvent that triggered the check.
     * @return true if both the clicked inventory and the current item are non-null, false otherwise.
     */
    public static boolean checkPresence(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return false;

        if (e.getCurrentItem() == null) return false;

        return true;
    }

}
