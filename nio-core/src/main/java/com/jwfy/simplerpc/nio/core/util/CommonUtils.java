package com.jwfy.simplerpc.nio.core.util;

import java.net.InetSocketAddress;

/**
 * @author jwfy
 */
public class CommonUtils {

    public static InetSocketAddress parseIp(String address) {
        if (address == null || address.length() == 0) {
            return null;
        }
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.valueOf(result[1]));
    }

}
