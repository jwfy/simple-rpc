package com.jwfy.simplerpc.v2.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jwfy.simplerpc.v2.protocol.MessageProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * socket 处理入口
 *
 * @author jwfy
 */
public class ServiceHandler {

    private ThreadPoolExecutor executor = null;

    private RpcService rpcService;

    private MessageProtocol messageProtocol;

    public ServiceHandler(RpcService rpcService) {
        this.rpcService = rpcService;

        ThreadFactory commonThreadName = new ThreadFactoryBuilder()
                .setNameFormat("Parse-Task-%d")
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
                Socket socket = socketTask.getSocket();
                if (socket != null) {
                    try {
                        socket.close();
                        System.out.println("reject socket:" + socketTask + ", and closed");
                        // 无法及时处理和响应的就快速拒绝掉
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public RpcService getRpcService() {
        return rpcService;
    }

    public void setRpcService(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    public MessageProtocol getMessageProtocol() {
        return messageProtocol;
    }

    public void setMessageProtocol(MessageProtocol messageProtocol) {
        this.messageProtocol = messageProtocol;
    }

    public void handler(Socket socket) {
        this.executor.execute(new SocketTask(socket));
    }

    class SocketTask implements Runnable {

        private Socket socket;

        public SocketTask(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                RpcRequest request = messageProtocol.serviceToRequest(inputStream);
                RpcResponse response = rpcService.invoke(request);

                System.out.println("request:[" + request + "], response:[" + response + "]");

                messageProtocol.serviceGetResponse(response, outputStream);
            } catch (Exception e) {
                // error
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
