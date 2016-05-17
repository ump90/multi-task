package com.baidu.unbiz.multitask.task;

import com.baidu.unbiz.multitask.exception.TaskBizException;

/**
 * TaskBean执行参数辅助类
 *
 * Created by baidu on 15/12/24.
 */
public class Params {

    private Object[] params;
    private int cursor = 0;

    private Params(int paramLength) {
        if (paramLength < 2) {
            throw new TaskBizException(String.format("worng params length:%s", paramLength));
        }
        params = new Object[paramLength];
    }

    public <T> Params add(T param) {
        params[cursor] = (Object) param;
        cursor++;
        return this;
    }

    /**
     * @param index 从0开始
     * @return 返回index对应的参数
     */
    public <T> T of(int index) {
        return (T) params[index];
    }

    /**
     * @return 参数数组
     */
    public Object[] use() {
        return this.params;
    }

    /**
     * @param paramLength
     * @return 指定长度的Params
     */
    public static Params with(int paramLength) {
        return new Params(paramLength);
    }
}
