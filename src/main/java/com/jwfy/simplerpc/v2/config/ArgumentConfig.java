package com.jwfy.simplerpc.v2.config;

import java.io.Serializable;

/**
 * @author jwfy
 */
public class ArgumentConfig implements Serializable {

    private static final long serialVersionUID = -5700787831013626220L;

    /**
     * 第几个参数
     */
    private int index;

    /**
     * 参数类型
     */
    private String type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
