package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BedwarsAddon extends JavaPlugin {
    public static BedwarsAddon getInstance() {
/* 11 */     return plugin;
/*    */   }
    private static BedwarsAddon plugin;
    static Set<String> debug;
    private static FileConfiguration debugConfig;

    @Override
    public void onEnable() {
        saveConfig();
        plugin = this;
        reloadSettings();
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
                Util.sendDataToSocket("enable:" + arena.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers() + ":" + arena.GetStatus().name());
            }
        }.runTaskTimerAsynchronously(this, 0, 200);

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<Arena> list = BedwarsAPI.getArenas();
                if (list.size() == 0) return;
                final Arena arena = list.get(0);
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (arena.getPlayers().contains(player) ||
                    arena.getSpectators().contains(player)) continue;
                    Util.connect(player);
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        Util.sendDataToSocket("disable:" + BedwarsAPI.getArenas().get(0).getName());
    }

    public static void reloadSettings() {
        plugin.reloadConfig();
        final File file = new File(plugin.getDataFolder(), "debug.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        debugConfig = YamlConfiguration.loadConfiguration(file);
        debug = new HashSet<>(debugConfig.getStringList("list"));
        Util.lobby = plugin.getConfig().getString("lobby");
    }

    public static void saveDebug() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                debugConfig.save(new File(plugin.getDataFolder(), "debug.yml"));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}