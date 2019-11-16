package com.jwfy.simplerpc.v2.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jwfy.simplerpc.v2.config.ServiceConfig;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * @author jwfy
 */
public class RpcInvoke {

    private static final Logger logger = LoggerFactory.getLogger(RpcInvoke.class);

    private RpcService rpcService;

    private ThreadPoolExecutor executor;

    public RpcInvoke(RpcService rpcService) {
        this.rpcService = rpcService;

        ThreadFactory commonThreadName = new ThreadFactoryBuilder()
                .setNameFormat("Handler-Task-%d")
                .build();

        this.executor = new ThreadPoolExecutor(
                100,
                100,
                2,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                commonThreadName, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.warn("reject task:");
            }
        });
    }

    private <K> RpcResponse invoke(RpcRequest request) {
        String className = request.getClassName();
        ServiceConfig<K> serviceConfig =  this.rpcService.getServiceConfig(className);
        // 暂时不考虑没有对应serviceconfig的情况

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        K ref = serviceConfig.getRef();
        try {
            Method method = ref.getClass().getMethod(
                    request.getMethodName(),
                    request.getParameterTypes());

            Object result = method.invoke(ref, request.getArguments());

            response.setResult(result);
            response.setError(false);

            return response;
        } catch (Exception e) {
            logger.error("invoke error with:{}", e);
        }

        response.setError(true);
        return response;
    }

    public void invoke(ChannelHandlerContext context, RpcRequest request) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                RpcResponse response = invoke(request);
                // 默认是pipline 刷新数据，如果直接使用channel可能存在遗漏的可能性
                context.writeAndFlush(response)
                        .addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                logger.info("返回响应结果, response:" + response);
                            }
                        });
            }
        });

    }

}
