package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.*;
import de.marcely.bedwars.api.event.ArenaStatusUpdateEvent;
import de.marcely.bedwars.api.event.PlayerJoinArenaEvent;
import de.marcely.bedwars.api.event.PlayerQuitArenaEvent;
import de.marcely.bedwars.api.event.PlayerQuitArenaSpectatorEvent;
import de.marcely.bedwars.dD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;

import static com.grassminevn.bwaddon.Util.sendDataToSocket;

public class EventListener implements Listener {
  @EventHandler
  public void arenaDebug(final PlayerJoinArenaEvent event) {
    if (event.getFailReason() == null) return;
    System.out.println(event.getFailReason().name());
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (BedwarsAddon.debug.contains(player.getName())) return;
    final Arena arena = BedwarsAPI.getArenas().get(0);
    if (arena.GetStatus().equals(ArenaStatus.Running)) {
      dD.a(player, (de.marcely.bedwars.game.arena.Arena) arena, SpectateReason.PLUGIN);
      return;
    }
    arena.addPlayer(player);
    sendDataToSocket("join:" + arena.getName() + ":" + player.getName() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitArenaEvent event) {
    if (event.getReason() == KickReason.Lose || event.getReason() == KickReason.Draw) {
      return;
    }
    final Arena arena = event.getArena();
    sendDataToSocket("quit:" + arena.getName() + ":" + event.getPlayer().getName() + ":" + arena.getPlayers().size() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
    Util.connect(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuitSpec(final PlayerQuitArenaSpectatorEvent event) {
    if (event.getArena().getPlayers().contains(event.getPlayer())) return;
    Util.connect(event.getPlayer());
  }

  @EventHandler
  public void onArenaUpdate(final ArenaStatusUpdateEvent event) {
    final Arena arena = event.getArena();
    sendDataToSocket("update:" + arena.getName() + ":" + event.getStatus().name() + ":" + arena.getPlayers().size() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
  }

  @EventHandler
  public void onPlayerExplode(final EntityDamageEvent event) {
    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
      event.setDamage(event.getFinalDamage() / 2);
    }
  }

  @EventHandler
  public void onCommand(final ServerCommandEvent event) {
    System.out.println(event.getCommand());
  }
}