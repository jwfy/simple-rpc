package com.jwfy.simplerpc.nio.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.protocol.RpcRequest;
import com.jwfy.simplerpc.nio.core.protocol.RpcResponse;
import com.jwfy.simplerpc.nio.core.util.CommonUtils;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

/**
 * @author jwfy
 */
public class ProxyInstance implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyInstance.class);

    private RpcClient rpcClient;

    private Class<?> clazz;

    public ProxyInstance(RpcClient client, Class<?> clazz) {
        this.rpcClient = client;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Set<String> socketAddressList = this.rpcClient.getSocketAddress(clazz.getName());
        String socketAddressStr = this.rpcClient.loadBalance(socketAddressList);
        InetSocketAddress socketAddress = CommonUtils.parseIp(socketAddressStr);
        if (socketAddress == null) {
            throw new RuntimeException("无有效服务提供方");
        }

        RpcRequest request = new RpcRequest();
        request.setClassName(this.clazz.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);

        boolean hasResult = !method.getReturnType().equals(Void.TYPE);
        RpcResponse<?> response = null;

        // 没有另外加filter或者配置重试次数，但是应该把超时时间加上
        Future<Channel> fc = this.rpcClient.acquireChannel(socketAddress);
        Channel channel = fc.get();

        logger.debug("获取将使用的channel:{}", channel);
        RpcResponseFuture future = this.rpcClient.send(channel, request, hasResult);
        response = future.getResponse();
        if (response.getError()) {
            logger.error(response.getErrorMessage());
            return null;
        }
        // 回收channel
        this.rpcClient.releaseChannel(channel);
        return response.getResult();
    }
}
