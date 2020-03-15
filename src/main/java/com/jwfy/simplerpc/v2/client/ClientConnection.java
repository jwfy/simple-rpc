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
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在链接操作中，需要持有和管理clienthandler
 *
 * @author jwfy
 */
public class ClientConnection  {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private ChannelPoolMap<InetSocketAddress, SimpleChannelPool> channelPool;

    private RpcClient rpcClient;

    private ReentrantLock lock;

    private Set<InetSocketAddress> addressSet;

    private ThreadLocal<SimpleChannelPool> poolThreadLocal = new ThreadLocal<>();

    public ClientConnection(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        this.lock = new ReentrantLock();
        this.addressSet = new HashSet<>();

        EventLoopGroup work = new NioEventLoopGroup(8);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        channelPool = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(
                        bootstrap.remoteAddress(key),
                        new CustomChannelPoolHandler(),
                        20);
            }
        };
    }

    public Future<Channel> acquire(InetSocketAddress socketAddress) {
        lock.lock();
        try {
            addressSet.add(socketAddress);
        } finally {
            lock.unlock();
        }
        SimpleChannelPool pool = channelPool.get(socketAddress);
        logger.debug("acquire pool with {}", socketAddress);
        poolThreadLocal.set(pool);
        return pool.acquire();
    }

    public void release(Channel channel) {
        logger.debug("release pool channel with {}", channel);
        SimpleChannelPool pool = poolThreadLocal.get();
        pool.release(channel);
        poolThreadLocal.remove();
    }

    public void close() {
        Iterator<InetSocketAddress> iterator = addressSet.iterator();
        while (iterator.hasNext()) {
            InetSocketAddress socketAddress = iterator.next();
            SimpleChannelPool pool = channelPool.get(socketAddress);
            pool.close();
        }
    }

    class CustomChannelPoolHandler implements ChannelPoolHandler {

        @Override
        public void channelReleased(Channel ch) throws Exception {
            logger.debug("channelReleased channel:{}", ch);
        }

        @Override
        public void channelAcquired(Channel ch) throws Exception {
            logger.debug("channelAcquired channel:{}", ch);
        }

        @Override
        public void channelCreated(Channel ch) throws Exception {
            logger.info("channelCreated channel:{}", ch);

            SerializeProtocol serializeProtocol = rpcClient.getSerializeProtocol();
            SocketChannel channel = (SocketChannel) ch;
            channel.config().setKeepAlive(true);
            channel.config().setTcpNoDelay(true);
            channel.pipeline()
                    .addLast(new LengthFieldBasedFrameDecoder(2048, 0, 2, 0, 2))
                    .addLast(new LengthFieldPrepender(2))
                    .addLast(new RpcEncoder<>(RpcRequest.class, serializeProtocol))
                    .addLast(new RpcDecoder<>(RpcResponse.class, serializeProtocol))
                    .addLast(new ClientHandler());
        }
    }

}
