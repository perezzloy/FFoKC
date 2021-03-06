package org.bukkit.craftbukkit;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.server.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.bukkit.plugin.java.JavaPluginLoader;
import org.spigotmc.CustomTimingsHandler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;import java.util.concurrent.*;

import org.bukkit.craftbukkit.scheduler.CraftTask;

public class SpigotTimings {

    public static final CustomTimingsHandler serverTickTimer = new CustomTimingsHandler("** Full Server Tick");
    public static final CustomTimingsHandler playerListTimer = new CustomTimingsHandler("Player List");
    public static final CustomTimingsHandler connectionTimer = new CustomTimingsHandler("Connection Handler");
    public static final CustomTimingsHandler tickablesTimer = new CustomTimingsHandler("Tickables");
    public static final CustomTimingsHandler schedulerTimer = new CustomTimingsHandler("Scheduler");
    public static final CustomTimingsHandler chunkIOTickTimer = new CustomTimingsHandler("ChunkIOTick");
    public static final CustomTimingsHandler timeUpdateTimer = new CustomTimingsHandler("Time Update");
    public static final CustomTimingsHandler serverCommandTimer = new CustomTimingsHandler("Server Command");
    public static final CustomTimingsHandler worldSaveTimer = new CustomTimingsHandler("World Save");

    public static final CustomTimingsHandler entityMoveTimer = new CustomTimingsHandler("** entityMove");
    public static final CustomTimingsHandler tickEntityTimer = new CustomTimingsHandler("** tickEntity");
    public static final CustomTimingsHandler activatedEntityTimer = new CustomTimingsHandler("** activatedTickEntity");
    public static final CustomTimingsHandler tickTileEntityTimer = new CustomTimingsHandler("** tickTileEntity");

    public static final CustomTimingsHandler timerEntityBaseTick = new CustomTimingsHandler("** livingEntityBaseTick");
    public static final CustomTimingsHandler timerEntityAI = new CustomTimingsHandler("** livingEntityAI");
    public static final CustomTimingsHandler timerEntityAICollision = new CustomTimingsHandler("** livingEntityAICollision");
    public static final CustomTimingsHandler timerEntityAIMove = new CustomTimingsHandler("** livingEntityAIMove");
    public static final CustomTimingsHandler timerEntityTickRest = new CustomTimingsHandler("** livingEntityTickRest");

    public static final CustomTimingsHandler processQueueTimer = new CustomTimingsHandler("processQueue");
    public static final CustomTimingsHandler schedulerSyncTimer = new CustomTimingsHandler("** Scheduler - Sync Tasks", JavaPluginLoader.pluginParentTimer);

    public static final CustomTimingsHandler playerCommandTimer = new CustomTimingsHandler("** playerCommand");

    public static final CustomTimingsHandler entityActivationCheckTimer = new CustomTimingsHandler("entityActivationCheck");
    public static final CustomTimingsHandler checkIfActiveTimer = new CustomTimingsHandler("** checkIfActive");

    public static final Map<String, CustomTimingsHandler> entityTypeTimingMap = new ConcurrentHashMap<String, CustomTimingsHandler>();
    public static final Map<String, CustomTimingsHandler> tileEntityTypeTimingMap = new ConcurrentHashMap<String, CustomTimingsHandler>();
    public static final Map<String, CustomTimingsHandler> pluginTaskTimingMap = new ConcurrentHashMap<String, CustomTimingsHandler>();
    private static final String a2s_text = "** ";

    /**
     * Gets a timer associated with a plugins tasks.
     * @param task
     * @param period
     * @return
     */
    public static CustomTimingsHandler getPluginTaskTimings(BukkitTask task, long period) {
        if (!task.isSync()) {
            return null;
        }
        String plugin;
        final CraftTask ctask = (CraftTask) task;

        if (task.getOwner() != null) {
            plugin = task.getOwner().getDescription().getFullName();
        } else if (ctask.timingName != null) {
            plugin = "CraftScheduler";
        } else {
            plugin = "Unknown";
        }
        String taskname = ctask.getTaskName();

        String name = "Task: " + plugin + " Runnable: " + taskname;
        StringBuilder nameSB = new StringBuilder(name);
        if (period > 0) {
            nameSB.append("(interval:").append(period).append(')');
        } else {
            nameSB.append("(Single)");
        }
        name = String.valueOf(nameSB);
        CustomTimingsHandler result = pluginTaskTimingMap.get(name);
        if (result == null) {
            result = new CustomTimingsHandler(name, SpigotTimings.schedulerSyncTimer);
            pluginTaskTimingMap.put(name, result);
        }
        return result;
    }

