package com.jwfy.simplerpc.v2.register;

import com.jwfy.simplerpc.v2.client.RpcClient;
import com.jwfy.simplerpc.v2.domain.ServiceType;
import com.jwfy.simplerpc.v2.util.CommonUtils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;

/**
 * @author jwfy
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private CuratorFramework client;

    private RpcClient rpcClient;

    public ZkServiceDiscovery(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        RegisterConfig registerConfig = this.rpcClient.getRegisterConfig();

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory
                .builder()
                .connectString(registerConfig.getZkHost())
                .sessionTimeoutMs(registerConfig.getSessionTimeOut())
                .retryPolicy(policy)
                .namespace(registerConfig.getZkNameSpace())
                .build();

        this.client.start();
        logger.info("zk启动正常");
    }

    @Override
    public void discovery(String interfaceName, Set<String> socketAddressList) {
        String zkPath = String.format("/%s/%s", interfaceName, ServiceType.PROVIDER.getType());

        try {
            List<String> ipList = this.client.getChildren().forPath(zkPath);
            if (ipList != null && !ipList.isEmpty()) {
                ipList.forEach(ip -> {
                    socketAddressList.add(ip);
                    //rpcClient.getClientConnection().connection(interfaceName, ip, true);
                });
            }
            addWatcher(zkPath, socketAddressList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addWatcher(String zkPath, Set<String> socketAddressList) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(this.client, zkPath, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (CHILD_ADDED == event.getType() || CHILD_REMOVED == event.getType()) {
                    String[] childPath = event.getData().getPath().split("/");
                    String ip = childPath[childPath.length-1];
                    PathChildrenCacheEvent.Type type = event.getType();
                    logger.info("path:[{}]，ip:[{}], type:{}", zkPath, ip, type.toString());
                    if (CHILD_ADDED == type) {
                        // 监听到节点添加
                        logger.warn("node add :{}", event);
                        socketAddressList.add(ip);
                        //rpcClient.getClientConnection().connection(interfaceName, ip, false);
                    } else if (CHILD_REMOVED == type) {
                        // 监听到节点移除
                        logger.warn("node remove :{}", event);
                        socketAddressList.remove(ip);
                        //rpcClient.getClientConnection().remove(interfaceName, ip);
                    }
                }
            }
        });
    }

}
