package com.grassminevn.bwaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1 || sender.hasPermission("admin")) return false;
        switch (args[0].toLowerCase()) {
            case "reload":
                BedwarsAddon.getInstance().reloadConfig();
                Util.lobby = BedwarsAddon.getInstance().getConfig().getString("lobby");
                sender.sendMessage("Reload xong");
                return true;
            case "send":
                if (args.length < 2) return false;
                Util.sendDataToSocket(args[1]);
            default:
                return true;
        }
    }
}