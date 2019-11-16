package com.jwfy.simplerpc.v2.client;


import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 外面应该管理和持有这些ClientHandler数据，通过这个完成send和recv
 *
 * @author jwfy
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse>  {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    /**
     * ip  ==> channel
     */
    private Map<String, Channel> ipMap = new ConcurrentHashMap<>();

    /**
     * interface ==>  list(ipA, ipB, ...)
     */
    private Map<String, List<String>> interfaceMap = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("注册成功 channel:{}", ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("激活成功 channel:{}", ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("取消注册 channel:{}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("断开 channel:{}", ctx.channel());
        removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught channel:{}, {}", ctx.channel(), cause.getMessage());
        ctx.channel().close();
        removeChannel(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        RequestManager.getInstance().setResponse(response);
        logger.debug("收到结果 :{}", response);
    }

    public void addChannel(Channel channel, String interfaceName, String ip) {
        ipMap.put(ip, channel);
        List<String> ipList = interfaceMap.get(interfaceName);
        if (ipList == null) {
            ipList = new ArrayList<>();
            interfaceMap.put(interfaceName, ipList);
        }
        ipList.add(ip);
    }

    public Channel getChannel(String ip) {
        return ipMap.get(ip);
    }

    public void removeChannel(String interfaceName, String ip) {
        logger.warn("removeChannel with interfaceName:{}, ip:{}", interfaceName, ip);
        Channel channel = ipMap.remove(ip);
        if (channel != null) {
            channel.close();
            interfaceMap.get(interfaceName).remove(ip);
        }
    }

    public void removeChannel(Channel channel) {
        Iterator<Map.Entry<String, Channel>> iterator = ipMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            if (entry.getValue() == channel) {
                iterator.remove();
            }
        }
    }

    public List<Channel> getChannels(String interfaceName) {
        long start = System.currentTimeMillis();
        List<String> ips = interfaceMap.getOrDefault(interfaceName, new ArrayList<>());

        List<Channel> channelList = new ArrayList<>();
        for(String ip : ips) {
            Channel channel = ipMap.get(ip);
            if (channel != null && channel.isWritable()) {
                channelList.add(channel);
            }
        }
        logger.debug("getChannels costTime:{},  channels:{}", (System.currentTimeMillis() - start), channelList);
        return channelList;
    }
}
