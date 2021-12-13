package com.titlark.entity;

public class BaseResponse<T> {

    private int code;
    private String message;
    private T data;
    private T list;
    private int count;

    public BaseResponse() {
    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 服务器返回码
     *
     * @return 0：成功  其他错误（错误信息获取 msg字段）
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return 返回信息
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return 返回数据 T
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
