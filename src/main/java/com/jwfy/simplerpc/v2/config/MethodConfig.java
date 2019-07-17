package com.jwfy.simplerpc.v2.config;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jwfy
 */
public class MethodConfig implements Serializable {

    private static final long serialVersionUID = -9067431548477691759L;

    private String methodName;

    private List<ArgumentConfig> argumentConfigs;

    /**
     * 是否需要返回
     */
    private Boolean isReturn;

    private Class<?> returnType;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Boolean getReturn() {
        return isReturn;
    }

    public void setReturn(Boolean aReturn) {
        isReturn = aReturn;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public List<ArgumentConfig> getArgumentConfigs() {
        return argumentConfigs;
    }

    public void setArgumentConfigs(List<ArgumentConfig> argumentConfigs) {
        this.argumentConfigs = argumentConfigs;
    }

    public static List<MethodConfig> convert(Method[] methods) {
        List<MethodConfig> methodConfigList = new ArrayList<>(methods.length);
        for(Method method : methods) {
            MethodConfig methodConfig = new MethodConfig();
            methodConfig.setMethodName(method.getName());

            Class<?> returnType = method.getReturnType();
            String returnName = returnType.getName();
            if ("void".equals(returnName)) {
                methodConfig.setReturn(false);
            } else {
                methodConfig.setReturn(true);
            }
            methodConfig.setReturnType(returnType);
            methodConfig.setArgumentConfigs(convert(method.getParameters()));

            methodConfigList.add(methodConfig);
        }
        return methodConfigList;
    }

    private static List<ArgumentConfig> convert(Parameter[] parameters) {
        List<ArgumentConfig> argumentConfigs = new ArrayList<>(parameters.length);

        int start = 0;
        for(Parameter parameter : parameters) {
            ArgumentConfig argumentConfig = new ArgumentConfig();

            argumentConfig.setIndex(start);
            argumentConfig.setType(parameter.getType().getName());

            argumentConfigs.add(argumentConfig);

            start += 1;
        }
        return argumentConfigs;
    }
}
