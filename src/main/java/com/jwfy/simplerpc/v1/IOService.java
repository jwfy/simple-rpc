package com.jwfy.simplerpc.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jwfy
 */
public class IOService implements Runnable{

    private int port;

    private ServerSocket serverSocket;

    private RpcExploreService rpcExploreService;

    private volatile boolean flag;

    private ThreadPoolExecutor executor;

    public IOService(RpcExploreService rpcExploreService, int port) throws IOException {
        this.rpcExploreService = rpcExploreService;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.flag = true;
        System.out.println("服务端启动了");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                flag = false;
                System.out.println("服务端关闭了");
            }
        });

        this.executor =new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5), new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void run() {
        while (flag) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {

            }
            if (socket == null) {
                continue;
            }

            this.executor.execute(new ServerSocketRunnable(socket));
            //new Thread(new ServerSocketRunnable(socket)).start();
        }
    }

    class ServerSocketRunnable implements Runnable {

        private Socket socket;

        public ServerSocketRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                MethodParameter methodParameter = MethodParameter.convert(inputStream);

                Object result = rpcExploreService.invoke(methodParameter);

                ObjectOutputStream output = new ObjectOutputStream(outputStream);

                output.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
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
