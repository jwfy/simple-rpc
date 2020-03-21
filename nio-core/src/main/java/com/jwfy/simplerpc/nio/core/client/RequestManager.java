package com.jwfy.simplerpc.nio.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.protocol.RpcRequest;
import com.jwfy.simplerpc.nio.core.protocol.RpcResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author jwfy
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
        // 单例，使用嵌套类，既能保证线程安全，也不影响性能
        return Single.INSTANCE;
    }

    /**
     * 客户端发送请求，先返回一个future
     * @param request
     * @return
     */
    public RpcResponseFuture send(Channel channel, RpcRequest request, boolean hasResult) {
        logger.debug("请求开始发送:{}", request);
        channel.writeAndFlush(request)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        logger.debug("请求发送成功:");
                    }
                });
        RpcResponseFuture future = new RpcResponseFuture(hasResult);
        responseMap.put(request.getRequestId(), future);
        return future;
    }

    public void setResponse(RpcResponse response) throws InterruptedException {
        RpcResponseFuture future = responseMap.remove(response.getRequestId());
        future.setResponse(response);
        logger.debug("收到结果 :{}", response);
    }
}
