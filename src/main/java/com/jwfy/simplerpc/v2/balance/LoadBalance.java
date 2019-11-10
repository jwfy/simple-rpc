package com.jwfy.simplerpc.v2.balance;

import io.netty.channel.Channel;

import java.util.List;

/**
 * @author jwfy
 */
public interface LoadBalance {
    Channel balance(List<Channel> channelList);
}
