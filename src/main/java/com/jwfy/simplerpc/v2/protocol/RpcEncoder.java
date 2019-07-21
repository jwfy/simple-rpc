package com.jwfy.simplerpc.v2.protocol;

import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author junhong
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcEncoder(Class<?> clazz, SerializeProtocol serializeProtocol) {
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)) {
            byte[] bytes = serializeProtocol.serialize(o);
            byteBuf.writeBytes(bytes);
        }
    }
}
