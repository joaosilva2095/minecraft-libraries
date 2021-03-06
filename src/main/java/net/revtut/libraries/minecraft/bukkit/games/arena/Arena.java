package net.revtut.libraries.minecraft.bukkit.games.arena;

import net.revtut.libraries.generic.util.Files;
import net.revtut.libraries.minecraft.bukkit.games.arena.session.GameSession;
import net.revtut.libraries.minecraft.bukkit.games.arena.session.GameState;
import net.revtut.libraries.minecraft.bukkit.games.arena.types.ArenaType;
import net.revtut.libraries.minecraft.bukkit.games.events.player.PlayerJoinArenaEvent;
import net.revtut.libraries.minecraft.bukkit.games.events.player.PlayerLeaveArenaEvent;
import net.revtut.libraries.minecraft.bukkit.games.events.player.PlayerSpectateArenaEvent;
import net.revtut.libraries.minecraft.bukkit.games.player.GamePlayer;
import net.revtut.libraries.minecraft.bukkit.games.player.PlayerState;
import net.revtut.libraries.minecraft.bukkit.utils.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Arena Object
 */
public abstract class Arena {

    /**
     * Current arena ID
     */
    private static int currentID = 0;

    /**
     * ID of the arena
     */
    private final int id;

    /**
     * Name of the arena
     */
    private final String name;

    /**
     * World of the arena
     */
    private World arenaWorld;

    /**
     * Lobby and spectator location of the arena
     */
    private Location lobbyLocation, spectatorLocation, spectatorDeathMatchLocation;

    /**
     * Corners of the arena
     * corner[0] - lowest corner
     * corner[1] - highest corner
     */
    private Location[] corners, cornersDeathMatch;

    /**
     * Current session of the arena
     */
    private GameSession currentSession;

    /**
     * Flags of the arena
     */
    private final Map<ArenaFlag, Boolean> flags;

    /**
     * Constructor of the Arena
     * @param name name of the arena
     */
    public Arena(final String name) {
        this.id = currentID++;
        this.name = this.id + "_" + name;
        this.flags = new HashMap<>();
    }

    /**
     * Initialize the arena
     * @param lobbyLocation location of the lobby
     * @param gameSession session of the arena
     */
    public void initArena(final Location lobbyLocation, final GameSession gameSession) {
        this.lobbyLocation = lobbyLocation;
        this.currentSession = gameSession;
    }

    /**
     * Initialize the arena world
     * @param arenaWorld world of the arena
     * @param spectatorLocation location of the spectator's spawn
     * @param spectatorDeathMatchLocation location of the spectator's spawn on death match
     * @param corners corners of the arena
     * @param cornersDeathMatch corners of the death match arena
     */
    public void initWorld(final World arenaWorld, final Location spectatorLocation, final Location spectatorDeathMatchLocation, final Location[] corners, final Location[] cornersDeathMatch) {
        this.arenaWorld = arenaWorld;
        this.spectatorLocation = spectatorLocation;
        this.spectatorDeathMatchLocation = spectatorDeathMatchLocation;

        // Make sure corners are in the right position
        Location lowestCorner = corners[0];
        Location highestCorner = corners[1];

        if(highestCorner.getX() < lowestCorner.getX()){
            final double temporary = highestCorner.getX();
            highestCorner.setX(lowestCorner.getX());
            lowestCorner.setX(temporary);
        }

        if(highestCorner.getY() < lowestCorner.getY()){
            final double temporary = highestCorner.getY();
            highestCorner.setY(lowestCorner.getY());
            lowestCorner.setY(temporary);
        }

        if(highestCorner.getZ() < lowestCorner.getZ()){
            final double temporary = highestCorner.getZ();
            highestCorner.setZ(lowestCorner.getZ());
            lowestCorner.setZ(temporary);
        }

        this.corners = corners;

        lowestCorner = cornersDeathMatch[0];
        highestCorner = cornersDeathMatch[1];

        if(highestCorner.getX() < lowestCorner.getX()){
            final double temporary = highestCorner.getX();
            highestCorner.setX(lowestCorner.getX());
            lowestCorner.setX(temporary);
        }

        if(highestCorner.getY() < lowestCorner.getY()){
            final double temporary = highestCorner.getY();
            highestCorner.setY(lowestCorner.getY());
            lowestCorner.setY(temporary);
        }

        if(highestCorner.getZ() < lowestCorner.getZ()){
            final double temporary = highestCorner.getZ();
            highestCorner.setZ(lowestCorner.getZ());
            lowestCorner.setZ(temporary);
        }

        this.cornersDeathMatch = cornersDeathMatch;
    }

    /**
     * Close the arena
     */
    public void close() {
        if(arenaWorld == null)
            return;

        Worlds.unloadWorld(arenaWorld.getName());
        Files.removeDirectory(arenaWorld.getWorldFolder());
    }

    /**
     * Get the ID of the arena
     * @return ID of the arena
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the arena
     * @return name of the arena
     */
    public String getName() {
        return name;
    }

    /**
     * Get the world of the arena
     * @return world of the arena
     */
    public World getWorld() {
        return arenaWorld;
    }

    /**
     * Get the lobby location of the arena
     * @return lobby location of the arena
     */
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    /**
     * Get the spectator location of the arena
     * @return spectator location of the arena
     */
    public Location getSpectatorLocation() {
        return spectatorLocation;
    }

    /**
     * Get the spectator location of death match of the arena
     * @return spectator location of death match of the arena
     */
    public Location getSpectatorDeathMatchLocation() {
        return spectatorDeathMatchLocation;
    }

