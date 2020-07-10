package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.event.ArenaStatusUpdateEvent;
import de.marcely.bedwars.api.event.PlayerQuitArenaEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;

import static com.grassminevn.bwaddon.Util.sendDataToSocket;

public class EventListener implements Listener {
  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (player.hasPermission("admin")) return;
    final Arena arena = BedwarsAPI.getArenas().get(0);
    sendDataToSocket("join:" + arena.getName() + ":" + player.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
    arena.addPlayer(player);
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitArenaEvent event) {
    final Arena arena = event.getArena();
    sendDataToSocket("quit:" + arena.getName() + ":" + event.getPlayer().getName() + ":" + arena.getPlayers().size() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
    Util.connect(event.getPlayer());
  }

  @EventHandler
  public void onArenaUpdate(final ArenaStatusUpdateEvent event) {
    final Arena arena = event.getArena();
    sendDataToSocket("update:" + arena.getName() + ":" + event.getStatus().name() + ":" + arena.getPlayers().size() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
  }

  @EventHandler
  public void onCommand(final ServerCommandEvent event) {
    System.out.println(event.getCommand());
  }
}