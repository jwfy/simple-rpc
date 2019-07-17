package com.jwfy.simplerpc.v2.core;


import com.jwfy.simplerpc.v2.config.ServiceConfig;
import com.jwfy.simplerpc.v2.protocol.DefaultMessageProtocol;
import com.jwfy.simplerpc.v2.protocol.MessageProtocol;
import com.jwfy.simplerpc.v2.register.ServiceRegister;
import com.jwfy.simplerpc.v2.register.ZkServiceRegister;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jwfy
 */
public class RpcService {

    /**
     * k 是接口的全名称
     * v 是对应的对象包含的详细信息
     */
    private Map<String, ServiceConfig> serviceConfigMap = new HashMap<>();

    private int port;

    private ServiceRegister serviceRegister;

    /**
     * 连接器还未抽象处理，使用的还是bio的io模型
     */
    private ServiceConnection serviceConnection;

    private ServiceHandler serviceHandler;

    public RpcService(int port) {
        this.port = port;
        this.serviceHandler = new ServiceHandler(this);
        this.serviceHandler.setMessageProtocol(new DefaultMessageProtocol());

        this.serviceRegister = new ZkServiceRegister();
    }

    public void setMessageProtocol(MessageProtocol messageProtocol) {
        if (this.serviceHandler == null) {
            throw new RuntimeException("套接字处理器无效");
        }
        this.serviceHandler.setMessageProtocol(messageProtocol);
    }

    public <T> void addService(Class<T> interfaceClass, T ref) {
        String interfaceName = interfaceClass.getName();
        ServiceConfig<T> serviceConfig = ServiceConfig.convert(interfaceName, interfaceClass, ref, this);
        serviceConfigMap.put(interfaceName, serviceConfig);
    }

    private void register() {
        // 服务注册，在网络监听启动之前就需要完成
        serviceConfigMap.values().forEach(serviceRegister::register);
    }

    public void start() {
        this.register();
        System.out.println("服务注册完成");

        this.serviceConnection = new ServiceConnection();
        this.serviceConnection.init(port, serviceHandler);
        new Thread(serviceConnection).start();

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            RpcService.this.destroy();
        }));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public <K, V> RpcResponse invoke(RpcRequest request) {
        if (request == null) {
            RpcResponse<V> response = new RpcResponse();
            response.setResult(null);
            response.setError(true);
            response.setErrorMessage("未知异常");

            return response;
        }

        String className = request.getClassName();
        ServiceConfig<K> serviceConfig = serviceConfigMap.get(className);
        // 暂时不考虑没有对应serviceconfig的情况

        K ref = serviceConfig.getRef();
        try {
            Method method = ref.getClass().getMethod(
                    request.getMethodName(),
                    request.getParameterTypes());

            V result = (V) method.invoke(ref, request.getArguments());

            RpcResponse<V> response = new RpcResponse();
            response.setResult(result);
            response.setError(false);
            response.setErrorMessage("");

            return response;
        } catch (Exception e) {
        }
        return null;
    }

    public void destroy() {
        this.serviceConnection.destroy();
        System.out.println("服务端关闭了");
    }

}
