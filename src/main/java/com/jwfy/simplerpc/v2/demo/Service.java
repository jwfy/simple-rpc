package com.jwfy.simplerpc.v2.demo;


import com.jwfy.simplerpc.v2.service.RpcService;

/**
 * @author jwfy
 */
public class Service {

    public static void main(String[] args) {
        RpcService rpcService = new RpcService(10001);
        rpcService.addService(Calculate.class, new SimpleCalculate());

        rpcService.start();
    }

}
