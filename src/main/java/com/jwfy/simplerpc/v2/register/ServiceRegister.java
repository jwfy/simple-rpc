package com.jwfy.simplerpc.v2.register;


import com.jwfy.simplerpc.v2.config.ServiceConfig;

import java.util.List;

/**
 * @author jwfy
 */
public interface ServiceRegister {

    /**
     * 批量服务注册
     * @param configList
     */
    void registerList(List<ServiceConfig> configList);

    /**
     * 服务注册
     * @param config
     */
    void register(ServiceConfig config);

    /**
     * 优雅关闭
     */
    void close();
}
