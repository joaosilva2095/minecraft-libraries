package net.revtut.libraries.games.events.player;

import net.revtut.libraries.games.arena.Arena;
import net.revtut.libraries.games.player.PlayerData;
import net.revtut.libraries.games.team.Team;

/**
 * Player Spectate Team Event
 */
public class PlayerSpectateTeamEvent extends PlayerSpectateArenaEvent {

    /**
     * Team that was spectated
     */
    private final Team team;

    /**
     * Constructor of PlayerSpectateTeamEvent
     * @param player player that spectated the team
     * @param arena arena that was spectated
     * @param team team that was spectated
     * @param joinMessage join message
     */
    public PlayerSpectateTeamEvent(final PlayerData player, final Arena arena, final Team team, final String joinMessage) {
        super(player, arena, joinMessage);
        this.team = team;
    }

    /**
     * Get the team that was spectated
     * @return team that was spectated
     */
    public Team getTeam() {
        return team;
    }

}
