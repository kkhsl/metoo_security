package com.metoo.nspm.core.vo;


import java.io.Serializable;

public class Result<T> implements Serializable {


    private static final long serialVersionUID = 4267799476339238113L;

    private static final String SUCCESS = "SUCCESS";

    private static final Integer CODE = 200;

    /** 状态码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

//    private SysUser user;
//
//    public SysUser getUser(){return user;}
//    public void setUser(SysUser user){
//        this.user=user;
//    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public Result(String msg) {
        this.msg = msg;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public void Ok(String msg) {
        this.code = 200;
        this.data = data;
    }


    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T object) {
        return new Result<T>(CODE, SUCCESS, object);
    }

    public static <T> Result<T> build(Integer code, String message) {
        return new Result<T>(code, message);
    }

    public static <T> Result<T> build(String message) {
        return new Result<T>(message);
    }
}