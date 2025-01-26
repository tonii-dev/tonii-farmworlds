package io.github.toniidev.toniifarmworlds.factories;

import io.github.toniidev.toniifarmworlds.interfaces.ChatInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Input factory. This class is going to be used to ask any user
 * an input in chat and directly getting their response. This
 * class also contains some Listeners, to handle the chat actions.
 */
public class InputFactory implements Listener {

    /**
     * This list is temporary, it means that it will be reset when the plugin enables or
     * disables. But that's ok, because we don't need its items to be always available: any
     * time we ask a Player for an input, a new instance of InputFactory gets created.
     */
    private final static Set<InputFactory> factories = new HashSet<>();

    /// It cannot be final since the blank constructor does not set this
    private final Plugin main;

    /// It cannot be final since the blank constructor does not set this
    private Player watching;
    private ChatInterface action;

    /**
     * Creates an InputFactory instance
     *
     * @param player The player that the InputFactory should watch and handle
     */
    public InputFactory(Player player, Plugin plugin) {
        this.watching = player;
        this.main = plugin;

        InputFactory.factories.add(this);
    }

    /**
     * Initialize a reference to this. It is only used to register Listener
     */
    public InputFactory(Plugin plugin) {
        this.main = plugin;
    }

    /**
     * Default setter for this class
     *
     * @param actionToExecute The action that has to be executed when the Player that this InputFactory
     *                        is watching writes something in chat
     * @return This InputFactory instance
     */
    public InputFactory setAction(ChatInterface actionToExecute) {
        this.action = actionToExecute;
        return this;
    }

    /**
     * Sets the title to show to the player when the message handling begins
     *
     * @param value The string that will be shown as title to the player
     * @return This InputFactory instance
     */
    public InputFactory setTitle(String value) {
        return this;
    }

    /**
     * Sets the subtitle to show to the player when the message handling begins
     *
     * @param value The string that will be shown as subtitle to the player
     * @return This InputFactory instance
     */
    public InputFactory setSubtitle(String value) {
        return this;
    }

    /**
     * Gets the InputFactory instance linked to the specified Player, if it exists
     *
     * @param player The player it is needed to get the InputFactory of
     * @return The InputFactory linked to the specified player if it exists, null if
     * it doesn't exist
     */
    @Nullable
    public static InputFactory getFactory(Player player) {
        return InputFactory.factories.stream()
                .filter(x -> x.getWatching().equals(player))
                .findFirst().orElse(null);
    }

    /**
     * Tells whether the specified player is required to write an input in chat or not
     *
     * @param player The player that we must check whether he must write an input or not
     * @return true if the player should write an input, false if the player shouldn't write any input
     */
    public static boolean isPlayerBeingWatched(Player player) {
        return getFactory(player) != null;
    }

    /**
     * Removes the current InputFactory instance from the InputFactory list.
     * WARNING: It should only be used when all the handling is done!
     */
    public void dispatch() {
        InputFactory.factories.remove(this);
    }

    /**
     * Default getter for this class
     *
     * @return The Player this InputFactory is watching
     */
    public Player getWatching() {
        return watching;
    }

    /**
     * Default getter for this class
     *
     * @return The action to execute when the User (if being watched)
     * writes something in chat
     */
    public ChatInterface getAction() {
        return action;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!InputFactory.isPlayerBeingWatched(e.getPlayer())) return;

        InputFactory factory = InputFactory.getFactory(e.getPlayer());
        assert factory != null;

        if (factory.getAction() == null) return;
        e.setCancelled(true);
        /// We must use a BukkitRunnable because the AsyncPlayerChatEvent is async
        new BukkitRunnable() {
            @Override
            public void run() {
                factory.getAction().run(e);
            }
        }.runTaskLater(main, 1L);

        e.getPlayer().resetTitle();
        factory.dispatch();
    }
}
