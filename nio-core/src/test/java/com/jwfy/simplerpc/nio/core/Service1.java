package com.jwfy.simplerpc.nio.core;

import com.jwfy.simplerpc.nio.core.service.RpcService;

/**
 * @author jwfy
 */
public class Service1 {

    public static void main(String[] args) {
        RpcService rpcService = new RpcService(10002);
        rpcService.addService(Calculate.class, new SimpleCalculate());

        rpcService.start();
    }

}
