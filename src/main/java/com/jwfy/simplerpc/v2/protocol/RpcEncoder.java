package com.jwfy.simplerpc.v2.protocol;

import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jwfy
 */
public class RpcEncoder<T> extends MessageToByteEncoder<T> {

    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<T> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcEncoder(Class<T> clazz, SerializeProtocol serializeProtocol) {
        super(clazz, true);
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, T mgs, ByteBuf byteBuf) throws Exception {
        logger.debug("接收到将序列化操作的数据");
        long startTime = System.currentTimeMillis();
        byte[] bytes = serializeProtocol.serialize(mgs);
        //logger.warn("encode bytes with len:{}\ndata:[{}]", bytes.length, bytes);
        byteBuf.writeBytes(bytes);
        logger.debug("序列化 length:{}, 耗时:{}, {}", bytes.length, System.currentTimeMillis() - startTime, mgs);
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg.getClass() == clazz;
    }
}
