package io.github.toniidev.toniifarmworlds.link;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

public class ServerPlayerTypeAdapter extends TypeAdapter<ServerPlayer> {

    @Override
    public void write(JsonWriter out, ServerPlayer serverPlayer) throws IOException {
        out.beginObject();
        out.name("playerId").value(serverPlayer.getPlayer().getUniqueId().toString());
        out.name("displayName").value(serverPlayer.getDisplayName());
        out.name("maxSingleTasks").value(serverPlayer.getMaxSingleTasks());
        out.name("maxMultipleTasks").value(serverPlayer.getMaxMultipleTasks());
        out.name("singleTasks").beginArray();
        for(SingleTask task : serverPlayer.getSingleTasks()){
            out.value(task.toString());
        }
        out.endArray();
        out.name("multipleTasks").beginArray();
        for(MultipleTask task : serverPlayer.getMultipleTasks()){
            out.value(task.toString());
        }
        out.endArray();
        out.name("balance").value(serverPlayer.getMoney());
        out.endObject();
    }

    @Override
    public ServerPlayer read(JsonReader in) throws IOException {
        ServerPlayer player = new ServerPlayer();
        in.beginObject();
        while (in.hasNext()){
            switch (in.nextName()){
                case "playerId":
                    player.setPlayerId(UUID.fromString(in.nextString()));
                    break;
                case "displayName":
                    player.setDisplayName(in.nextString());
                    break;
                case "maxSingleTasks":
                    player.setMaxSingleTasks(Integer.parseInt(in.nextString()));
                    break;
                case "maxMultipleTasks":
                    player.setMaxMultipleTasks(Integer.parseInt(in.nextString()));
                    break;
                case "singleTasks":
                    in.beginArray();
                    while(in.hasNext()){
                        player.getSingleTasks().add((SingleTask) SingleTask.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "multipleTasks":
                    in.beginArray();
                    while(in.hasNext()){
                        player.getMultipleTasks().add((MultipleTask) MultipleTask.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "balance":
                    player.setMoney(Double.parseDouble(in.nextString()));
                    break;
            }
        }
        in.endObject();
        return player;
    }
}
