package com.jwfy.simplerpc.v2.service;


import com.jwfy.simplerpc.v2.config.ServiceConfig;
import com.jwfy.simplerpc.v2.register.RegisterConfig;
import com.jwfy.simplerpc.v2.register.ServiceRegister;
import com.jwfy.simplerpc.v2.register.ZkServiceRegister;
import com.jwfy.simplerpc.v2.serialize.HessianSerialize;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jwfy
 */
public class RpcService {

    private static final Logger logge = LoggerFactory.getLogger(RpcService.class);

    private int port;

    /**
     * k 是接口的全名称
     * v 是对应的对象包含的详细信息
     */
    private Map<String, ServiceConfig> serviceConfigMap;

    /**
     * 服务注册器
     */
    private ServiceRegister serviceRegister;

    /**
     * 服务连接器
     */
    private ServiceConnection serviceConnection;

    /**
     * 序列号工具
     */
    private SerializeProtocol serializeProtocol;

    private RpcInvoke rpcInvoke;

    public RpcService(int port) {
        // 优雅关闭注册，必须放在最前面
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            RpcService.this.close();
        }));
        this.port = port;
        this.serviceConfigMap = new HashMap<>(64);
        this.serviceRegister = new ZkServiceRegister(new RegisterConfig());
        this.serializeProtocol = new HessianSerialize();
        this.serviceConnection = new ServiceConnection(this);
        this.rpcInvoke = new RpcInvoke(this);
    }

    public SerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public <T> void addService(Class<T> interfaceClass, T ref) {
        String interfaceName = interfaceClass.getName();
        ServiceConfig<T> serviceConfig = ServiceConfig.convert(interfaceName, interfaceClass, ref, this);
        serviceConfigMap.put(interfaceName, serviceConfig);
    }

    public void start() {
        List<ServiceConfig> serviceConfigList = new ArrayList<>(serviceConfigMap.values());
        if (serviceConfigList.isEmpty()) {
            throw new IllegalArgumentException("未注册有效服务");
        }
        // 服务连接&注册
        this.serviceConnection.start(serviceConfigList);
    }

    public int getPort() {
        return this.port;
    }

    public ServiceConfig getServiceConfig(String key) {
        return this.serviceConfigMap.get(key);
    }

    public ServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    public RpcInvoke getRpcInvoke() {
        return rpcInvoke;
    }

    public void close() {
        this.serviceConfigMap.clear();
        this.serviceRegister.close();
        this.serviceConnection.close();
        logge.warn("服务端关闭了");
    }

}
