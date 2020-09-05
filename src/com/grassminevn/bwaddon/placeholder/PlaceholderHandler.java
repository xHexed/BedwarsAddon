package com.grassminevn.bwaddon.placeholder;

import com.grassminevn.bwaddon.phase.ArenaPhaseHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderHandler extends PlaceholderExpansion {
    private static final Map<String, IdentifierHandler> identifierHandlers = new HashMap<>();

    static {
        identifierHandlers.put("phasetime", (player, params) -> String.valueOf(ArenaPhaseHandler.getCounter().getNextMoment()));
        identifierHandlers.put("phasename", (player, params) -> ArenaPhaseHandler.getCounter().getCurrentMoment().name);
    }

    @Override
    public String getIdentifier() {
        return "bwa";
    }

    @Override
    public String getAuthor() {
        return "xHexed";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(final OfflinePlayer player, final String identifier) {
        if (player == null || identifier.isEmpty()) return "";
        final String[] params = identifier.split("_");
        if (identifierHandlers.containsKey(params[0].toLowerCase())) {
            return identifierHandlers.get(params[0]).handle(player, params);
        }
        else {
            return "";
        }
    }
}
