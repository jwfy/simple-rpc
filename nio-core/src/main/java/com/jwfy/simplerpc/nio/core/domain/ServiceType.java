package com.jwfy.simplerpc.nio.core.domain;

/**
 * @author jwfy
 */
public enum ServiceType {

    /**
     * 服务提供者
     */
    PROVIDER("provider"),

    /**
     * 服务消费者
     */
    CONSUMER("consumer")
    ;

    private String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }}
