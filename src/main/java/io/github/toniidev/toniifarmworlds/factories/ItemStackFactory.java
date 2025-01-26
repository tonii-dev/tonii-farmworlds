package io.github.toniidev.toniifarmworlds.factories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemStack factory. This class will be used to create any custom ItemStack
 * with custom properties and custom handling
 */
public class ItemStackFactory {
    private final ItemStack stack;

    /**
     * Checks if this stack has ItemMeta. By default, any ItemStack but AIR has
     * ItemMeta
     *
     * @return true if the Stack has an ItemMeta, false if it doesn't
     */
    private boolean checkItemMeta() {
        return stack.getItemMeta() != null;
    }

    /**
     * Checks if this stack has ItemMeta and notifies the console if it doesn't.
     * If any method implements this function like this: if(notifyMissingItemMeta()) return ...,
     * the ItemStack has 100% an ItemMeta. So we can use assert stack.getItemMeta() != null
     *
     * @return true if a console message has been sent, false if anything is ok
     */
    private boolean notifyMissingItemMeta() {
        return !checkItemMeta();
        //Bukkit.getLogger().warning(ConsoleInfo.MISSING_ITEM_META.getMessage(stack));
    }

    /**
     * Creates an ItemStackFactory that will edit a new ItemStack of the given material.
     * The amount of the ItemStack is 1
     *
     * @param material The Material of the ItemStack the Factory has to work with
     */
    public ItemStackFactory(Material material) {
        this.stack = new ItemStack(material, 1);
    }

    /**
     * Creates an ItemStackFactory that will edit a new ItemStack of the given material.
     *
     * @param material The Material of the ItemStack the Factory has to work with
     * @param amount   The amount of the ItemStack the Factory has to work with
     */
    public ItemStackFactory(Material material, int amount) {
        this.stack = new ItemStack(material, amount);
    }

    /**
     * Creates an ItemStackFactory that will work with an existing ItemStack instance
     *
     * @param itemStack THe existing ItemStack instance the ItemStackFactory has to edit
     */
    public ItemStackFactory(ItemStack itemStack) {
        this.stack = itemStack;
    }

    /**
     * Gets the ItemMeta of the ItemStack the ItemStackFactory is working with and
     * gets its name
     *
     * @return The item DisplayName of the ItemStack
     */
    @Nullable
    public String getName() {
        if (notifyMissingItemMeta()) return null;
        assert stack.getItemMeta() != null;
        return stack.getItemMeta().getDisplayName();
    }

    /**
     * Edits the DisplayName of the given ItemStack
     *
     * @param name The new name of the ItemStack
     * @return This ItemFactory instance
     */
    public ItemStackFactory setName(String name) {
        if (notifyMissingItemMeta()) return null;
        assert stack.getItemMeta() != null;

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the lore of the given ItemStack
     *
     * @param lore The new lore of the ItemStack
     * @return This ItemFactory instance
     */
    public ItemStackFactory setLore(List<String> lore) {
        if (notifyMissingItemMeta()) return null;
        assert stack.getItemMeta() != null;

        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds a line to the lore of the given ItemStack.
     * If the ItemStack does not have a lore, it gets created
     *
     * @param line The line to add to the lore
     * @return This ItemFactory instance
     */
    public ItemStackFactory addLoreLine(String line) {
        if (notifyMissingItemMeta()) return null;
        assert stack.getItemMeta() != null;

        ItemMeta meta = stack.getItemMeta();
        List<String> lore = (meta.getLore() != null) ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("§7" + line);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Adds a blank lore line to the lore of the given ItemStack.
     * If the ItemStack does not have a lore, it gets created
     *
     * @return This ItemFactory instance
     */
    public ItemStackFactory addBlankLoreLine() {
        return addLoreLine(" ");
    }

    /**
     * Finally, gets the result of the ItemStackFactory.
     * This should only be used at the end of the creation.
     *
     * @return The created ItemStack
     */
    public ItemStack get() {
        return this.stack;
    }
}
