package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Random;

/**
 * @author jwfy
 */
public class DefaultLoadBalance extends AbstractLoadBalance {

    @Override
    public Channel doLoad(List<Channel> channelList) {
        Random random = new Random();
        return channelList.get(random.nextInt(channelList.size()));
    }
}
