package com.baidu.unbiz.multitask.utils;

import com.baidu.unbiz.multitask.constants.TaskConfig;

/**
 * Created by wangchongjie on 15/12/21.
 */
public class AssistUtils {
    public static String removeTaskVersion(String fetcherName) {
        return fetcherName.replaceAll(TaskConfig.TASKNAME_SEPARATOR + ".*", "");
    }
}
