package com.jwfy.simplerpc.v2.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.*;

/**
 * socket 处理入口
 *
 * @author jwfy
 */
public class ServiceHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ThreadPoolExecutor executor = null;

    private RpcService rpcService;

    public ServiceHandler(RpcService rpcService) {
        this.rpcService = rpcService;

        ThreadFactory commonThreadName = new ThreadFactoryBuilder()
                .setNameFormat("Handler-Task-%d")
                .build();

        this.executor = new ThreadPoolExecutor(
                10,
                10,
                2,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                commonThreadName, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                SocketTask socketTask = (SocketTask) r;
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, RpcRequest request) throws Exception {
        // 接收到了服务端的request请求，需要调用给rpcservice进行处理
        this.executor.execute(new SocketTask(context, request));
    }

    public RpcService getRpcService() {
        return rpcService;
    }

    public void setRpcService(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    class SocketTask implements Runnable {

        private ChannelHandlerContext context;
        private RpcRequest request;

        public SocketTask(ChannelHandlerContext context, RpcRequest request) {
            this.context = context;
            this.request = request;
        }

        @Override
        public void run() {
            RpcResponse response = rpcService.invoke(request);
            context.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("返回响应结果：" + request.getRequestId());
                }
            });
        }
    }
}
