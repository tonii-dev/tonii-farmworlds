package io.github.toniidev.toniifarmworlds;

import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.commands.CreateFarm;
import io.github.toniidev.toniifarmworlds.commands.GeneralFarm;
import io.github.toniidev.toniifarmworlds.factories.InputFactory;
import io.github.toniidev.toniifarmworlds.factories.InventoryFactory;
import io.github.toniidev.toniifarmworlds.utils.InitializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToniiFarmWorlds extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(new InventoryFactory(), this);
        Bukkit.getPluginManager().registerEvents(new InputFactory(this), this);
        Bukkit.getPluginManager().registerEvents(new Farm(), this);

        new InitializeUtils(new CreateFarm(), "creafattoria").initialize();
        new InitializeUtils(new GeneralFarm(this), "fattoria").initialize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
