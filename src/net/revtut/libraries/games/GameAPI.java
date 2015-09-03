package net.revtut.libraries.games;

import net.revtut.libraries.Libraries;
import net.revtut.libraries.games.arena.Arena;
import net.revtut.libraries.games.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Game API.
 */
public class GameAPI {

    /**
     * Instance of GameAPI
     */
    private static GameAPI instance;

    /**
     * List with all players
     */
    private List<PlayerData> players;

    /**
     * List with all game controllers
     */
    private List<GameController> games;

    /**
     * Constructor of GameAPI
     */
    private GameAPI() {
        this.players = new ArrayList<>();
        this.games = new ArrayList<>();

        // Run the tick task
        Bukkit.getScheduler().runTaskTimer(Libraries.getInstance(), () -> {
            for(GameController gameController : games)
                for(Arena arena : gameController.getArenas())
                    arena.getSession().tick();
        }, 20L, 20L);
    }

    /**
     * Get the instance of game api
     * @return instance of game api
     */
    public static GameAPI getInstance() {
        if(instance == null)
            instance = new GameAPI();
        return instance;
    }

    /**
     * Register a game
     * @param plugin plugin of the game
     * @param worldsFolder folder where worlds are located
     * @return controller of the game
     */
    public GameController registerGame(Plugin plugin, File worldsFolder) {
        GameController gameController = new GameController(plugin, worldsFolder);
        games.add(gameController);
        return gameController;
    }

    /**
     * Unregister a game
     * @param plugin plugin of the game
     */
    public void unregisterGame(Plugin plugin) {
        GameController gameController = getGameController(plugin);
        if(gameController != null)
            games.remove(gameController);
    }

    /**
     * Get the game controller of a plugin
     * @param plugin plugin to get the game controller
     * @return game controller of the plugin
     */
    public GameController getGameController(Plugin plugin) {
        for(GameController gameController : games)
            if(gameController.getPlugin().equals(plugin))
                return gameController;
        return null;
    }

    /**
     * Get all the players on the server
     * @return players on the server
     */
    public List<PlayerData> getPlayers() {
        return players;
    }

    /**
     * Get the player data of a given player
     * @param uuid uuid of the player to get the player data
     * @return player data of the player
     */
    public PlayerData getPlayer(UUID uuid) {
        for(PlayerData player : players)
            if(player.getUuid().equals(uuid))
                return player;
        return null;
    }

    /**
     * Get the arena of a given player
     * @param uuid uuid of the player
     * @return arena of the player
     */
    public Arena getPlayerArena(UUID uuid) {
        Arena arena = null;
        for(GameController gameController : games) {
            arena = gameController.getPlayerArena(uuid);
            if(arena != null)
                break;
        }

        return arena;
    }

    /**
     * Add a player to the list
     * @param player player to be added
     */
    public void addPlayer(PlayerData player) {
        players.add(player);
    }

    /**
     * Remove a player from the list
     * @param player player to be removed
     */
    public void removePlayer(PlayerData player) {
        players.remove(player);
    }

    /**
     * Building the arena
    public void build() {
        // First time calling this method
        if(getSession().getState() != GameState.BUILD) {
            Libraries.getInstance().getLogger().log(Level.INFO, "[" + getName() + "] Started building!");

            // Remove previous world
            if(getWorld() != null) {
                String worldName = getWorld().getName();

                WorldAPI.unloadWorld(worldName);

                File worldFolder = new File(System.getProperty("user.dir") + File.separator + worldName);
                WorldAPI.removeDirectory(worldFolder);
            }

            // Copy world folder
            String[] listWorlds = getWorldsFolder().list();
            if (listWorlds == null)
                throw new IllegalStateException("List of worlds is null.");

            int posWorld = (int) (Math.random() * listWorlds.length);
            final String sourcePath = new File(getWorldsFolder() + File.separator + listWorlds[posWorld]).getAbsolutePath();
            final String mapName = getName() + "_" + listWorlds[posWorld];
            final String targetPath = new File(System.getProperty("user.dir") + File.separator + mapName).getAbsolutePath();

            WorldAPI.copyDirectory(new File(sourcePath), new File(targetPath));

            // Load World
            World world = WorldAPI.loadWorldAsync(mapName);
            if(world == null)
                throw new IllegalStateException("Loaded world is null.");
            world.setAutoSave(false);
        }
    }
     */
}
