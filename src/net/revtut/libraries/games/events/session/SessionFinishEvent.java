package net.revtut.libraries.games.events.session;

import net.revtut.libraries.games.arena.session.GameSession;

/**
 * Session Finish Event
 */
public class SessionFinishEvent extends SessionEvent {

    /**
     * Constructor of SessionFinishEvent
     * @param gameSession session where the event occurred
     */
    public SessionFinishEvent(GameSession gameSession) {
        super(gameSession);
    }
}