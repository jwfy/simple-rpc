package com.jwfy.simplerpc.nio.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.protocol.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 外面应该管理和持有这些ClientHandler数据，通过这个完成send和recv
 *
 * @author jwfy
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse>  {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private RpcClient rpcClient;

    public ClientHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("注册成功 channel:{}", ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("激活成功 channel:{}", ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("取消注册 channel:{}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("断开 channel:{}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught channel:{}, {}", ctx.channel(), cause.getMessage());
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        this.rpcClient.setResponse(response);
        logger.debug("收到结果 :{}", response);
    }

}
