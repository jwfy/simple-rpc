package com.jwfy.simplerpc.v2.client;


import com.jwfy.simplerpc.v2.config.ClientConfig;
import com.jwfy.simplerpc.v2.register.RegisterConfig;
import com.jwfy.simplerpc.v2.register.ServiceDiscovery;
import com.jwfy.simplerpc.v2.register.ZkServiceDiscovery;
import com.jwfy.simplerpc.v2.serialize.HessianSerialize;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

/**
 * @author jwfy
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    /**
     * k 是接口的全名称
     * v 是对应的对象包含的详细信息
     */
    private Map<String, ClientConfig> clientConfigMap = new HashMap<>();

    private ServiceDiscovery serviceDiscovery;

    private ClientConnection clientConnection;

    private RegisterConfig registerConfig;

    private SerializeProtocol serializeProtocol = new HessianSerialize();

    /**
     * 在zk中存储着服务端暴露出来的接口的ip地址等信息
     */
    private Map<String, Set<String>> socketAddressMap = new HashMap<>();

    public RpcClient() {
        // 优雅关闭注册，必须放在最前面
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            RpcClient.this.close();
        }));
        this.registerConfig = new RegisterConfig();
        this.serviceDiscovery = new ZkServiceDiscovery(this);
        this.clientConnection = new ClientConnection(this);
    }

    public <T> void subscribe(Class<T> clazz) {
        String interfaceName = clazz.getName();
        ProxyInstance invocationHandler = new ProxyInstance(this, clazz);
        ClientConfig<T> clientConfig = ClientConfig.convert(clazz, invocationHandler);
        clientConfigMap.put(interfaceName, clientConfig);
    }

    public void start() {
        // 服务注册，仅完成了从zk获取对应的ip地址信息以及相关的监听工作
        for(String interfaceName : clientConfigMap.keySet()) {
            Set<String> inetSocketAddresses = new HashSet<>();
            socketAddressMap.put(interfaceName, inetSocketAddresses);
            this.serviceDiscovery.discovery(interfaceName, inetSocketAddresses);
        }
    }

    public RegisterConfig getRegisterConfig() {
        return registerConfig;
    }

    public SerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) (clientConfigMap.get(clazz.getName()).getProxy());
    }

    public Set<String> getSocketAddress(String interfaceName) {
        return this.socketAddressMap.get(interfaceName);
    }

    /**
     * 默认最多花1s时间获取到有效的channel
     * @param socketAddress
     * @return
     */
    public Future<Channel> acquireChannel(InetSocketAddress socketAddress) {
        return this.clientConnection.acquire(socketAddress);
    }

    public void releaseChannel(Channel channel) {
        this.clientConnection.release(channel);
    }

    public void close() {
        this.clientConfigMap.clear();
        this.clientConnection.close();
        logger.error("客户端关闭了");
    }

}
