package net.revtut.libraries.games.events.gun;

import net.revtut.libraries.games.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Gun Event
 */
public abstract class GunEvent extends Event implements Cancellable {

    /**
     * Handlers list
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Player that used the gun
     */
    private Player shooter;

    /**
     * Gun that was used
     */
    private Gun gun;

    /**
     * Flag to check if the event was cancelled
     */
    private boolean isCancelled;

    /**
     * Constructor of GunEvent
     * @param shooter player that used the gun
     * @param gun gun that was used
     */
    public GunEvent(Player shooter, Gun gun) {
        this.shooter = shooter;
        this.gun = gun;
        this.isCancelled = false;
    }

    /**
     * Get the player that used the gun
     * @return player that used the gun
     */
    public Player getShooter() {
        return shooter;
    }

    /**
     * Get the gun that was used
     * @return gun that was used
     */
    public Gun getGun() {
        return gun;
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
    public void setCancelled(boolean cancelled) {
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
