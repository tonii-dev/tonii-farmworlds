package io.github.toniidev.toniifarmworlds.database;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DatabaseItem<T> {
    private final File file;
    private final List<T> list;

    public DatabaseItem(Plugin main, String fileName, List<T> listLinkedToItem) {
        if (main == null || fileName == null || listLinkedToItem == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.file = new File(main.getDataFolder(), fileName + ".db");
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