package com.jwfy.simplerpc.nio.core;

import java.util.List;

/**
 * @author jwfy
 */
public interface Calculate<T> {

    T add(T a, T b);

    T sub(T a, T b);

    T bigTest(List<String> list);
}
