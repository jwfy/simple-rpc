package com.jwfy.simplerpc.v2.serialize;

/**
 * @author jwfy
 */
public interface SerializeProtocol {

    /**
     * 序列化
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     */
     <T> T deserialize(Class<T> clazz, byte[] bytes);

}
