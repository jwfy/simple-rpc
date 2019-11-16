package com.jwfy.simplerpc.v2.protocol;

import com.jwfy.simplerpc.v2.serialize.SerializeProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jwfy
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<?> clazz;

    private SerializeProtocol serializeProtocol;

    public RpcDecoder(Class<?> clazz, SerializeProtocol serializeProtocol) {
        this.clazz = clazz;
        this.serializeProtocol = serializeProtocol;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.debug("接收到将反序列化操作的数据");
        long startTime = System.currentTimeMillis();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        // logger.debug("反序列化 字节数据 with len:{}, data:{}", data.length, data);
        Object obj = this.serializeProtocol.deserialize(clazz, data);
        list.add(obj);
        logger.debug("反序列化 length:{}, 耗时:{}", data.length, System.currentTimeMillis() - startTime);
    }

}
