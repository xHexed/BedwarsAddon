package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BedwarsAddon extends JavaPlugin {
    public static BedwarsAddon getInstance() {
/* 11 */     return plugin;
/*    */   }
    private static BedwarsAddon plugin;

    @Override
    public void onEnable() {
        saveConfig();
        Util.lobby = getConfig().getString("lobby");
        plugin = this;
        final CommandExecutor cmd = new CommandHandler();
        getCommand("bwa").setExecutor(cmd);
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<Arena> list = BedwarsAPI.getArenas();
                if (list.size() == 0) return;
                final Arena arena = list.get(0);
                Util.sendDataToSocket("enable:" + arena.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
            }
        }.runTaskTimerAsynchronously(this, 0, 200);
    }

    @Override
    public void onDisable() {
        Util.sendDataToSocket("disable:" + BedwarsAPI.getArenas().get(0).getName());
    }
}