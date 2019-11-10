package com.jwfy.simplerpc.v2.util;

import java.net.InetSocketAddress;

/**
 * @author junhong
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
