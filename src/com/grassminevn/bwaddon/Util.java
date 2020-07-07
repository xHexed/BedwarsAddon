package com.grassminevn.bwaddon;

import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Util {
    static String lobby;

    public static void sendDataToSocket(final String data) {
        try {
            final Socket client = new Socket("127.0.0.1", 2);
            final DataOutputStream ds = new DataOutputStream(client.getOutputStream());
            ds.writeUTF(data);
            ds.close();
            client.close();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect(final PluginMessageRecipient player) {
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
    }
}