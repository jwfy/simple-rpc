package com.jwfy.simplerpc.nio.core.protocol;

import java.io.Serializable;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

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

    private Class<?>[] parameterTypes;

    public RpcRequest() {
        this.requestId = UUID.randomUUID().toString();
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
