package com.jwfy.simplerpc.v2.service;

import com.jwfy.simplerpc.v2.protocol.RpcDecoder;
import com.jwfy.simplerpc.v2.protocol.RpcEncoder;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author jwfy
 */
public class ServiceConnection {

    private int port;

    /**
     * 请求处理器
     */
    private ServiceHandler serviceHandler;

    private NioEventLoopGroup boss;

    private NioEventLoopGroup work;

    private ServerBootstrap serverBootstrap;

    private SerializeProtocol serializeProtocol;

    public void init(int port, SerializeProtocol serializeProtocol, ServiceHandler serviceHandler) {
        this.port = port;
        this.serviceHandler = serviceHandler;
        this.serializeProtocol = serializeProtocol;

        this.boss = new NioEventLoopGroup(1);
        this.work = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcDecoder(RpcRequest.class, serializeProtocol))
                                .addLast(new RpcEncoder(RpcResponse.class, serializeProtocol))
                                .addLast(serviceHandler);
                    }
                });
    }

    public void start() {
        try {
            ChannelFuture channelFuture = this.serverBootstrap.bind(port).sync();
            System.out.println("服务启动了");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            close();
        }
    }

    public void close() {
        if (boss != null) {
            boss.shutdownGracefully();
        }
        if (work != null) {
            work.shutdownGracefully();
        }
        System.out.println("服务端关闭了");
    }
}
