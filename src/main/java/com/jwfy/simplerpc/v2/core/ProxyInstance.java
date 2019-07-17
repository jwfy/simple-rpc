package com.jwfy.simplerpc.v2.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author jwfy
 */
public class ProxyInstance implements InvocationHandler {

    private RpcClient rpcClient;

    private Class clazz;

    public ProxyInstance(RpcClient client, Class clazz) {
        this.rpcClient = client;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(args);

        // 获取服务提供方信息
        InetSocketAddress address = rpcClient.discovery(request);
        System.out.println("[" + Thread.currentThread().getName() + "]discover service:" + address);

        // 发起网络请求，得到请求数据
        RpcResponse response = rpcClient.invoke(request, address);
        return response.getResult();
    }
}
