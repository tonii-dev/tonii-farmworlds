package io.github.toniidev.toniifarmworlds.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {
    public static Player getPlayer(UUID uuid){
        if(Bukkit.getPlayer(uuid) != null) return Bukkit.getPlayer(uuid);
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
    }
}
