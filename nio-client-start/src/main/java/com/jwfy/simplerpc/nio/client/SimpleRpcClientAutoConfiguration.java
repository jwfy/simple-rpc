package com.jwfy.simplerpc.nio.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SimpleRpcClientProerties.class)
public class SimpleRpcClientAutoConfiguration {

    @Bean
    public SimpleRpcClientProerties simpleRpcClientProerties() {
        return new SimpleRpcClientProerties();
    }

    @Bean
    public SimpleRpcClientBean rpcClientBean() {
        return new SimpleRpcClientBean();
    }

    @Bean
    public SimpleRpcClientPostProcessor simpleRpcClientPostProcessor() {
        SimpleRpcClientBean clientBean = rpcClientBean();
        return new SimpleRpcClientPostProcessor(clientBean.getRpcClient());
    }

}
