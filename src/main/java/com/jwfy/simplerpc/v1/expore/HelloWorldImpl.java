package com.jwfy.simplerpc.v1.expore;

import java.util.Random;

/**
 * @author jwfy
 */
public class HelloWorldImpl implements Helloworld {

    @Override
    public String hi() {
        return "ok";
    }

    @Override
    public int add(int a, int b) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int c = a + b;
        System.out.println(Thread.currentThread().getName() + " 耗时:" + (System.currentTimeMillis() - start));
        return c;
    }
}
