package com.grassminevn.bwaddon;

import com.grassminevn.bwaddon.phase.ArenaPhaseHandler;
import com.grassminevn.bwaddon.rank.ArenaRankHandler;
import de.marcely.bedwars.api.*;
import de.marcely.bedwars.api.event.*;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

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
    final de.marcely.bedwars.game.arena.Arena arena = (de.marcely.bedwars.game.arena.Arena) BedwarsAPI.getArenas().get(0);
    if (arena.GetStatus().equals(ArenaStatus.Running)) {
      BedwarsAPI.enterSpectatorMode(player, arena, SpectateReason.PLUGIN);
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
    switch (event.getStatus()) {
      case Running:
        ArenaPhaseHandler.startArena();
        ArenaRankHandler.handleArenaStart(arena);
        break;
      case EndLobby:
        ArenaPhaseHandler.endArena();
        ArenaRankHandler.handleArenaEnd();
        break;
    }
    sendDataToSocket("update:" + arena.getName() + ":" + event.getStatus().name() + ":" + arena.getPlayers().size() + ":" + arena.getAuthor() + ":" + arena.getMaxPlayers());
  }

  @EventHandler(ignoreCancelled = true)
  public void onShopBuy(final ShopBuyEvent event) {
    if (event.getProblems().contains(ShopBuyEvent.ShopBuyProblem.DEFAULT_NOT_ENOUGH_ITEMS))
      return;
    if (event.getShopItem().getIcon().getType().name().contains("SWORD")) {
      event.getBuyer().getInventory().remove(Material.WOOD_SWORD);
    }
  }

  @EventHandler
  public void onPlayerExplode(final EntityDamageEvent event) {
    if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
      if (event.getFinalDamage() <= 5)
        event.setDamage(event.getDamage() * 8 / 10);
      else if (event.getFinalDamage() <= 10)
        event.setDamage(event.getDamage() * 7 / 10);
      else if (event.getFinalDamage() <= 15)
        event.setDamage(event.getDamage() * 6 / 10);
      else
        event.setDamage(event.getDamage() * 5 / 10);
    }
  }

  @EventHandler
  public void onItemDamage(final PlayerItemDamageEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDamage(final EntityDamageByEntityEvent event) {
    final Entity damager = event.getDamager();
    final Entity victim = event.getEntity();
    if (!(damager instanceof Player) || !(victim instanceof Player)) return;
    if (isCritical((LivingEntity) damager)) {
      event.setDamage(event.getDamage() * 8 / 10);
    }
  }

  private boolean isCritical(final LivingEntity player) {
    return player.getFallDistance() > 0.0F &&
            !player.isOnGround() &&
            !player.isInsideVehicle() &&
            !player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
            player.getLocation().getBlock().getType() != Material.LADDER &&
            player.getLocation().getBlock().getType() != Material.VINE;
  }

  @EventHandler
  public void onTeamEliminate(final TeamEliminateEvent event) {
    ArenaRankHandler.handleTeamEliminate(event.getTeam());
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event) {
    final Player killer = event.getEntity().getKiller();
    if (killer != null) ArenaRankHandler.handlePlayerKill(killer);
  }
}