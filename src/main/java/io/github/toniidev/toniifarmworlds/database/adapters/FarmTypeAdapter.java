package io.github.toniidev.toniifarmworlds.database.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.toniidev.toniifarmworlds.classes.Farm;
import io.github.toniidev.toniifarmworlds.classes.HistoryAction;

import java.io.IOException;
import java.util.UUID;

public class FarmTypeAdapter extends TypeAdapter<Farm> {

    @Override
    public void write(JsonWriter out, Farm farm) throws IOException {
        out.beginObject();
        out.name("owner").value(farm.getOwner().toString());
        out.name("whitelist").beginArray();
        for (UUID uuid : farm.getWhitelist()) {
            out.value(uuid.toString());
        }
        out.endArray();
        out.name("worldName").value(farm.getWorldName());
        out.name("history").beginArray();
        for (HistoryAction action : farm.getHistory()) {
            out.value(action.toString()); // Assuming HistoryAction has a proper toString implementation
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public Farm read(JsonReader in) throws IOException {
        Farm farm = new Farm();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "owner":
                    farm.setOwner(UUID.fromString(in.nextString()));
                    break;
                case "whitelist":
                    in.beginArray();
                    while (in.hasNext()) {
                        farm.getWhitelist().add(UUID.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "worldName":
                    farm.setWorldName(in.nextString());
                    break;
                case "history":
                    in.beginArray();
                    while (in.hasNext()) {
                        farm.getHistory().add(HistoryAction.fromString(in.nextString())); // Assuming HistoryAction has a fromString method
                    }
                    in.endArray();
                    break;
            }
        }
        in.endObject();
        return farm;
    }
}