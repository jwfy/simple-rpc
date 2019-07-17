package com.jwfy.simplerpc.v2.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jwfy
 */
public class ServiceConnection implements Runnable {

    private int port;

    /**
     * 服务关闭标记位
     */
    private volatile boolean flag = true;

    /**
     * 服务端套接字
     */
    private ServerSocket serverSocket;

    /**
     * 网络处理器
     */
    private ServiceHandler serviceHandler;

    public void init(int port, ServiceHandler serviceHandler) {
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("启动失败:" + e.getMessage());
        }
        this.serviceHandler = serviceHandler;
        System.out.println("服务启动了");
    }

    @Override
    public void run() {
        while (flag) {
            try {
                Socket socket = serverSocket.accept();
                serviceHandler.handler(socket);
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void destroy() {
        System.out.println("服务端套接字关闭");
        this.flag = false;
    }
}
