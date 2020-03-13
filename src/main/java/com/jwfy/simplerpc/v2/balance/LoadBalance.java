package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * @author jwfy
 */
public interface LoadBalance {
    String balance(Set<String> socketAddressList);
}
