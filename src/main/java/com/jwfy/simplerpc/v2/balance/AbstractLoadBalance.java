package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.util.List;

/**
 * @author jwfy
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public Channel balance(List<Channel> channelList) {
        if (channelList == null || channelList.isEmpty()) {
            return null;
        }
        if (channelList.size() == 1) {
            return channelList.get(0);
        }
        return doLoad(channelList);
    }

    abstract Channel doLoad(List<Channel> addressList);
}
