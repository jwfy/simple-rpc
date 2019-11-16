package com.jwfy.simplerpc.v2.register;

import com.jwfy.simplerpc.v2.domain.ServiceType;

/**
 * @author jwfy
 */
public class RegisterConfig {

    /**
     * 默认情况，这个是不允许变的，zk默认节点信息
     */
    private static final String zkNameSpace = "jwfy/simple-rpc";

    /**
     * zk机器信息
     */
    private String zkHost = "127.0.0.1:2182";

    /**
     * 注册的类型，目前是有服务提供方、以及服务调用方
     */
    private ServiceType serviceType;

    /**
     * zk节点session过期时间，服务断开后5秒自动关闭
     */
    private int sessionTimeOut = 5000;

    public String getZkNameSpace() {
        return zkNameSpace;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }
}
