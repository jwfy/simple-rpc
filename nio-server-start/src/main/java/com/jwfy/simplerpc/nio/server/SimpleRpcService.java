package com.jwfy.simplerpc.nio.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

/**
 * 如果需要对外暴露，则需要添加该注解,默认该类的所有公共方法都会被暴露出去
 *
 * 添加了Spring的Service注解
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface SimpleRpcService {

    Class<?> value();
}
