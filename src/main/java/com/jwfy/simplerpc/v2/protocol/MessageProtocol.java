package com.jwfy.simplerpc.v2.protocol;


import java.io.InputStream;
import java.io.OutputStream;

/**
 * 请求、应答 解析和反解析，包含了序列化以及反序列化操作
 *
 * @author jwfy
 */
public interface MessageProtocol {

    /**
     * 服务端解析从网络传输的数据，转变成request
     * @param inputStream
     * @return
     */
    RpcRequest serviceToRequest(InputStream inputStream);

    /**
     * 服务端把计算机的结果包装好，通过outputStream 返回给客户端
     * @param response
     * @param outputStream
     * @param <T>
     */
     <T> void serviceGetResponse(RpcResponse<T> response, OutputStream outputStream);

    /**
     * 客户端把请求拼接好，通过outputStream发送到服务端
     * @param request
     * @param outputStream
     */
     void clientToRequest(RpcRequest request, OutputStream outputStream);

    /**
     * 客户端接收到服务端响应的结果，转变成response
     * @param inputStream
     */
    <T> RpcResponse<T>  clientGetResponse(InputStream inputStream);
}
