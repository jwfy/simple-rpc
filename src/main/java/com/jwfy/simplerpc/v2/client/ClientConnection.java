package com.jwfy.simplerpc.v2.client;

import com.jwfy.simplerpc.v2.protocol.RpcDecoder;
import com.jwfy.simplerpc.v2.protocol.RpcEncoder;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import com.jwfy.simplerpc.v2.util.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * 在链接操作中，需要持有和管理clienthandler
 *
 * @author jwfy
 */
public class ClientConnection  {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private RpcClient rpcClient;

    private ClientHandler clientHandler;

    public ClientConnection(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        this.clientHandler = rpcClient.getClientHandler();
    }

    /**
     *
     * @param interfaceName
     * @param ip
     * @param sync  为true就是阻塞处理，否则就是异步处理
     */
    public void connection(String interfaceName, String ip, boolean sync) {
        Channel channel = clientHandler.getChannel(ip);
        if (channel != null) {
            logger.warn("已经连接好了, IP:{}, interface:{}, channel:{}", ip, interfaceName, channel);
            return;
        }
        InetSocketAddress address = CommonUtils.parseIp(ip);

        SerializeProtocol serializeProtocol = rpcClient.getSerializeProtocol();

        EventLoopGroup work = new NioEventLoopGroup(8);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
                                .addLast(new RpcEncoder(RpcRequest.class, serializeProtocol))
                                .addLast(new RpcDecoder(RpcResponse.class, serializeProtocol))
                                .addLast(clientHandler);
                    }
                });

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ChannelFuture channelFuture = bootstrap.connect(address);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    clientHandler.addChannel(channelFuture.channel(), interfaceName, ip);
                    countDownLatch.countDown();
                }
            }

        });
        if (sync) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            countDownLatch.countDown();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(String interfaceName, String ip) {
        this.clientHandler.removeChannel(interfaceName, ip);
    }

}
