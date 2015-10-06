package net.revtut.libraries.utils;

import net.minecraft.server.v1_8_R3.*;
import net.revtut.libraries.Libraries;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * World Library.
 *
 * <P>Library with several methods world related to such as loadWorld, unloadWorld, copyDirectory and so on.</P>
 *
 * @author Joao Silva
 * @version 1.0
 */
public final class WorldAPI {

    /**
     * Constructor of WorldAPI
     */
    private WorldAPI() {}

    /**
     * Load a new world to the server.
     *
     * @param worldName name of the world to load
     * @return loaded world
     */
    public static World loadWorld(final String worldName) {
        // World Creator
        final WorldCreator creator = new WorldCreator(worldName);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        return creator.createWorld();
    }

    /**
     * Load a new world to the server asynchronously.
     *
     * @param worldName name of the world to load
     * @return loaded world
     */
    public static World loadWorldAsync(final String worldName) {
        // World Creator
        final WorldCreator creator = new WorldCreator(worldName);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        // Load world async task
        Thread loadWorld = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    creator.createWorld();
                } catch (IllegalStateException exception) {
                } finally {
                    synchronized (this) {
                        notify();
                    }
                }
            }
        });
        loadWorld.start();

        // Wait for world
        synchronized (loadWorld) {
            try {
                loadWorld.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Bukkit.getWorld(worldName);
    }

    /**
     * Unload a world from the server.
     *
     * @param worldName name of the world to load
     * @return true if world was unloaded
     */
    public static boolean unloadWorld(final String worldName) {
        final World world = Bukkit.getWorld(worldName);
        if (world == null)
            return false;
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);

        // Kick remaining players
        world.getPlayers().forEach(player -> player.kickPlayer("§fUps, looks like I was disconnected!"));

        // Remove all living entities
        world.getLivingEntities().forEach(org.bukkit.entity.LivingEntity::remove);

        // Remove all entities
        world.getEntities().forEach(org.bukkit.entity.Entity::remove);

        // Unload all the chunks
        for (final Chunk chunk : world.getLoadedChunks()) {
            chunk.unload();
        }

        // Unload world
        final boolean successfull = Bukkit.unloadWorld(world, true);
        RegionFileCache.a();

        return successfull;
    }

    /**
     * Change damage of falling blocks
     *
     * @param block falling block to change damage
     * @param damage amout of damage
     * @param max max damge applied
     * @return true if successfull
     */
    public static boolean changeFallingBlockDamage(final FallingBlock block, final float damage, final int max) {
        try {
            // Falling block
            Class classzz = ReflectionAPI.getOBCClass("entity.CraftFallingSand");
            final Method getHandle = ReflectionAPI.getMethod(classzz, "getHandle");
            if(getHandle == null) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "'getHandle' method does not exist on falling block class.");
                return false;
            }

            final Object fallingBlock = getHandle.invoke(block);

            // Enable falling block damage
            classzz = ReflectionAPI.getNMSClass("EntityFallingBlock");
            Field field = ReflectionAPI.getField(classzz, "hurtEntities");
            if(field == null) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "'hurtEntities' field does not exist on falling block class.");
                return false;
            }
            field.setAccessible(true);
            field.setBoolean(fallingBlock, true);
            field.setAccessible(false);

            // Set the hurt amount of a falling block
            field = ReflectionAPI.getField(classzz, "fallHurtAmount");
            if(field == null) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "'fallHurtAmount' field does not exist on falling block class.");
                return false;
            }
            field.setAccessible(true);
            field.setFloat(fallingBlock, damage);
            field.setAccessible(false);

            // Set the maximum hurt amount of a falling block
            field = ReflectionAPI.getField(classzz, "fallHurtMax");
            if(field == null) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "'fallHurtMax' field does not exist on falling block class.");
                return false;
            }
            field.setAccessible(true);
            field.setInt(fallingBlock, max);
            field.setAccessible(false);

            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
