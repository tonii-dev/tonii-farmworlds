package io.github.toniidev.toniifarmworlds.database;

import io.github.toniidev.toniifarmworlds.database.DatabaseManager;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DatabaseItem<T> {
    private final File file;
    private final List<T> list;

    public DatabaseItem(Plugin main, String fileName, List<T> listLinkedToItem) {
        if (main == null) {
            throw new IllegalArgumentException("Main plugin instance cannot be null");
        }
        this.file = new File(main.getDataFolder(), fileName + ".db");
        this.list = listLinkedToItem;
    }

    public DatabaseItem(File customFile, List<T> listLinkedToItem) {
        this.file = customFile;
        this.list = listLinkedToItem;
    }

    public void save() {
        try {
            DatabaseManager.save(this.list, file);
        } catch (SQLException | IOException | IllegalAccessException e) {
            throw new RuntimeException("Failed to save the database item", e);
        }
    }

    public File getFile() {
        return file;
    }

    public List<T> getList() {
        return list;
    }
}