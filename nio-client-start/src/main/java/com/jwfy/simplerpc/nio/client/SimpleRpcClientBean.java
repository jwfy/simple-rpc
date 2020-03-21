package com.jwfy.simplerpc.nio.client;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.jwfy.simplerpc.nio.core.client.RpcClient;

public class SimpleRpcClientBean implements InitializingBean, DisposableBean {

    private RpcClient rpcClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.rpcClient = new RpcClient();
    }

    @Override
    public void destroy() throws Exception {
        this.rpcClient.close();
    }

    public RpcClient getRpcClient() {
        return rpcClient;
    }
}
