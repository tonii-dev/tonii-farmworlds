package io.github.toniidev.toniifarmworlds.factories;

import io.github.toniidev.toniifarmworlds.utils.InventoryUtils;
import io.github.toniidev.toniifarmworlds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Inventory Factory. This class creates an Inventory that can have
 * more pages that can be browsed by an arrow that is on the left of it.
 */
public class MultipleInventoryFactory {
    /// This list contains all the pages of the MultipleInventory
    private final List<Inventory> pages = new ArrayList<>();

    /**
     * Creates a blank MultipleInventoryFactory instance.
     *
     * @param items        The Items that this MultipleInventory will contain. The Items will be split in groups of 21 and
     *                     every group of 21 Items will be displayed in his own page
     * @param startFactory The InventoryFactory on which this MultipleInventoryFactory instance should be based on
     */
    public MultipleInventoryFactory(List<ItemStack> items, InventoryFactory startFactory, Plugin plugin) {
        int[] airSlots = {2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24, 25, 29,
                30, 31, 32, 33, 34, 38, 39, 40, 41, 42, 43, 47, 48, 49, 50, 51, 52};

        int totalPages = (int) Math.ceil((double) items.size() / airSlots.length);

        for (int i = 0; i < totalPages; i++) {
            int startIndex = i * airSlots.length;
            int endIndex = Math.min(startIndex + airSlots.length, items.size());
            List<ItemStack> itemsToDisplayInThisPage = items.subList(startIndex, endIndex);

            InventoryFactory factory = new InventoryFactory(InventoryUtils.cloneInventory(startFactory.get(), startFactory.getTitle()), plugin);

            factory.fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                    .setName(" ").get());

            for (int slot : airSlots) {
                factory.setItem(slot, new ItemStack(Material.AIR));
            }

            factory.addItem(itemsToDisplayInThisPage);
            setPageNavigationItems(i, totalPages, factory);

            pages.add(factory.get());
        }
    }

    /**
     * Sets the navigation items to the Inventory linked to the specified InventoryFactory
     * @param pageNumber The number of the current page
     * @param totalPages The number of total pages that this MultipleInventoryFactory has
     * @param template The InventoryFactory this MultipleInventoryFactory should be based on
     */
    private void setPageNavigationItems(int pageNumber, int totalPages, InventoryFactory template) {
        template.setItem(9, new ItemStackFactory(Material.COMPASS)
                .setName(StringUtils.formatColorCodes('&', "&ePagina dell'inventario"))
                .addLoreLine(StringUtils.formatColorCodes('&', "Pagina corrente: &f" + (pageNumber + 1) +
                        " &7di &f" + totalPages))
                .get());

        if (pageNumber > 0) {
            template.setAction(27, e -> e.getWhoClicked().openInventory(pages.get(pageNumber - 1)));
            template.setItem(27, new ItemStackFactory(Material.ARROW)
                    .setName(StringUtils.formatColorCodes('&', "&cPagina precedente"))
                    .addLoreLine(StringUtils.formatColorCodes('&', String.format("Torna a pagina &e%d", pageNumber)))
                    .get());
        }

        if (pageNumber < totalPages - 1) {
            template.setAction(36, e -> e.getWhoClicked().openInventory(pages.get(pageNumber + 1)));
            template.setItem(36, new ItemStackFactory(Material.SPECTRAL_ARROW)
                            .setName(StringUtils.formatColorCodes('&', "&aPagina successiva"))
                            .addLoreLine(StringUtils.formatColorCodes('&', String.format("Vai a pagina &e%d", pageNumber + 2)))
                            .get());
        }
    }

    public Inventory get() {
        return pages.isEmpty() ? null : pages.getFirst();
    }
}
