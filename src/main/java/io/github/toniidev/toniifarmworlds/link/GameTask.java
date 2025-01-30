package io.github.toniidev.toniifarmworlds.link;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class GameTask {
    public abstract boolean canComplete(Player player);
    public abstract void complete(Player player);
    public abstract ItemStack getIcon(Player player);
    public abstract String toString();
    public static GameTask fromString(String string){
        String[] singleMaxiArgs = string.split("@");
        if(singleMaxiArgs[0].equals("singletask")){
            String[] args = singleMaxiArgs[1].split(",");
            if(args.length != 5) throw new IllegalArgumentException("Invalid string format for SingleTask");

            String materialName = args[0];
            int amount = Integer.parseInt(args[1]);
            String requestName = args[2];
            double reward = Double.parseDouble(args[3]);
            String clientName = args[4];

            return new SingleTask(Material.getMaterial(materialName), requestName,
                    amount, reward, clientName);
        }

        String[] multiMaxiArgs = string.split("#");
        if(multiMaxiArgs[0].equals("multitask")){
            String actual = multiMaxiArgs[1];
            String[] superactual = actual.split(":");
            String clientName = superactual[1];
            List<SingleTask> tasks = new ArrayList<>();
            String[] tasksString = superactual[0].split(";");
            for(String task : tasksString){
                tasks.add((SingleTask) SingleTask.fromString(task));
            }

            return new MultipleTask(tasks, clientName);
        }

        return null;
    }
}
