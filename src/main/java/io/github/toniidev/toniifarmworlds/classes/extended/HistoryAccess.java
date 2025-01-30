package io.github.toniidev.toniifarmworlds.classes.extended;

import io.github.toniidev.toniifarmworlds.classes.HistoryAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryAccess extends HistoryAction {
    public HistoryAccess(String playerName) {
        super(playerName);
    }

    public HistoryAccess(String playerName, LocalDateTime date){
        super(playerName, date);
    }

    @Override
    public String toString(){
        return "accesso," + this.getName() + "," + this.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
