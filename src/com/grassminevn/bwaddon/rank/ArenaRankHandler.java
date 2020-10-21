package com.grassminevn.bwaddon.rank;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.Team;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaRankHandler {
    private static final Map<Team, List<Player>> playerList = new EnumMap<>(Team.class);
    private static final Collection<Team> eliminateList = new ArrayList<>();
    private static final HashMap<UUID, Integer> topKillList = new HashMap<>();

    public static void handleArenaStart(final Arena arena) {
        playerList.clear();
        eliminateList.clear();
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

    public static void handleArenaEnd(final Arena arena) {

    }
}
