package com.jwfy.simplerpc.v2.protocol;

import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author junhong
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcDecoder(Class<?> clazz, SerializeProtocol serializeProtocol) {
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        Object obj = this.serializeProtocol.deserialize(clazz, data);
        list.add(obj);
    }

}
