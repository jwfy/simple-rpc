package com.jwfy.simplerpc.nio.core.config;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import com.jwfy.simplerpc.nio.core.client.ProxyInstance;

/**
 * @author jwfy
 */
public class ClientConfig<T> extends BasicConfig implements Serializable {
    private static final long serialVersionUID = 8717863920334826099L;

    private T proxy;

    public void setProxy(T proxy) {
        this.proxy = proxy;
    }

    public T getProxy() {
        return proxy;
    }

    public static <T> ClientConfig<T> convert(Class<T> interfaceClass, ProxyInstance invocationHandler) {
        ClientConfig<T> config = new ClientConfig<>();

        config.setVersion("default");
        config.setInterfaceClass(interfaceClass);
        config.setInterfaceName(interfaceClass.getName());
        config.setMethods(MethodConfig.convert(interfaceClass.getMethods()));

        Object proxy = Proxy.newProxyInstance(ClientConfig.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                invocationHandler);
        config.setProxy((T) proxy);
        return config;
    }
}
