package com.jwfy.simplerpc.nio.core.register;

import java.util.Set;

/**
 * @author jwfy
 */
public interface ServiceDiscovery {

    /**
     * 监听
     * @param interfaceName
     */
    void discovery(String interfaceName, Set<String> socketAddressList);

}
