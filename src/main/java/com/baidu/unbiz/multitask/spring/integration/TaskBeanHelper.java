package com.baidu.unbiz.multitask.spring.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multitask.exception.TaskBizException;
import com.baidu.unbiz.multitask.task.Taskable;

/**
 * Created by wangchongjie on 15/12/25.
 */
public class TaskBeanHelper {

    private static final Log LOG = LogFactory.getLog(TaskBeanHelper.class);
    private static final int NORMAL_PARAM_LENTH = 1;

    /**
     * 经带多个参数的方法，包装成Fetcher
     *
     * @param service
     * @param method
     * @param beanName
     * @param paramLen
     *
     * @return Fetcher
     */
    public static Taskable<?> newFetcher(final Object service, final Method method, final String beanName,
                                         final int paramLen) {
        if (NORMAL_PARAM_LENTH == paramLen) {
            return newNormalFetcher(service, method, beanName);
        }

        return new Taskable<Object>() {
            @Override
            public <E> Object work(E request) {
                return invokeMethod(service, method, beanName, paramLen, request);
            }
        };
    }

    /**
     * 创建带一个参数的正常Fetcher
     *
     * @param service
     * @param method
     * @param beanName
     *
     * @return Fetcher
     */
    private static Taskable<?> newNormalFetcher(final Object service, final Method method, final String beanName) {
        return new Taskable<Object>() {
            @Override
            public <E> Object work(E request) {
                return invokeMethod(service, method, beanName, NORMAL_PARAM_LENTH, request);
            }
        };
    }

    private static <E> Object invokeMethod(final Object service, final Method method, final String beanName,
                                           final int paramLen, E request) {
        String logFormat = "Fail to run task bean: %s";
        Exception ex = null;
        try {
            return TaskBeanHelper.invokeMethod(service, method, request, paramLen);
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InvocationTargetException e) {
            LOG.error(String.format(logFormat, beanName), e);
            // 该类异常通常为业务异常导致，需获取原始原因并上抛
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                ex = e;
            }
        } catch (Exception e) {
            ex = e;
        }
        // 执行到此处，则说明发生异常
        LOG.error(String.format(logFormat, beanName), ex);
        throw new TaskBizException(String.format(logFormat, beanName), ex);
    }

    /**
     * 执行方法，支持单参或多参
     *
     * @param service
     * @param method
     * @param request
     * @param paramLen
     *
     * @return 方法执行结果
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static <T> Object invokeMethod(Object service, Method method, T request, int paramLen)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        switch (paramLen) {
            case 0:
                return method.invoke(service);
            case 1:
                return method.invoke(service, request);
            default:
                Object[] params = (Object[]) request;
                return method.invoke(service, params);
        }
    }
}
