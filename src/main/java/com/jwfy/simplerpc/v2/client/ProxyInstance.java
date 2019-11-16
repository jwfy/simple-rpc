package com.jwfy.simplerpc.v2.client;

import com.jwfy.simplerpc.v2.balance.DefaultLoadBalance;
import com.jwfy.simplerpc.v2.balance.LoadBalance;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author jwfy
 */
public class ProxyInstance implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyInstance.class);

    private RpcClient rpcClient;

    private ClientHandler clientHandler;

    private LoadBalance loadBalance;

    private Class clazz;

    public ProxyInstance(RpcClient client, Class clazz) {
        this.rpcClient = client;
        this.clientHandler = this.rpcClient.getClientHandler();
        this.clazz = clazz;
        this.loadBalance = new DefaultLoadBalance();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Channel> channelList = this.clientHandler.getChannels(clazz.getName());
        Channel channel = loadBalance.balance(channelList);
        if (channel == null) {
            throw new RuntimeException("无有效服务提供方");
        }
        logger.debug("获取将使用的channel:{}", channel);

        RpcRequest request = new RpcRequest();
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);

        RpcResponseFuture future = RequestManager.getInstance().send(channel, request);
        RpcResponse response = future.getResponse();
        if (response.getError()) {
            throw new RuntimeException(response.getErrorMessage());
        }
        return response.getResult();
    }
}
