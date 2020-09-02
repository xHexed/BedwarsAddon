package com.grassminevn.bwaddon.handler;

import org.bukkit.OfflinePlayer;

public interface IdentifierHandler {
    String handle(final OfflinePlayer player, final String[] params);
}
