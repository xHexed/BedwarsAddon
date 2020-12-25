package com.grassminevn.bwaddon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.bukkit.plugin.Plugin;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyClient {
    private static final SocketAddress address = new InetSocketAddress(2);

    private final Plugin plugin;
    private NioEventLoopGroup group;
    private final Bootstrap bootstrapper;
    private Channel channel;

    public NettyClient(final Plugin plugin) {
        this.plugin = plugin;
        bootstrapper = new Bootstrap();
    }

    public void start() {
        group = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat(plugin.getName() + " Server IO #%d").setDaemon(true).build());
        bootstrapper.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel channel) {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                });
        connect();
    }

    public void stop() {
        Util.ASYNC_SCHEDULER_EXECUTER.execute(() -> group.shutdownGracefully().syncUninterruptibly());
    }

    private void connect() {
        channel = bootstrapper.connect(address).syncUninterruptibly().channel();
        channel.closeFuture().addListener((ChannelFutureListener) future -> plugin.getLogger().info("Netty client has disconnected."));
    }

    public void sendMessage(final Object obj) {
        if (channel.isActive()) {
            channel.writeAndFlush(obj);
        }
        else {
            connect();
            channel.writeAndFlush(obj).syncUninterruptibly();
        }
    }
}
