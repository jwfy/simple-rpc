package com.jwfy.simplerpc.v2.register;


/**
 * @author jwfy
 */
public interface ServiceDiscovery {

    /**
     * 监听
     * @param interfaceName
     */
    void discovery(String interfaceName);

}
