package io.github.toniidev.toniifarmworlds.utils;

import org.bukkit.Bukkit;

import java.io.File;

public class WorldUtils {
    public static void cloneWorld(String sourceWorldName, String targetWorldName) throws Exception {
        File sourceFolder = new File(Bukkit.getWorldContainer(), sourceWorldName);
        File targetFolder = new File(Bukkit.getWorldContainer(), targetWorldName);

        if (!sourceFolder.exists()) {
            throw new Exception("Il mondo template " + sourceWorldName + " non esiste!");
        }

        FileUtils.copyFolder(sourceFolder, targetFolder);
    }
}
