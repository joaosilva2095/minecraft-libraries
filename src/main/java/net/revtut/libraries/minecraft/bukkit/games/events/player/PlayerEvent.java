package net.revtut.libraries.minecraft.bukkit.games.events.player;

import net.revtut.libraries.minecraft.bukkit.games.arena.Arena;
import net.revtut.libraries.minecraft.bukkit.games.player.GamePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Player Event
 */
public abstract class PlayerEvent extends Event implements Cancellable {

    /**
     * Handlers list
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Player that caused the event
     */
    private final GamePlayer player;

    /**
     * Arena where the event occurred
     */
    private final Arena arena;

    /**
     * Flag to check if the event was cancelled
     */
    private boolean isCancelled;

    /**
     * Constructor of PlayerEvent
     * @param player player that caused the event
     * @param arena arena where the event occurred
     */
    public PlayerEvent(final GamePlayer player, final Arena arena) {
        this.player = player;
        this.arena = arena;
        this.isCancelled = false;
    }

    /**
     * Get the player that caused the event
     * @return player that caused the event
     */
    public GamePlayer getPlayer() {
        return player;
    }

    /**
     * Get the arena where the event occurred
     * @return arena where the event occurred
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Get the handlers of the event
     * @return handlers of the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Check if the event is cancelled
     * @return true if event is cancelled, false otherwise
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Set the event as cancelled or not
     * @param cancelled new value for cancelled
     */
    public void setCancelled(final boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /**
     * Get the handlers of the event
     * @return handlers of the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
