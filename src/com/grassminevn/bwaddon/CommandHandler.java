package com.grassminevn.bwaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1 || (sender instanceof Player && !sender.hasPermission("admin"))) return false;
        switch (args[0].toLowerCase()) {
            case "reload":
                BedwarsAddon.reloadSettings();
                sender.sendMessage("Reload xong");
                return true;
            case "send":
                if (args.length < 2) return false;
                Util.sendDataToSocket(args[1]);
                return true;
            case "debug":
                if (args.length < 2) return false;
                if (BedwarsAddon.debug.contains(args[1])) {
                    BedwarsAddon.debug.remove(args[1]);
                    sender.sendMessage("Removed " + args[1]);
                }
                else {
                    BedwarsAddon.debug.add(args[1]);
                    sender.sendMessage("Added " + args[1]);
                }
                BedwarsAddon.saveDebug();
                return true;
            default:
                return true;
        }
    }
}