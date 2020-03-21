package com.jwfy.simplerpc.nio.core;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.client.RpcClient;

/**
 * @author jwfy
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(
            Client.class);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        rpcClient.subscribe(Calculate.class);
        rpcClient.start();

        Calculate<Integer> calculateProxy = rpcClient.getInstance(Calculate.class);

        for(int i=1; i<=1000; i++) {
            test(calculateProxy);
            if (i==500) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //new Thread(() -> {
            //    test(calculateProxy);
            //}).start();
        }
    }

    private static void test(Calculate<Integer> calculateProxy) {
        try {
            int s1 = new Random().nextInt(100);
            int s2 = new Random().nextInt(100);
            long start = System.currentTimeMillis();
            int s3 = calculateProxy.add(s1, s2);
            logger.info("["
                    + Thread.currentThread().getName()
                    + "]a: "
                    + s1
                    + ", b:"
                    + s2
                    + ", c="
                    + s3
                    + ", 耗时:"
                    + (System.currentTimeMillis() - start));
        } catch (Exception e) {

        }
    }

}
