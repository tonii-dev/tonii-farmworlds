package io.github.toniidev.toniifarmworlds.classes;

import java.time.LocalDateTime;
import java.util.Date;

public class HistoryAction {
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
}
