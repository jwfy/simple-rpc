package com.jwfy.simplerpc.v2.balance;

import java.util.List;
import java.util.Random;

/**
 * @author jwfy
 */
public class DefaultLoadBalance extends AbstractLoadBalance {

    @Override
    String doLoad(List<String> addressList) {
        Random random = new Random();
        return addressList.get(random.nextInt(addressList.size()));
    }
}
