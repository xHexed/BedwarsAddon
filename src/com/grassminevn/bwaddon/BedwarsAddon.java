package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.ServerSocket;

public class BedwarsAddon extends JavaPlugin {
    public static BedwarsAddon getInstance() {
/* 11 */     return plugin;
/*    */   }
    private static BedwarsAddon plugin;
    private static ServerSocket socket;
    private static final BukkitRunnable sendTask = new BukkitRunnable() {
        @Override
        public void run() {
            System.out.println("Sending arena enable data...");
            final Arena arena = BedwarsAPI.getArenas().get(0);
            Util.sendDataToSocket("enable:" + arena.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
        }
    };

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Util.lobby = getConfig().getString("lobby");
        plugin = this;
        final CommandExecutor cmd = new CommandHandler();
        getCommand("bwa").setExecutor(cmd);
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        sendTask.runTaskTimerAsynchronously(this, 0, 200);

        try {
            socket = new ServerSocket(getServer().getPort());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                socket.accept();
                sendTask.cancel();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDisable() {
        Util.sendDataToSocket("disable:" + BedwarsAPI.getArenas().get(0));
    }
}