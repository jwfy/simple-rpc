package com.jwfy.simplerpc.v2.core;


import com.jwfy.simplerpc.v2.protocol.MessageProtocol;
import com.jwfy.simplerpc.v2.protocol.RpcRequest;
import com.jwfy.simplerpc.v2.protocol.RpcResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author jwfy
 */
public class ClientHandler {

    private RpcClient rpcClient;

    private MessageProtocol messageProtocol;

    public ClientHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void setMessageProtocol(MessageProtocol messageProtocol) {
        this.messageProtocol = messageProtocol;
    }

    public <T> RpcResponse<T> invoke(RpcRequest request, InetSocketAddress address) {
        RpcResponse<T> response = new RpcResponse<>();

        Socket socket = getSocketInstance(address);
        if (socket == null) {
            // 套接字链接失败
            response.setError(true);
            response.setErrorMessage("套接字链接失败");
            return response;
        }

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            messageProtocol.clientToRequest(request, outputStream);

//            socket.shutdownOutput();

            response = messageProtocol.clientGetResponse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    private Socket getSocketInstance(InetSocketAddress address) {
        try {
            return new Socket(address.getHostString(), address.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
