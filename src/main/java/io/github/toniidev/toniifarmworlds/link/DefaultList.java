package io.github.toniidev.toniifarmworlds.link;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class DefaultList {
    public static List<String> CLIENT_NAMES = List.of("Greg", "Tom", "Bartolo", "Lucia", "Amanda", "Armandino",
            "Pietro", "Heidi", "Gerardo", "Massimo", "Salvatore", "Gennaro",
            "Christian", "Antonio", "Angelo", "Andrea", "Alice", "Bob",
            "Niccolò", "Shippino", "Nicolino", "Raffaele", "Claudio", "Sceriffo");
    public static List<String> DESTINATION_NAMES = List.of("Asilo", "Scuola elementare", "Scuola media", "Chiesa",
            "Comune", "Centro anziani", "Parco giochi", "Stadio");

    public static List<SingleTask> BASIC_REQUESTS = DefaultList.parseTaskList(new SingleTask(Material.BREAD, "Pane"),
            new SingleTask(Material.WHEAT, "Grano"),
            new SingleTask(Material.APPLE, "Mela"),
            new SingleTask(Material.MILK_BUCKET, "Latte"),
            new SingleTask(Material.BAKED_POTATO, "Patata al forno"),
            new SingleTask(Material.BEEF, "Bistecca cruda"),
            new SingleTask(Material.BEETROOT, "Barbabietola"),
            new SingleTask(Material.BEETROOT_SOUP, "Zuppa di barbabietola"),
            new SingleTask(Material.CAKE, "Torta"),
            new SingleTask(Material.CARROT, "Carota"),
            new SingleTask(Material.CHICKEN, "Pollo crudo"),
            new SingleTask(Material.COOKED_BEEF, "Bistecca cotta"),
            new SingleTask(Material.COOKED_CHICKEN, "Pollo cotto"),
            new SingleTask(Material.COOKED_COD, "Merluzzo cotto"),
            new SingleTask(Material.COOKED_MUTTON, "Carne di montone cotta"),
            new SingleTask(Material.COOKED_PORKCHOP, "Bistecca di maiale cotta"),
            new SingleTask(Material.COOKED_RABBIT, "Coniglio cotto"),
            new SingleTask(Material.COOKED_SALMON, "Salmone cotto"),
            new SingleTask(Material.COOKIE, "Biscotto"),
            new SingleTask(Material.COD, "Merluzzo crudo"),
            new SingleTask(Material.MELON_SLICE, "Fetta di melone"),
            new SingleTask(Material.MUSHROOM_STEW, "Zuppa di funghi"),
            new SingleTask(Material.MUTTON, "Carne di montone cruda"),
            new SingleTask(Material.PORKCHOP, "Bistecca di maiale cruda"),
            new SingleTask(Material.POTATO, "Patata"),
            new SingleTask(Material.PUMPKIN_PIE, "Torta di zucca"),
            new SingleTask(Material.RABBIT, "Coniglio crudo"),
            new SingleTask(Material.RABBIT_STEW, "Zuppa di coniglio"),
            new SingleTask(Material.SALMON, "Salmone crudo"),
            new SingleTask(Material.SWEET_BERRIES, "Bacche dolci"));

    private static List<SingleTask> parseTaskList(SingleTask... tasks){
        return Arrays.asList(tasks);
    }
}
