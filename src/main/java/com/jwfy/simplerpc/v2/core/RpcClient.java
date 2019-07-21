package com.jwfy.simplerpc.v2.core;


import com.jwfy.simplerpc.v2.config.ClientConfig;
import com.jwfy.simplerpc.v2.domain.ServiceType;
import com.jwfy.simplerpc.v2.protocol.DefaultMessageProtocol;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import com.jwfy.simplerpc.v2.register.ServiceRegister;
import com.jwfy.simplerpc.v2.register.ZkServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jwfy
 */
public class RpcClient {

    /**
     * k 是接口的全名称
     * v 是对应的对象包含的详细信息
     */
    private Map<String, ClientConfig> clientConfigMap = new HashMap<>();

    private ServiceRegister serviceRegister;

    private ClientHandler clientHandler;

    public RpcClient() {
        this.serviceRegister = new ZkServiceRegister();
        this.clientHandler = new ClientHandler(this);
        // 设置默认的消息处理协议
        this.clientHandler.setMessageProtocol(new DefaultMessageProtocol());
    }

    public <T> void subscribe(Class<T> clazz) {
        String interfaceName = clazz.getName();
        ProxyInstance invocationHandler = new ProxyInstance(this, clazz);
        ClientConfig<T> clientConfig = ClientConfig.convert(clazz, invocationHandler);
        clientConfigMap.put(interfaceName, clientConfig);
    }

    private void register() {
        // 服务注册，在网络监听启动之前就需要完成
        clientConfigMap.values().forEach(serviceRegister::register);
    }

    public void start() {
        this.register();
    }

    public InetSocketAddress discovery(RpcRequest request) {
        return serviceRegister.discovery(request, ServiceType.PROVIDER);
    }

    public RpcResponse invoke(RpcRequest request, InetSocketAddress address) {
        return this.clientHandler.invoke(request, address);
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) (clientConfigMap.get(clazz.getName()).getProxy());
    }

}
