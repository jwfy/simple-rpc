package com.jwfy.simplerpc.v2.protocol;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jwfy
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -8432924711920321749L;

    /**
     * 以ip + 时间戳组成的唯一请求id
     */
    private String requestId;

    private String className;

    private String methodName;

    private Object[] arguments;

    private  Class<?>[] parameterTypes;

    public RpcRequest() {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.requestId = ip + "#" + System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
