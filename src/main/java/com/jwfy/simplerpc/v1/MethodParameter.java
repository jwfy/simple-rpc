package com.jwfy.simplerpc.v1;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author jwfy
 */

public class MethodParameter {

    String className;
    String methodName;
    Object[] arguments;
    Class<?>[] parameterTypes;

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

    public static MethodParameter convert(InputStream inputStream) {

        try {
            ObjectInputStream input = new ObjectInputStream(inputStream);
            String className = input.readUTF();
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
            Object[] arguments = (Object[])input.readObject();

            MethodParameter methodParameter = new MethodParameter();
            methodParameter.setClassName(className);
            methodParameter.setMethodName(methodName);
            methodParameter.setArguments(arguments);
            methodParameter.setParameterTypes(parameterTypes);

            return methodParameter;
        } catch (Exception e) {
            throw new RuntimeException("解析请求错误:" + e.getMessage());
        }
    }

}
