package com.grassminevn.bwaddon;

import com.grassminevn.levels.LevelsAPI;
import me.MathiasMC.PvPLevels.PvPLevelsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Util {
    static String lobby;

    public static void sendDataToSocket(final String data) {
        try {
            final Socket client = new Socket(InetAddress.getLocalHost(), 2);
            final DataOutputStream ds = new DataOutputStream(client.getOutputStream());
            ds.writeUTF(data);
            ds.close();
            client.close();
        }
        catch (final IOException e) {
            BedwarsAddon.getInstance().getLogger().warning("Error on sending data: " + e.toString());
        }
    }

    public static void connect(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(BedwarsAddon.getInstance(), () -> {
            PvPLevelsAPI.syncSave(player.getUniqueId().toString());
            LevelsAPI.syncSave(player.getUniqueId());
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(bytes);
            try {
                out.writeUTF("Connect");
                out.writeUTF(lobby);
                player.sendPluginMessage(BedwarsAddon.getInstance(), "BungeeCord", bytes.toByteArray());
                out.flush();
                bytes.flush();
                out.close();
                bytes.close();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}