package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author jwfy
 */
public class DefaultLoadBalance extends AbstractLoadBalance {

    @Override
    public String doLoad(List<String> socketAddressList) {
        Random random = new Random();
        return socketAddressList.get(random.nextInt(socketAddressList.size()));
    }
}
