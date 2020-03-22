package com.jwfy.simplerpc.nio.core.register;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.config.BasicConfig;
import com.jwfy.simplerpc.nio.core.config.ServiceConfig;
import com.jwfy.simplerpc.nio.core.domain.ServiceType;

/**
 * zk 服务管理
 *
 * @author jwfy
 */
public class ZkServiceRegister implements ServiceRegister {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceRegister.class);

    private CuratorFramework client;

    private RegisterConfig registerConfig;

    public ZkServiceRegister(RegisterConfig registerConfig) {
        this.registerConfig = registerConfig;
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        this.client = CuratorFrameworkFactory
                .builder()
                .connectString(registerConfig.getZkHost())
                .sessionTimeoutMs(registerConfig.getSessionTimeOut())
                .retryPolicy(policy)
                .namespace(registerConfig.getZkNameSpace())
                .build();
        // 业务的根路径是 /jwfy/simple-rpc ,其他的都会默认挂载在这里

        this.client.start();
        logger.info("zk启动正常");
    }

    @Override
    public void registerList(List<ServiceConfig<?>> configList) {
        configList.forEach(this::register);
        logger.info("服务注册完成");
    }

    @Override
    public void register(ServiceConfig<?> config) {
        String interfacePath = "/" + config.getInterfaceName();
        try {
            if (this.client.checkExists().forPath(interfacePath) == null) {
                // 创建 服务的永久节点
                this.client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(interfacePath);
            }
            String address = getServiceAddress(config);
            String path = String.format("%s/%s/%s", interfacePath, ServiceType.PROVIDER.getType(), address);

            logger.info("注册 zk path: [" + this.registerConfig.getZkNameSpace() + path + "]");
            this.client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, "0".getBytes());
            // 创建临时节点，节点包含了服务提供段的信息
        } catch (Exception e) {
            logger.error("注册zk失败, [{}]:{}", interfacePath, e.getMessage());
            throw new RuntimeException("注册zk失败，退出服务");
        }
    }

    @Override
    public void close() {
        this.client.close();
        logger.info("zkClient关闭");
    }

    private String getServiceAddress(BasicConfig config) {
        return new StringBuilder().append(config.getHost()).append(":").append(config.getPort()).toString();
    }
}
