package com.jwfy.simplerpc.nio.core.protocol;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * @author jwfy
 */
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -6786762199234477466L;

    private String requestId;

    private T result;

    private Boolean error;

    private String errorMessage;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
