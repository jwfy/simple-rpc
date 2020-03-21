package com.jwfy.simplerpc.nio.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.config.ServiceConfig;
import com.jwfy.simplerpc.nio.core.protocol.RpcDecoder;
import com.jwfy.simplerpc.nio.core.protocol.RpcEncoder;
import com.jwfy.simplerpc.nio.core.protocol.RpcRequest;
import com.jwfy.simplerpc.nio.core.protocol.RpcResponse;
import com.jwfy.simplerpc.nio.core.serialize.SerializeProtocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author jwfy
 */
public class ServiceConnection {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConnection.class);

    private NioEventLoopGroup boss;

    private NioEventLoopGroup work;

    private ServerBootstrap serverBootstrap;

    private RpcService rpcService;

    public ServiceConnection(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    public void start(List<ServiceConfig<?>> serviceConfigList) {
        try {
            this.boss = new NioEventLoopGroup();
            this.work = new NioEventLoopGroup();

            SerializeProtocol serializeProtocol = rpcService.getSerializeProtocol();

            this.serverBootstrap = new ServerBootstrap();
            this.serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65530, 0, 2, 0, 2))
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new RpcDecoder<>(RpcRequest.class, serializeProtocol))
                                    .addLast(new RpcEncoder<>(RpcResponse.class, serializeProtocol))
                                    .addLast(new ServiceHandler(rpcService.getRpcInvoke()));
                        }
                    });

            ChannelFuture channelFuture = this.serverBootstrap.bind(this.rpcService.getPort()).sync();
            logger.info("服务启动了");

            // 服务注册
            this.rpcService.getServiceRegister().registerList(serviceConfigList);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        logger.warn("Netty服务端关闭了");
    }
}
