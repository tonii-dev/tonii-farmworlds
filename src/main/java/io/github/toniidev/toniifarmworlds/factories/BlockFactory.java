package io.github.toniidev.toniifarmworlds.factories;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Class used to build custom blocks with custom text
 * displayed above them.
 */
public class BlockFactory {
    private final ArmorStand titleArmorStand;
    private final ArmorStand subtitleArmorStand;

    /**
     * Creates a BlockFactory instance starting from the location of the Block
     * text should be displayed on
     *
     * @param blockLocation The location of the Block on top of which there will be displayed
     *                      the ArmorStands
     */
    public BlockFactory(Location blockLocation) {
        this.titleArmorStand = spawnArmorStand(calculateTitleArmorStandLocation(blockLocation));
        this.subtitleArmorStand = spawnArmorStand(calculateSubtitleArmorStandLocation(blockLocation));
    }

    /**
     * Sets the custom name to display above the top ArmorStand
     *
     * @param string The title to display
     * @return This BlockFactory instance
     */
    public BlockFactory setTitle(String string) {
        titleArmorStand.setCustomName(string);
        return this;
    }

    /**
     * Sets the custom name to display above the bottom ArmorStand
     *
     * @param string The title to display
     * @return This BlockFactory instance
     */
    public BlockFactory setSubtitle(String string) {
        subtitleArmorStand.setCustomName(string);
        return this;
    }

    /**
     * Calculates where the title armor stand should be spawned according to the block location
     *
     * @param blockLocation The location of the block linked to this ArmorStand
     * @return The location where the title armor stand should be spawned according to the location
     * of the block linked to it.
     */
    public static Location calculateTitleArmorStandLocation(Location blockLocation) {
        return blockLocation.clone().add(0.5, -1, 0.5);
    }

    /**
     * Calculates where the subtitle armor stand should be spawned according to the block location
     *
     * @param blockLocation The location of the block linked to this ArmorStand
     * @return The location where the subtitle armor stand should be spawned according to the location
     * of the block linked to it.
     */
    public static Location calculateSubtitleArmorStandLocation(Location blockLocation) {
        return calculateTitleArmorStandLocation(blockLocation).clone().add(0, -0.25, 0);
    }

    /**
     * Spawns an ArmorStand at the given location and gets its instance if the location is valid
     *
     * @param location The location where the ArmorStand should be spawned
     * @return null if location#getWorld() is invalid, the ArmorStand instance if the
     * entity spawn is successful
     */
    @Nullable
    public static ArmorStand spawnArmorStand(Location location) {
        if (location.getWorld() == null) return null;

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setCanPickupItems(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(true);

        return armorStand;
    }

    /**
     * Gets the ArmorStand instance at the given location
     *
     * @param location The location where there should be the ArmorStand
     * @return null if there is no ArmorStand at the specified location or location#getWorld() is invalid,
     * the ArmorStand instance if it exists
     */
    @Nullable
    public static ArmorStand getArmorStand(Location location) {
        if (location.getWorld() == null) return null;

        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 0.1, 0.1, 0.1);
        if (nearbyEntities.isEmpty()) return null;

        return (ArmorStand) nearbyEntities.stream()
                .filter(x -> x.getType().equals(EntityType.ARMOR_STAND))
                .findFirst().orElse(null);
    }

    /**
     * Says whether an ArmorStand exists in the specified location or not
     *
     * @param location The location where we must check whether there is an ArmorStand or not
     * @return true if there is an ArmorStand at the given location, false if there isn't
     */
    public static boolean doesArmorStandExist(Location location) {
        return getArmorStand(location) != null;
    }

    /**
     * Let us know whether an ArmorStand has a custom name or not
     *
     * @param location The exact location of the ArmorStand
     * @return true if the ArmorStand has a custom name, false if the ArmorStand does not exist
     * or does not have a custom name
     */
    public static boolean doesArmorStandHaveCustomName(Location location) {
        if (!doesArmorStandExist(location)) return false;

        ArmorStand armorStand = getArmorStand(location);
        assert armorStand != null;

        return armorStand.getCustomName() != null;
    }

    /**
     * Break shop procedure. It is called every time a shop gets broken.
     * It removes the ArmorStands on top of it
     *
     * @param location The location of the block that gets broken
     */
    public static void breakShop(Location location) {
        Location topArmorStand = calculateTitleArmorStandLocation(location);
        Location bottomArmorStand = calculateSubtitleArmorStandLocation(location);

        removeCustomArmorStand(topArmorStand);
        removeCustomArmorStand(bottomArmorStand);
    }

    /**
     * Removes a custom ArmorStand. An ArmorStand is considered "custom" when
     * it has a custom name
     *
     * @param location The exact location of the ArmorStand
     */
    public static void removeCustomArmorStand(Location location) {
        if (doesArmorStandHaveCustomName(location)) {
            ArmorStand armorStand = getArmorStand(location);
            assert armorStand != null;
            armorStand.remove();
        }
    }
}
