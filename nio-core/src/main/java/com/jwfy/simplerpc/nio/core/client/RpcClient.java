package com.jwfy.simplerpc.nio.core.client;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.balance.DefaultLoadBalance;
import com.jwfy.simplerpc.nio.core.balance.LoadBalance;
import com.jwfy.simplerpc.nio.core.config.ClientConfig;
import com.jwfy.simplerpc.nio.core.register.RegisterConfig;
import com.jwfy.simplerpc.nio.core.register.ServiceDiscovery;
import com.jwfy.simplerpc.nio.core.register.ZkServiceDiscovery;
import com.jwfy.simplerpc.nio.core.serialize.HessianSerialize;
import com.jwfy.simplerpc.nio.core.serialize.SerializeProtocol;

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
    private Map<String, ClientConfig<?>> clientConfigMap = new ConcurrentHashMap<>();

    private ServiceDiscovery serviceDiscovery;

    private ClientConnection clientConnection;

    private RegisterConfig registerConfig;

    private SerializeProtocol serializeProtocol;

    private LoadBalance loadBalance;

    /**
     * 在zk中存储着服务端暴露出来的接口的ip地址等信息
     */
    private Map<String, Set<String>> socketAddressMap = new ConcurrentHashMap<>();

    public RpcClient() {
        this.registerConfig = new RegisterConfig();
        this.serviceDiscovery = new ZkServiceDiscovery(this);
        this.clientConnection = new ClientConnection(this);
        this.loadBalance = new DefaultLoadBalance();
        this.serializeProtocol = new HessianSerialize();
    }

    /**
     * 需要考虑并发安全，故采用double-check的模式
     * @param clazz
     * @param <T>
     */
    public <T> void subscribe(Class<T> clazz) {
        String interfaceName = clazz.getName();
        if (clientConfigMap.get(interfaceName) == null) {
            synchronized (RpcClient.class) {
                if (clientConfigMap.get(interfaceName) == null) {
                    ProxyInstance invocationHandler = new ProxyInstance(this, clazz);
                    ClientConfig<T> clientConfig = ClientConfig.convert(clazz, invocationHandler);
                    clientConfigMap.put(interfaceName, clientConfig);
                }
            }
        }
    }

    public void start() {
        // 服务注册，仅完成了从zk获取对应的ip地址信息以及相关的监听工作
        for(String interfaceName : clientConfigMap.keySet()) {
            Set<String> inetSocketAddresses = new HashSet<>();
            socketAddressMap.put(interfaceName, inetSocketAddresses);
            this.serviceDiscovery.discovery(interfaceName, inetSocketAddresses);
        }
    }

    public <T> void subscribe0(Class<T> clazz) {
        String interfaceName = clazz.getName();
        if (clientConfigMap.get(interfaceName) == null) {
            synchronized (RpcClient.class) {
                if (clientConfigMap.get(interfaceName) == null) {
                    ProxyInstance invocationHandler = new ProxyInstance(this, clazz);
                    ClientConfig<T> clientConfig = ClientConfig.convert(clazz, invocationHandler);
                    clientConfigMap.put(interfaceName, clientConfig);

                    Set<String> inetSocketAddresses = new HashSet<>();
                    socketAddressMap.put(interfaceName, inetSocketAddresses);
                    this.serviceDiscovery.discovery(interfaceName, inetSocketAddresses);
                }
            }
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

    public String loadBalance(Set<String> socketAddressList) {
        if (this.loadBalance == null) {
            this.loadBalance = new DefaultLoadBalance();
        }
        return this.loadBalance.balance(socketAddressList);
    }

    /**
     * 可添加自定义的负载均衡器
     * @param loadBalance
     */
    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setSerializeProtocol(SerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
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
