package com.grassminevn.bwaddon;

import com.grassminevn.bwaddon.placeholder.PlaceholderHandler;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BedwarsAddon extends JavaPlugin {
    public static BedwarsAddon getInstance() {
        return plugin;
    }

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
                for (final Arena arena : BedwarsAPI.getArenas()) {
                    Util.sendDataToSocket("enable:" + arena.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers() + ":" + arena.GetStatus().name() + ":" + arena.getPlayers().size());
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 200);

        new BukkitRunnable() {
            @Override
            public void run() {
                final Collection<Player> players = new HashSet<>();
                for (final Arena arena : BedwarsAPI.getArenas()) {
                    players.addAll(arena.getPlayers());
                    players.addAll(arena.getSpectators());
                }
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (players.contains(player) ||
                            debug.contains(player.getName())) continue;
                    Util.connect(player);
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 20);

        new PlaceholderHandler().register();
    }

    @Override
    public void onDisable() {
        for (final Arena arena : BedwarsAPI.getArenas()) {
            Util.sendDataToSocket("disable:" + arena.getName());
        }
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