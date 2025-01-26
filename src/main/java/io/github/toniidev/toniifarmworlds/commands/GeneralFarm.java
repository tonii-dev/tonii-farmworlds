package io.github.toniidev.toniifarmworlds.commands;

import io.github.toniidev.toniifarmworlds.gui.FarmManagement;
import io.github.toniidev.toniifarmworlds.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GeneralFarm implements CommandExecutor {
    private final Plugin plugin;

    public GeneralFarm(Plugin main){
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!CommandUtils.checkPrerequisites(commandSender)) return true;

        Player player = (Player) commandSender;
        player.openInventory(FarmManagement.getHome(player, plugin));

        return true;
    }
}
