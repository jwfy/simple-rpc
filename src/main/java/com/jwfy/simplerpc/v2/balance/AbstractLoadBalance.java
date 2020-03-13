package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jwfy
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String balance(Set<String> socketAddressList) {
        if (socketAddressList == null || socketAddressList.isEmpty()) {
            return null;
        }
        List<String> list = new ArrayList<>(socketAddressList);
        if (socketAddressList.size() == 1) {
            return list.get(0);
        }
        return doLoad(list);
    }

    abstract String doLoad(List<String> socketAddressList);
}
