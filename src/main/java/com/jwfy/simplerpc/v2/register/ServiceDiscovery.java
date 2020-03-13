package com.jwfy.simplerpc.v2.register;

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
