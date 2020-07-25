package com.grassminevn.bwaddon;

import de.marcely.bedwars.api.*;
import de.marcely.bedwars.api.event.*;
import de.marcely.bedwars.dD;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.grassminevn.bwaddon.Util.sendDataToSocket;

public class EventListener implements Listener {
  @EventHandler
  public void arenaDebug(final PlayerJoinArenaEvent event) {
    if (event.getFailReason() == null) return;
    System.out.println(event.getPlayer() + ":" + event.getFailReason().name());
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

  @EventHandler(ignoreCancelled = true)
  public void onShopBuy(final ShopBuyEvent event) {
    if (event.getProblems().isEmpty() || !event.isTakingPayments() || !event.isGivingProducts()) return;
    for (final ShopBuyEvent.ShopBuyProblem problem : event.getProblems()) {
      if (problem.equals(ShopBuyEvent.ShopBuyProblem.DEFAULT_NOT_ENOUGH_ITEMS)) {
        if (event.getShopItem().getIcon().getType().name().contains("SWORD")) {
          event.getBuyer().getInventory().remove(Material.WOOD_SWORD);
          return;
        }
      }
    }
  }

  @EventHandler
  public void onItemDamage(final PlayerItemDamageEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerPickup(final EntityPickupItemEvent event) {
    if (!(event.getEntity() instanceof  Player)) return;
    final List<Entity> nearby = event.getEntity().getNearbyEntities(1, 0.5, 1);
    if (!nearby.isEmpty()) return;

    event.setCancelled(ThreadLocalRandom.current().nextBoolean());
  }
}