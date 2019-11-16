package com.jwfy.simplerpc.v2.service;

import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理入口
 *
 * @author jwfy
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

    private RpcInvoke rpcInvoke;

    public ServiceHandler(RpcInvoke rpcInvoke) {
        this.rpcInvoke = rpcInvoke;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("收到连接请求:{}", ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("取消注册 channel:{}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("链接断开请求:{}", ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.warn("收到注册请求:{}", ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        logger.info("收到请求:" + request);
        this.rpcInvoke.invoke(ctx, request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("出现异常:{}, cause:{}", ctx.channel(), cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
    }
}
