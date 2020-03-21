package com.jwfy.simplerpc.nio.core;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author jwfy
 */

public class SimpleCalculate implements Calculate<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(
            SimpleCalculate.class);

    @Override
    public Integer add(Integer a, Integer b) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(new Random().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int c =  a + b;
        logger.info("请求结果:{} + {} = {}, 耗时:{}", a, b, c, (System.currentTimeMillis() - start));
        return c;
    }

    @Override
    public Integer sub(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer bigTest(List<String> list) {
        return 0;
    }
}