    /**
     * Get the corners of the arena
     * @return corners of the arena, corner[0] lowest, corner[1] highest
     */
    public Location[] getCorners() {
        return corners;
    }

    /**
     * Get the corners of the death match of the arena
     * @return corners of the death match of the arena, corner[0] lowest, corner[1] highest
     */
    public Location[] getCornersDeathMatch() {
        return cornersDeathMatch;
    }

    /**
     * Get the current session of the arena
     * @return current session of the arena
     */
    public GameSession getSession() {
        return currentSession;
    }

    /**
     * Get players that are currently on a state
     * @param state state to filter players
     * @return players that correspond to that state
     */
    public List<GamePlayer> getPlayers(final PlayerState state) {
        return getAllPlayers().stream().filter(player -> player.getState() == state).collect(Collectors.toList());
    }

    /**
     * Get the number of players on the arena
     * @return number of players on the arena
     */
    public int getSize() {
        return getAllPlayers().size();
    }

    /**
     * Get a flag value
     * @param flag flag to get the value
     * @return value of the flag
     */
    public boolean getFlag(final ArenaFlag flag) {
        return flags.containsKey(flag) ? flags.get(flag) : true;
    }

    /**
     * Update / Add a flag to the arena
     * @param flag flag to be updated / added
     * @param value value of the flag
     */
    public void updateFlag(final ArenaFlag flag, final boolean value) {
        flags.put(flag, value);
    }

    /**
     * Make a player join the arena
     * @param player player to join
     * @return true if has joined, false otherwise
     */
    public boolean join(final GamePlayer player) {
        if(!canJoin(player))
            return false;

        // Call event
        final PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(player, this, player.getName() + " has joined the arena " + name);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return false;

        broadcastMessage(event.getJoinMessage());

        // Get bukkit player
        final Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
        if(bukkitPlayer == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Bukkit player for " + player.getName() + " is null on join!");
            return false;
        }

        // Update player
        player.updateState(PlayerState.ALIVE);
        player.setCurrentArena(this);
        bukkitPlayer.teleport(lobbyLocation);
        bukkitPlayer.setGameMode(GameMode.ADVENTURE);

        // Visibility configuration
        for(final GamePlayer target : getAllPlayers()) {
            final Player bukkitTarget = Bukkit.getPlayer(target.getUuid());
            if(bukkitTarget == null)
                continue;

            bukkitTarget.showPlayer(bukkitPlayer);

            if(target.getState() == PlayerState.SPECTATOR)
                bukkitPlayer.hidePlayer(bukkitTarget);
            else if(target.getState() == PlayerState.ALIVE)
                bukkitPlayer.showPlayer(bukkitTarget);
        }

        return true;
    }

    /**
     * Make a player leave the arena
     * @param player player to leave
     * @return true if has left, false otherwise
     */
    public boolean leave(final GamePlayer player) {
        // Call event
        final PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(player, this, player.getName() + " has left the arena " + name);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return false;

        broadcastMessage(event.getLeaveMessage());

        // Get bukkit player
        final Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
        if(bukkitPlayer == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Bukkit player for " + player.getName() + " is null on leave!");
            return false;
        }

        // Update player
        player.updateState(PlayerState.NOT_ASSIGNED);
        player.setCurrentArena(null);
        bukkitPlayer.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        bukkitPlayer.setGameMode(Bukkit.getDefaultGameMode());

        return true;
    }

    /**
     * Make a player spectate the arena
     * @param player player to spectate
     * @return true if is spectating, false otherwise
     */
    public boolean spectate(final GamePlayer player) {
        // Call event
        final PlayerSpectateArenaEvent event = new PlayerSpectateArenaEvent(player, this, player.getName() + " is spectating the arena " + name);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return false;

        broadcastMessage(event.getJoinMessage());

        // Get bukkit player
        final Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
        if(bukkitPlayer == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Bukkit player for " + player.getName() + " is null on spectate!");
            return false;
        }

        // Update player
        player.updateState(PlayerState.SPECTATOR);
        player.setCurrentArena(this);
        bukkitPlayer.teleport(lobbyLocation);
        bukkitPlayer.setGameMode(GameMode.SPECTATOR);

        // Hide to players ingame except spectators
        getAllPlayers().stream()
                .filter(target -> target.getState() != PlayerState.SPECTATOR)
                .filter(target -> Bukkit.getPlayer(target.getUuid()) != null)
                .forEach(target -> bukkitPlayer.hidePlayer(Bukkit.getPlayer(target.getUuid())));

        return true;
    }

    /**
     * Check if a player can join a arena
     * @param player player to be joined
     * @return true if can, false otherwise
     */
    public boolean canJoin(final GamePlayer player) {
        // Avoid joining when no session is created
        if(currentSession == null)
            return false;

        // Maximum players already achieved
        if(getSize() >= currentSession.getMaxPlayers())
            return false;

        // Arena is already ingame
        if(currentSession.getState() != GameState.LOBBY)
            return false;

        return true;
    }

    /**
     * Broadcast a message to the arena
     * @param message message to be broadcast
     */
    public void broadcastMessage(final String message) {
        for(final GamePlayer player : getAllPlayers()) {
            final Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
            if (bukkitPlayer != null)
                bukkitPlayer.sendMessage(message);
        }
    }

    /**
     * Get all the players on the arena
     * @return players on the arena
     */
    public abstract List<GamePlayer> getAllPlayers();

    /**
     * Get the type of the arena
     * @return type of the arena
     */
    public abstract ArenaType getType();

    /**
     * Check if the arena contains a given player by its UUID
     * @param uuid uuid of the player to be checked
     * @return true if contains, false otherwise
     */
    public abstract boolean containsPlayer(UUID uuid);
}