package io.github.toniidev.toniifarmworlds.classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class HistoryAction {
    private final LocalDateTime date;
    private final String name;

    public HistoryAction(String playerName){
        this.date = LocalDateTime.now();
        this.name = playerName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    // Create a HistoryAction from a string representation
    public static HistoryAction fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid string format for HistoryAction");
        }
        String type = parts[0];
        String playerName = parts[1];
        LocalDateTime date = LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return type.equals("accesso") ? new HistoryAccess(playerName, date) : new HistoryLeave(playerName, date);
    }

    // Constructor for deserialization
    public HistoryAction(String playerName, LocalDateTime date) {
        this.name = playerName;
        this.date = date;
    }
}
