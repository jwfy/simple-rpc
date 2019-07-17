package com.jwfy.simplerpc.v2.balance;

import java.util.List;

/**
 * @author jwfy
 */
public interface LoadBalance {
    String balance(List<String> addressList);
}
