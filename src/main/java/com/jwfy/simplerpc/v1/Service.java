package com.jwfy.simplerpc.v1;


import com.jwfy.simplerpc.v1.expore.HelloWorldImpl;

import java.io.IOException;

/**
 * @author jwfy
 */
public class Service {

    public static void main(String[] args) {
        RpcExploreService rpcExploreService = new RpcExploreService();
        rpcExploreService.explore("com.jwfy.simplerpc.v1.expore.Helloworld", new HelloWorldImpl());

        try {
            Runnable ioService = new IOService(rpcExploreService, 10001);
            new Thread(ioService).start();
        } catch (IOException e) {

        }
    }

}
