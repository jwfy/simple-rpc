package com.jwfy.simplerpc.v2.client;


import com.jwfy.simplerpc.v2.config.ClientConfig;
import com.jwfy.simplerpc.v2.register.RegisterConfig;
import com.jwfy.simplerpc.v2.register.ServiceDiscovery;
import com.jwfy.simplerpc.v2.register.ZkServiceDiscovery;
import com.jwfy.simplerpc.v2.serialize.HessianSerialize;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
     * netty handler处理器
     */
    private ClientHandler clientHandler = new ClientHandler();

    public RpcClient() {
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

    private void discovery() {
        // 服务注册，在网络监听启动之前就需要完成
        for(String interfaceName : clientConfigMap.keySet()) {
            this.serviceDiscovery.discovery(interfaceName);
        }
    }

    public void start() {
        this.discovery();

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            RpcClient.this.close();
        }));
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public RegisterConfig getRegisterConfig() {
        return registerConfig;
    }

    public SerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) (clientConfigMap.get(clazz.getName()).getProxy());
    }

    public void close() {
        this.clientConfigMap.clear();
//        this.clientConnection.close();
        // this.serviceDiscovery.
        logger.error("客户端关闭了");
    }

}
