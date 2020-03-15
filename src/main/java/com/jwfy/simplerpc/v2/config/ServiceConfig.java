package com.jwfy.simplerpc.v2.config;


import com.alibaba.fastjson.JSON;
import com.jwfy.simplerpc.v2.service.RpcService;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jwfy
 */
public class ServiceConfig<T> extends BasicConfig implements Serializable {

    private static final long serialVersionUID = -6425436155037464981L;

    private T ref;

    /**
     * 统计调用次数使用
     */
    private int count;

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static <T> ServiceConfig<T> convert(String interfaceName,
                                               Class<T> interfaceClass,
                                               T ref, RpcService rpcService) {
        ServiceConfig<T> serviceConfig = new ServiceConfig<>();

        serviceConfig.setRef(ref);
        serviceConfig.setInterfaceName(interfaceName);
        serviceConfig.setInterfaceClass(interfaceClass);
        serviceConfig.setCount(0);
        serviceConfig.setMethods(MethodConfig.convert(interfaceClass.getMethods()));
        serviceConfig.setPort(rpcService.getPort());

        try {
            InetAddress addr = InetAddress.getLocalHost();
            serviceConfig.setHost(addr.getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return serviceConfig;
    }
}
