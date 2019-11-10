package com.jwfy.simplerpc.v2.client;

import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 利用栅栏锁完成数据的控制读取
 *
 * @author junhong
 */
public class RpcResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseFuture.class);

    private RpcResponse response;

    private CountDownLatch countDownLatch;

    public RpcResponseFuture() {
        this.countDownLatch = new CountDownLatch(1);
    }

    public RpcResponse getResponse() throws InterruptedException {
        // 阻塞在这里，直到得到了数据
        this.countDownLatch.await();
        return this.response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
        this.countDownLatch.countDown();
    }
}
