package com.jwfy.simplerpc.v2.protocol;

import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author junhong
 */
public class RpcEncoder extends MessageToByteEncoder {

    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<?> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcEncoder(Class<?> clazz, SerializeProtocol serializeProtocol) {
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        long startTime = System.currentTimeMillis();
        byte[] bytes = serializeProtocol.serialize(o);

        // logger.debug("encde bytes with len:{}, data:{}", bytes.length, bytes);
//        byteBuf.writeInt(bytes.length);

//        byte[] cur = new byte[2];
//        cur[0] = (byte) (bytes.length & 0xff);   // -1
//        cur[1] = (byte) (bytes.length >>> 8 & 0xff);  // 0

        byteBuf.writeShort(bytes.length);
        byteBuf.writeBytes(bytes);

        logger.debug("序列化 length:{}, 耗时:{}, {}", bytes.length, System.currentTimeMillis() - startTime, o);
    }
}
