package com.jwfy.simplerpc.v1;


import com.jwfy.simplerpc.v1.expore.Helloworld;

import java.io.IOException;
import java.util.Random;

/**
 * @author jwfy
 */
public class Client {

    public static void main(String args[]) {
        RpcUsedService rpcUsedService = new RpcUsedService();
        rpcUsedService.register(Helloworld.class);

        try {
            IOClient ioClient = new IOClient("127.0.0.1", 10001);
            rpcUsedService.setIoClient(ioClient);

            Helloworld helloworld = rpcUsedService.get(Helloworld.class);

            for(int i=0; i< 100; i++) {
                new Thread(() -> {
                    long start = System.currentTimeMillis();
                    int a = new Random().nextInt(100);
                    int b = new Random().nextInt(100);
                    int c = helloworld.add(a, b);
                    System.out.println("a: " + a + ", b:" + b + ", c=" + c + ", 耗时:" + (System.currentTimeMillis() - start));
                }).start();
            }

        } catch (IOException e) {
        }
    }
}
