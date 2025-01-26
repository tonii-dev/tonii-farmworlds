package io.github.toniidev.toniifarmworlds.commands;

import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.utils.CommandUtils;
import io.github.toniidev.toniifarmworlds.utils.StringUtils;
import io.github.toniidev.toniifarmworlds.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CreateFarm implements CommandExecutor {
    private final String TEMPLATE_WORLD = "template";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!CommandUtils.checkPrerequisites(commandSender)) return true;

        Player player = (Player) commandSender;
        String worldName = "farm_" + player.getUniqueId();

        // Check if Player already has a terrain
        if(Farm.doesPlayerOwnATerrain(player)){
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Mondo] &aFattoria:&7 Hai già una " +
                    "fattoria! Ti teletrasporto..."));

            player.teleport(Farm.reverse(player).get().getWorld().getSpawnLocation());
        }

        // Check if world has been generated but not loaded
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if(worldFolder.exists()){
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Mondo] &aFattoria:&7 Caricamento del tuo " +
                    "mondo..."));
            World world = new Farm(player).createWorld();
            player.teleport(world.getSpawnLocation());
            return true;
        }

        // Clone template world
        player.sendMessage(StringUtils.formatColorCodes('&', "&e[Mondo] &aFattoria:&7 Sto creando la tua " +
                "fattoria. Attendi..."));
        try{
            WorldUtils.cloneWorld(TEMPLATE_WORLD, worldName);
            World world = new Farm(player).createWorld();
            world.setSpawnLocation(-4, -60, -6);
            player.teleport(world.getSpawnLocation());
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Mondo] &aFattoria:&7 Fattoria creata! Buon divertimento."));
        } catch (Exception ex){
            player.sendMessage(StringUtils.formatColorCodes('&', "&e[Mondo] &aFattoria:&7 Errore durante la creazione della tua fattoria. " +
                    "Contatta un admin."));
            Bukkit.getLogger().info(player.getDisplayName() + " tried to create a farm but there was an error. Stack trace:");
            ex.printStackTrace();
        }

        return true;
    }
}