    /**
     * Get a named timer for the specified entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static CustomTimingsHandler getEntityTimings(Entity entity) {
        String entityType = entity.getClass().getSimpleName();
        CustomTimingsHandler result = entityTypeTimingMap.get(entityType);
        if (result == null) {
            result = new CustomTimingsHandler("** tickEntity - " + entityType, activatedEntityTimer);
            entityTypeTimingMap.put(entityType, result);
        }
        return result;
    }

    /**
     * Get a named timer for the specified tile entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static CustomTimingsHandler getTileEntityTimings(TileEntity entity) {
        String entityType = entity.getClass().getSimpleName();
        CustomTimingsHandler result = tileEntityTypeTimingMap.get(entityType);
        if (result == null) {
            result = new CustomTimingsHandler("** tickTileEntity - " + entityType, tickTileEntityTimer);
            tileEntityTypeTimingMap.put(entityType, result);
        }
        return result;
    }

    /**
     * Set of timers per world, to track world specific timings.
     */
    public static class WorldTimingsHandler {
        public final CustomTimingsHandler mobSpawn;
        public final CustomTimingsHandler doChunkUnload;
        public final CustomTimingsHandler doPortalForcer;
        public final CustomTimingsHandler doTickPending;
        public final CustomTimingsHandler doTickTiles;
        public final CustomTimingsHandler doVillages;
        public final CustomTimingsHandler doChunkMap;
        public final CustomTimingsHandler doChunkGC;
        public final CustomTimingsHandler doSounds;
        public final CustomTimingsHandler entityTick;
        public final CustomTimingsHandler tileEntityTick;
        public final CustomTimingsHandler tileEntityPending;
        public final CustomTimingsHandler tracker;
        public final CustomTimingsHandler doTick;
        public final CustomTimingsHandler tickEntities;

        public final CustomTimingsHandler syncChunkLoadTimer;
        public final CustomTimingsHandler syncChunkLoadDataTimer;
        public final CustomTimingsHandler syncChunkLoadStructuresTimer;
        public final CustomTimingsHandler syncChunkLoadEntitiesTimer;
        public final CustomTimingsHandler syncChunkLoadTileEntitiesTimer;
        public final CustomTimingsHandler syncChunkLoadTileTicksTimer;
        public final CustomTimingsHandler syncChunkLoadPostTimer;

        public WorldTimingsHandler(World server) {
            String name = server.worldInfo.getWorldName() +" - ";

            mobSpawn = new CustomTimingsHandler(a2s_text + name + "mobSpawn");
            doChunkUnload = new CustomTimingsHandler(a2s_text + name + "doChunkUnload");
            doTickPending = new CustomTimingsHandler(a2s_text + name + "doTickPending");
            doTickTiles = new CustomTimingsHandler(a2s_text + name + "doTickTiles");
            doVillages = new CustomTimingsHandler(a2s_text + name + "doVillages");
            doChunkMap = new CustomTimingsHandler(a2s_text + name + "doChunkMap");
            doSounds = new CustomTimingsHandler(a2s_text + name + "doSounds");
            doChunkGC = new CustomTimingsHandler(a2s_text + name + "doChunkGC");
            doPortalForcer = new CustomTimingsHandler(a2s_text + name + "doPortalForcer");
            entityTick = new CustomTimingsHandler(a2s_text + name + "entityTick");
            tileEntityTick = new CustomTimingsHandler(a2s_text + name + "tileEntityTick");
            tileEntityPending = new CustomTimingsHandler(a2s_text + name + "tileEntityPending");

            syncChunkLoadTimer = new CustomTimingsHandler(a2s_text + name + "syncChunkLoad");
            syncChunkLoadDataTimer = new CustomTimingsHandler(a2s_text + name + "syncChunkLoad - Data");
            syncChunkLoadStructuresTimer = new CustomTimingsHandler(a2s_text + name + "chunkLoad - Structures");
            syncChunkLoadEntitiesTimer = new CustomTimingsHandler(a2s_text + name + "chunkLoad - Entities");
            syncChunkLoadTileEntitiesTimer = new CustomTimingsHandler(a2s_text + name + "chunkLoad - TileEntities");
            syncChunkLoadTileTicksTimer = new CustomTimingsHandler(a2s_text + name + "chunkLoad - TileTicks");
            syncChunkLoadPostTimer = new CustomTimingsHandler(a2s_text + name + "chunkLoad - Post");


            tracker = new CustomTimingsHandler(name + "tracker");
            doTick = new CustomTimingsHandler(name + "doTick");
            tickEntities = new CustomTimingsHandler(name + "tickEntities");
        }
    }
}