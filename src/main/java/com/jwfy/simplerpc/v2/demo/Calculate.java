package com.jwfy.simplerpc.v2.demo;

/**
 * @author jwfy
 */
public interface Calculate<T> {

    T add(T a, T b);

    T sub(T a, T b);
}
