package com.grassminevn.bwaddon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grassminevn.levels.LevelsAPI;
import me.MathiasMC.PvPLevels.api.PvPLevelsAPI;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Util {
    static String lobby;
    public static final ScheduledExecutorService ASYNC_SCHEDULER_EXECUTER = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(BedwarsAddon.getInstance().getName() + "-scheduler-%d").setDaemon(true).build());

    public static void sendDataToSocket(final String data) {
        ASYNC_SCHEDULER_EXECUTER.execute(() -> BedwarsAddon.getInstance().client.sendMessage(data));
    }

    public static void connect(final Player player) {
        ASYNC_SCHEDULER_EXECUTER.execute(() -> {
            PvPLevelsAPI.syncSave(player.getUniqueId().toString());
            LevelsAPI.syncSave(player.getUniqueId());
            try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 final DataOutputStream out = new DataOutputStream(bytes)){
                out.writeUTF("Connect");
                out.writeUTF(lobby);
                player.sendPluginMessage(BedwarsAddon.getInstance(), "BungeeCord", bytes.toByteArray());
                out.flush();
                bytes.flush();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}