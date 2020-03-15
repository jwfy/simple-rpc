package com.jwfy.simplerpc.v2.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 利用hessian 序列化工具
 *
 * @author jwfy
 */
public class HessianSerialize implements SerializeProtocol {

    @Override
    public <T> byte[] serialize(T t) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        try {
            hessian2Output.writeObject(t);
            // NOTICE 验证过，一定需要在flush之前关闭掉hessian2Output，否则获取的bytes字段信息是空的
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                hessian2Output.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            outputStream.flush();
            byte[] bytes = outputStream.toByteArray();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        try {
            T t = (T) hessian2Input.readObject();
            return t;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                hessian2Input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
