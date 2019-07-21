package com.jwfy.simplerpc.v2.register;


import com.jwfy.simplerpc.v2.balance.DefaultLoadBalance;
import com.jwfy.simplerpc.v2.balance.LoadBalance;
import com.jwfy.simplerpc.v2.config.BasicConfig;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.domain.ServiceType;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * zk 服务管理
 *
 * @author jwfy
 */
public class ZkServiceRegister implements ServiceRegister {

    private CuratorFramework client;

    private static final String ROOT_PATH = "jwfy/simple-rpc";

    private LoadBalance loadBalance = new DefaultLoadBalance();

    public ZkServiceRegister() {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);

        this.client = CuratorFrameworkFactory
                .builder()
                .connectString("127.0.0.1:2182")
                .sessionTimeoutMs(50000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        // 业务的根路径是 /jwfy/simple-rpc ,其他的都会默认挂载在这里

        this.client.start();
        System.out.println("zk启动正常");
    }

    @Override
    public void register(BasicConfig config) {
        String interfacePath = "/" + config.getInterfaceName();
        try {
            if (this.client.checkExists().forPath(interfacePath) == null) {
                // 创建 服务的永久节点
                this.client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(interfacePath);
            }

            config.getMethods().forEach(method -> {
                try {
                    String methodPath = null;
                    ServiceType serviceType = config.getType();
                    if (serviceType == ServiceType.PROVIDER) {
                        // 服务提供方，需要暴露自身的ip、port信息，而消费端则不需要
                        String address = getServiceAddress(config);
                        methodPath = String.format("%s/%s/%s/%s", interfacePath, serviceType.getType(), method.getMethodName(), address);
                    } else {
                        methodPath = String.format("%s/%s/%s", interfacePath, serviceType.getType(), method.getMethodName());
                    }
                    System.out.println("zk path: [" + ROOT_PATH + methodPath + "]");
                    this.client.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(methodPath, "0".getBytes());
                    // 创建临时节点，节点包含了服务提供段的信息
                } catch (Exception e) {
                    e.getMessage();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public InetSocketAddress discovery(RpcRequest request, ServiceType nodeType) {
        String path = String.format("/%s/%s/%s", request.getClassName(), nodeType.getType(), request.getMethodName());
        try {
            List<String> addressList = this.client.getChildren().forPath(path);
            // 采用负载均衡的方式获取服务提供方信息,不过并没有添加watcher监听模式
            String address = loadBalance.balance(addressList);
            if (address == null) {
                return null;
            }
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getServiceAddress(BasicConfig config) {
        String hostInfo = new StringBuilder()
                .append(config.getHost())
                .append(":")
                .append(config.getPort())
                .toString();
        return hostInfo;
    }

    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.valueOf(result[1]));
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        // 可以重新设置负载均衡的策略
        this.loadBalance = loadBalance;
    }
}
