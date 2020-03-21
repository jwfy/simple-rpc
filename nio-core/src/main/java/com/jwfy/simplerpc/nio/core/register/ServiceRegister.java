package com.jwfy.simplerpc.nio.core.register;

import java.util.List;

import com.jwfy.simplerpc.nio.core.config.ServiceConfig;

/**
 * @author jwfy
 */
public interface ServiceRegister {

    /**
     * 批量服务注册
     * @param configList
     */
    void registerList(List<ServiceConfig<?>> configList);

    /**
     * 服务注册
     * @param config
     */
    void register(ServiceConfig<?> config);

    /**
     * 优雅关闭
     */
    void close();
}
