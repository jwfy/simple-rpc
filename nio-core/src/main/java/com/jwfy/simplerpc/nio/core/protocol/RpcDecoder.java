package com.jwfy.simplerpc.nio.core.protocol;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jwfy.simplerpc.nio.core.serialize.SerializeProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author jwfy
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<T> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcDecoder(Class<T> clazz, SerializeProtocol serializeProtocol) {
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.debug("接收到将反序列化操作的数据");
        long startTime = System.currentTimeMillis();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        logger.debug("反序列化 字节数据 with len:{}, data:{}", data.length, data);
        T obj = this.serializeProtocol.deserialize(clazz, data);
        list.add(obj);
        logger.debug("反序列化 length:{}, 耗时:{}", data.length, System.currentTimeMillis() - startTime);
    }

}
