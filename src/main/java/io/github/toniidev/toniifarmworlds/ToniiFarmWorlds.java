package io.github.toniidev.toniifarmworlds;

import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.commands.CreateFarm;
import io.github.toniidev.toniifarmworlds.commands.GeneralFarm;
import io.github.toniidev.toniifarmworlds.database.DatabaseManager;
import io.github.toniidev.toniifarmworlds.factories.InputFactory;
import io.github.toniidev.toniifarmworlds.factories.InventoryFactory;
import io.github.toniidev.toniifarmworlds.link.ServerPlayer;
import io.github.toniidev.toniifarmworlds.utils.InitializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class ToniiFarmWorlds extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic

        InitializeUtils.setMainInstance(this);

        // load farms
        Farm.load(this);
        ServerPlayer.load(this);

        Bukkit.getPluginManager().registerEvents(new InventoryFactory(), this);
        Bukkit.getPluginManager().registerEvents(new InputFactory(this), this);
        Bukkit.getPluginManager().registerEvents(new Farm(), this);

        new InitializeUtils(new CreateFarm(), "creafattoria").initialize();
        new InitializeUtils(new GeneralFarm(this), "fattoria").initialize();
    }

    @Override
    public void onDisable() {
        for(Farm farm : Farm.getTerrains()){
            farm.save();
        }
    }
}
