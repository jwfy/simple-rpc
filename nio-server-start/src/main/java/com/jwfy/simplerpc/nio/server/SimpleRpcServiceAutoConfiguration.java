package com.jwfy.simplerpc.nio.server;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SimpleRpcServiceProerties.class)
public class SimpleRpcServiceAutoConfiguration {

    @Bean
    public SimpleRpcServiceProerties simpleRpcServiceProerties() {
        return new SimpleRpcServiceProerties();
    }

    @Bean
    public SimpleRpcServiceBean rpcServiceBean() {
        SimpleRpcServiceProerties proerties = simpleRpcServiceProerties();
        return new SimpleRpcServiceBean(proerties.isDelay(), proerties.getPort());
    }

}
