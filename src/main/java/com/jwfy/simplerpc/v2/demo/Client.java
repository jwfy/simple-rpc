package com.jwfy.simplerpc.v2.demo;


import com.jwfy.simplerpc.v2.core.RpcClient;

import java.util.Random;

/**
 * @author jwfy
 */
public class Client {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();

        rpcClient.subscribe(Calculate.class);
        rpcClient.start();

        Calculate<Integer> calculateProxy = rpcClient.getInstance(Calculate.class);

        for(int i=0; i< 1; i++) {
            new Thread(() -> {
                long start = System.currentTimeMillis();
                int s1 = new Random().nextInt(100);
                int s2 = new Random().nextInt(100);
                int s3 = calculateProxy.add(s1, s2);
                System.out.println("[" + Thread.currentThread().getName() + "]a: " + s1 + ", b:" + s2 + ", c=" + s3 + ", 耗时:" + (System.currentTimeMillis() - start));
            }).start();
        }

    }

}
