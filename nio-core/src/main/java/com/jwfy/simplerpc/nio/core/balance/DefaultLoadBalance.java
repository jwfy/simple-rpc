package com.jwfy.simplerpc.nio.core.balance;

import java.util.List;
import java.util.Random;

/**
 * @author jwfy
 */
public class DefaultLoadBalance extends AbstractLoadBalance {

    @Override
    public String doLoad(List<String> socketAddressList) {
        Random random = new Random();
        return socketAddressList.get(random.nextInt(socketAddressList.size()));
    }
}
