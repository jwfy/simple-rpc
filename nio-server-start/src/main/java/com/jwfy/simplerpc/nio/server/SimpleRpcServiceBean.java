package com.jwfy.simplerpc.nio.server;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jwfy.simplerpc.nio.core.service.RpcService;

/**
 * 扫描所有的添加了SimpleRpcService注解的类，通过rpcService完成具体的操作
 */

public class SimpleRpcServiceBean implements InitializingBean, ApplicationContextAware,
        DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private RpcService rpcService;

    /**
     * 是否延期暴露
     */
    private boolean isDelay;

    /**
     * 暴露标志位，避免重复暴露操作
     */
    private volatile boolean usedExport;

    private int port;

    public SimpleRpcServiceBean(boolean isDelay, int port) {
        this.isDelay = isDelay;
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.port == 0) {
            throw new RuntimeException("未设置启动端口号");
        }
        this.rpcService = new RpcService(this.port);
        // 原本在demo中的rpcService在这里创建
        if (!isDelay) {
            export();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!usedExport) {
            export();
        }
    }

    private void export() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                try {
                    Map<String, Object> map = applicationContext.getBeansWithAnnotation(SimpleRpcService.class);
                    for (Object obj : map.values()) {
                        SimpleRpcService simpleRpcService = obj.getClass().getAnnotation(SimpleRpcService.class);
                        Class<?> clazz = simpleRpcService.value();
                        rpcService.addService(clazz, obj);
                    }
                    rpcService.start();
                    flag = true;
                } finally {
                    if (flag) {
                        usedExport = true;
                    }
                }
            }
        }).start();

    }

    @Override
    public void destroy() throws Exception {
        this.rpcService.close();
    }
}
