package com.jwfy.simplerpc.nio.client;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.jwfy.simplerpc.nio.core.client.RpcClient;

public class SimpleRpcClientPostProcessor implements BeanPostProcessor {

    private RpcClient rpcClient;

    public SimpleRpcClientPostProcessor(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(SimpleRpcClient.class)) {
                // 只有先添加了，才能在后面的getInstance中获取到代理对象
                Class<?> fieldClazz = field.getType();
                rpcClient.subscribe0(fieldClazz);
                field.setAccessible(true);
                try {
                    field.set(bean, rpcClient.getInstance(fieldClazz));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}
