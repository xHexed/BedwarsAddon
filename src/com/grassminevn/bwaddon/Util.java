package com.grassminevn.bwaddon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grassminevn.levels.LevelsAPI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.MathiasMC.PvPLevels.api.PvPLevelsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Util {
    static String lobby;
    static final ChannelFuture client = new Bootstrap()
            .group(new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("BedwarsHub Server IO #%d").setDaemon(true).build()))
            .channel(NioSocketChannel.class)
            .connect("127.0.0.1", 2).syncUninterruptibly();

    public static void sendDataToSocket(final String data) {
        client.channel().writeAndFlush(data);
    }

    public static void connect(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(BedwarsAddon.getInstance(), () -> {
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