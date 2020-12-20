package com.grassminevn.bwaddon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.grassminevn.levels.LevelsAPI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
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
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(final SocketChannel channel) {
                    channel.pipeline().addLast(new StringEncoder());
                }
            })
            .connect("127.0.0.1", 2);

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