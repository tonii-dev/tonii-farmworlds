package io.github.toniidev.toniifarmworlds.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtils {
    public static boolean checkPrerequisites(CommandSender sender){
        if(!(sender instanceof Player)){
            sender.sendMessage(StringUtils.formatColorCodes('&', "&e[Comando] &cErrore:&7 Puoi eseguire questo comando solo se sei un giocatore"));
            return false;
        }
        return true;
    }
}
