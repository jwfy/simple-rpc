package com.jwfy.simplerpc.nio.core.balance;

import java.util.Set;

/**
 * @author jwfy
 */
public interface LoadBalance {
    String balance(Set<String> socketAddressList);
}
