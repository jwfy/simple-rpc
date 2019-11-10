package com.jwfy.simplerpc.v2.client;

import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author junhong
 */
public class RequestManager {

    private static final Logger logger = LoggerFactory.getLogger(RequestManager.class);

    private Map<String, RpcResponseFuture> responseMap = new ConcurrentHashMap<>();

    private RequestManager() {

    }

    private static class Single {
        private static final RequestManager INSTANCE = new RequestManager();
    }

    public static final RequestManager getInstance() {
        return Single.INSTANCE;
    }

    // private Map<String, RpcResponseFuture> responseMap = new ConcurrentHashMap<>();

    /**
     * 客户端发送请求，先返回一个future
     * @param request
     * @return
     */
    public RpcResponseFuture send(Channel channel, RpcRequest request) {
        logger.info("请求开始发送:{}", request);
        channel.writeAndFlush(request)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        logger.info("请求发送成功:");
                    }
                });
        RpcResponseFuture future = new RpcResponseFuture();
        responseMap.put(request.getRequestId(), future);
        return future;
    }

    public void setResponse(RpcResponse response) throws InterruptedException {
        RpcResponseFuture future = responseMap.remove(response.getRequestId());
        future.setResponse(response);
        logger.info("收到结果 :{}", response);
    }
}
