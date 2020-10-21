package com.grassminevn.bwaddon.rank;

import com.grassminevn.bwaddon.BedwarsAddon;
import com.grassminevn.levels.LevelsAPI;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.jskills.ITeam;
import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaRankHandler {
    private static final Map<Team, List<Player>> playerList = new EnumMap<>(Team.class);
    private static final Collection<Team> eliminateList = new ArrayList<>();
    private static final HashMap<UUID, Integer> topKillList = new HashMap<>();

    public static void handleArenaStart(final Arena arena) {
        clearData();
        for (final Team team : arena.GetTeamColors().GetEnabledTeams()) {
            playerList.put(team, arena.getPlayersInTeam(team));
        }
    }

    public static void handleTeamEliminate(final Team team) {
        eliminateList.add(team);
    }

    public static void handlePlayerKill(final Entity killer) {
        topKillList.put(killer.getUniqueId(), topKillList.getOrDefault(killer.getUniqueId(), 0) + 1);
    }

    public static void handleArenaEnd() {
        Bukkit.getScheduler().runTaskAsynchronously(BedwarsAddon.getInstance(), () -> {
            final List<ITeam> teamList = new ArrayList<>();
            for (final Team team : eliminateList) {
                final ITeam playerTeam = new com.grassminevn.levels.jskills.Team();
                for (final Player player : playerList.get(team)) {
                    final PlayerConnect playerConnect = LevelsAPI.getPlayerConnect(player.getUniqueId());
                    playerTeam.put(playerConnect, playerConnect.getRating());
                }
                teamList.add(playerTeam);
            }
            final int[] pos = new int[eliminateList.size()];
            for (int i = 1; i <= eliminateList.size(); i++) {
                pos[i - 1] = i;
            }
            LevelsAPI.calculateRatings(teamList, pos);
            clearData();
        });
    }

    private static void clearData() {
        playerList.clear();
        eliminateList.clear();
        topKillList.clear();
    }
}
