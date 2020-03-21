package com.jwfy.simplerpc.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author jwfy
 */
public class IOClient {

    private String ip;
    private int port;

    public IOClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
    }

    public Object invoke(MethodParameter methodParameter) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);

            OutputStream outputStream = socket.getOutputStream();

            ObjectOutputStream ouput = new ObjectOutputStream(outputStream);

            ouput.writeUTF(methodParameter.getClassName());
            ouput.writeUTF(methodParameter.getMethodName());
            ouput.writeObject(methodParameter.getParameterTypes());
            ouput.writeObject(methodParameter.getArguments());

            InputStream inputStream = socket.getInputStream();
            ObjectInputStream input = new ObjectInputStream(inputStream);
            return input.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
