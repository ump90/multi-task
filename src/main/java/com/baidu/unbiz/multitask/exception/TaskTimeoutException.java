package com.baidu.unbiz.multitask.exception;

/**
 * 数据获取超时异常
 * 
 * @author wangchongjie
 * @fileName TaskTimeoutException.java
 * @dateTime 2015-7-3 下午4:04:06
 */
public class TaskTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -7375423850222016116L;

    public TaskTimeoutException(String msg) {
        super(msg);
    }

    public TaskTimeoutException(Throwable cause) {
        super(cause);
    }

    public TaskTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
