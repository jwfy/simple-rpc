package com.jwfy.simplerpc.v2.config;


import com.jwfy.simplerpc.v2.domain.ServiceType;

import java.util.List;

/**
 * @author jwfy
 */
public class BasicConfig {

    private String host;
    private int port;

    /**
     * 服务提供方还是服务消毒方
     */
    private ServiceType type;

    private String interfaceName;

    private Class<?> interfaceClass;

    private List<MethodConfig> methods;

    private String group;

    /**
     * 默认版本号是default
     */
    private String version = "default";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }
}
