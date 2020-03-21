package com.jwfy.simplerpc.nio.core;

import com.jwfy.simplerpc.nio.core.service.RpcService;

/**
 * @author jwfy
 */
public class Service {

    public static void main(String[] args) {
        RpcService rpcService = new RpcService(10001);
        rpcService.addService(Calculate.class, new SimpleCalculate());
        rpcService.addService(IStudentService.class, new StudentService());

        rpcService.start();
    }

}
