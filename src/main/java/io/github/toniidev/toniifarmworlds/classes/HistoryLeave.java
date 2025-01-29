package io.github.toniidev.toniifarmworlds.classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryLeave extends HistoryAction{
    public HistoryLeave(String playerName) {
        super(playerName);
    }

    public HistoryLeave(String playerName, LocalDateTime date){
        super(playerName, date);
    }

    @Override
    public String toString(){
        return "uscita," + this.getName() + "," + this.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
