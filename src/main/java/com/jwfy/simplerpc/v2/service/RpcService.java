package com.jwfy.simplerpc.v2.service;


import com.jwfy.simplerpc.v2.config.ServiceConfig;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import com.jwfy.simplerpc.v2.register.ServiceRegister;
import com.jwfy.simplerpc.v2.register.ZkServiceRegister;
import com.jwfy.simplerpc.v2.serialize.HessianSerialize;
import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;

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
     * 现在替换成nio模型了
     */
    private ServiceConnection serviceConnection;

    private ServiceHandler serviceHandler;

    private SerializeProtocol serializeProtocol = new HessianSerialize();

    public RpcService(int port) {
        this.port = port;
        this.serviceHandler = new ServiceHandler(this);

        this.serviceRegister = new ZkServiceRegister();
    }

    public void setSerializeProtocol(SerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
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
        this.serviceConnection.init(port, serializeProtocol, serviceHandler);
        this.serviceConnection.start();

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            RpcService.this.close();
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
            response.setRequestId(request.getRequestId());
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
            response.setRequestId(request.getRequestId());
            response.setResult(result);
            response.setError(false);
            response.setErrorMessage("");

            return response;
        } catch (Exception e) {
        }
        return null;
    }

    public void close() {
        this.serviceConnection.close();
        System.out.println("服务端关闭了");
    }

}
