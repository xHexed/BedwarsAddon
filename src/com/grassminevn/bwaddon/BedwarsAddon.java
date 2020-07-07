package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.ServerSocket;

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

        try {
            new ServerSocket(2);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final Arena arena = BedwarsAPI.getArenas().get(0);
        Util.sendDataToSocket("enable:" + arena.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
    }

    @Override
    public void onDisable() {
        Util.sendDataToSocket("disable:" + BedwarsAPI.getArenas().get(0));
    }
}