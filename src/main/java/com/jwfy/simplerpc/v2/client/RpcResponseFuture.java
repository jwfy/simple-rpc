package com.jwfy.simplerpc.v2.client;

import com.jwfy.simplerpc.v2.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 利用栅栏锁完成数据的控制读取
 *
 * @author jwfy
 */
public class RpcResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseFuture.class);

    private RpcResponse response;

    private boolean hasResult;

    private CountDownLatch countDownLatch;

    public RpcResponseFuture(boolean hasResult) {
        this.hasResult = hasResult;
        this.countDownLatch = new CountDownLatch(1);
    }

    public RpcResponse getResponse() {
        // 阻塞在这里，直到得到了数据，默认3s
        return getResponse(3, TimeUnit.SECONDS);
    }

    public RpcResponse getResponse(long timeout, TimeUnit unit) {
        // 阻塞在这里，直到得到了数据
        boolean flag = false;
        try {
            flag = this.countDownLatch.await(timeout, unit);
            if (!flag) {
                throw new TimeoutException("timeout");
            }
        } catch (Exception e) {
            this.response = new RpcResponse();
            this.response.setError(true);
            this.response.setErrorMessage(e.getMessage());
        }
        return this.response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
        this.countDownLatch.countDown();
    }
}
