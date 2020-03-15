package com.jwfy.simplerpc.v2.demo;


import com.jwfy.simplerpc.v2.service.RpcService;

/**
 * @author jwfy
 */
public class Service2 {

    public static void main(String[] args) {
        RpcService rpcService = new RpcService(10003);
        rpcService.addService(Calculate.class, new SimpleCalculate());
        rpcService.addService(IStudentService.class, new StudentService());

        rpcService.start();
    }

}
