package io.github.toniidev.toniifarmworlds.database;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.database.adapters.FarmTypeAdapter;
import io.github.toniidev.toniifarmworlds.database.adapters.LocalDateTimeAdapter;
import org.bukkit.Bukkit;

public class DatabaseManager {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Farm.class, new FarmTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // Save list of objects to a database file
    public static <T> void save(List<T> list, File dbFile) throws SQLException, IOException, IllegalAccessException {
        if(dbFile.exists()) dbFile.delete();

        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
            dbFile.createNewFile();
        }
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                for (T item : list) {
                    saveItem(conn, item);
                }
            }
        }

        Bukkit.getLogger().info("Saved something in the database file " + dbFile.getName());
    }

    // Load list of objects from a database file
    public static <T> List<T> load(File dbFile, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                createTableIfNotExists(conn, clazz);
                String tableName = clazz.getSimpleName();
                String query = "SELECT * FROM " + tableName;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        T item = gson.fromJson(rs.getString("data"), clazz);
                        list.add(item);
                    }
                }
            }
        }
        return list;
    }

    // Save a single item to the database
    private static <T> void saveItem(Connection conn, T item) throws SQLException, IllegalAccessException {
        String tableName = item.getClass().getSimpleName();
        createTableIfNotExists(conn, item.getClass());

        String insertSQL = "INSERT INTO " + tableName + " (data) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, gson.toJson(item));
            pstmt.executeUpdate();
        }
    }

    // Create table if it doesn't exist
    private static <T> void createTableIfNotExists(Connection conn, Class<T> clazz) throws SQLException {
        String tableName = clazz.getSimpleName();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (data TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Get SQL type from Java type
    private static String getSQLType(Class<?> type) {
        if (type == String.class) {
            return "TEXT";
        } else if (type == int.class || type == Integer.class) {
            return "INTEGER";
        } else if (type == long.class || type == Long.class) {
            return "BIGINT";
        } else if (type == double.class || type == Double.class) {
            return "REAL";
        } else if (type == float.class || type == Float.class) {
            return "REAL";
        } else if (type == boolean.class || type == Boolean.class) {
            return "BOOLEAN";
        } else if (type == UUID.class) {
            return "TEXT";
        } else if (type == List.class) {
            return "TEXT";
        } else if (type == LocalDateTime.class) {
            return "TEXT";
        } else if (type == Map.class) {
            return "TEXT";
        } else if (type == Farm.class) {
            return "TEXT";
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + type.getName());
        }
    }

    // Check if the field type is unsupported
    private static boolean isUnsupportedFieldType(Class<?> type) {
        return Comparator.class.isAssignableFrom(type) ||
                Map.Entry.class.isAssignableFrom(type) ||
                LinkedTreeMap.class.isAssignableFrom(type) ||
                Set.class.isAssignableFrom(type);
    }

    // Check if the field is synthetic or starts with $
    private static boolean isSyntheticField(Field field) {
        return field.isSynthetic() || field.getName().startsWith("$");
    }
}