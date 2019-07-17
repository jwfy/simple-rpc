package com.jwfy.simplerpc.v2.register;


import com.jwfy.simplerpc.v2.config.BasicConfig;
import com.jwfy.simplerpc.v2.core.RpcRequest;
import com.jwfy.simplerpc.v2.domain.ServiceType;

import java.net.InetSocketAddress;

/**
 * @author jwfy
 */
public interface ServiceRegister {

    /**
     * 服务注册
     * @param config
     */
    void register(BasicConfig config);

    /**
     * 服务发现，从注册中心获取可用的服务提供方配置信息
     * @param request
     * @return
     */
    InetSocketAddress discovery(RpcRequest request, ServiceType nodeType);
}
