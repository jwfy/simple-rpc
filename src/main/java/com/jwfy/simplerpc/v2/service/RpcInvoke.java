package com.jwfy.simplerpc.v2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
 * @author junhong
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
//                RpcRequest request = convert(requestStr);

                RpcResponse response = invoke(request);
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

    private RpcRequest convert(String requestStr) {
        RpcRequest request = new RpcRequest();

        JSONObject jsonObject = JSON.parseObject(requestStr);
        request.setRequestId(jsonObject.getString("requestId"));
        request.setClassName(jsonObject.getString("className"));
        request.setMethodName(jsonObject.getString("methodName"));

        JSONArray arguments = jsonObject.getJSONArray("arguments");
        Object[] argumentObj = new Object[arguments.size()];

        for(int i=0; i< arguments.size(); i++) {
            argumentObj[i] = arguments.get(i);
        }
        request.setArguments(argumentObj);
        request.setParameterTypes(new Class[]{Object.class, Object.class});

        // TODO: 2019-11-06 序列化存在问题
        return request;
    }

}
