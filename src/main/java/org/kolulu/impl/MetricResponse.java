package org.kolulu.impl;

import java.util.Map;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 23:33
 */
public class MetricResponse {
    private boolean success;
    private int code;
    private String message;
    private Map<String, Object> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
