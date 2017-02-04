package com.baidu.unbiz.multitask.spring.integration;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.baidu.unbiz.multitask.annotation.TaskBean;
import com.baidu.unbiz.multitask.annotation.TaskService;
import com.baidu.unbiz.multitask.exception.TaskBizException;
import com.baidu.unbiz.multitask.task.Taskable;

/**
 * Fetcher Bean容器，与spring集成
 *
 * @author wangchongjie
 * @since 2015-8-9 下午2:18:00
 */
@Component
public class TaskBeanContainer implements ApplicationContextAware, PriorityOrdered {

    private static final Log LOG = LogFactory.getLog(TaskBeanContainer.class);

    // Spring应用上下文环境
    private static ApplicationContext applicationContext;
    private static Map<String, Taskable<?>> container = new ConcurrentHashMap<String, Taskable<?>>();
    private static AtomicBoolean initing = new AtomicBoolean(false);
    private static CountDownLatch hasInit = new CountDownLatch(1);
    private static volatile String springContainerInstanceFlag = "";

    // @PostConstruct
    public static void initFetcherContainer() {
        initFetcherContainer(applicationContext);
    }

    /**
     * 初始化Fetcher容器
     *
     * @param factory
     */
    public static void initFetcherContainer(ListableBeanFactory factory) {
        if (initing.get()) {
            waitInit();
            return;
        }
        if (initing.compareAndSet(false, true)) {
            Map<String, Object> fetcherServices = factory.getBeansWithAnnotation(TaskService.class);
            for (Object service : fetcherServices.values()) {
                regiserOneService(service);
            }
            hasInit.countDown();
        } else {
            waitInit();
        }
    }

    private static void waitInit() {
        try {
            hasInit.await();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting init.", e);
        }
    }

    /**
     * 向容器中注册一个service中带FetcherBean的方法，并包装成Fetcher
     *
     * @param service
     */
    public static void regiserOneService(Object service) {
        Class<?> clazz = service.getClass();
        for (Method method : ReflectionUtils.getAllDeclaredMethods(clazz)) {
            TaskBean bean = method.getAnnotation(TaskBean.class);
            if (bean != null) {
                registerFetcher(service, method, bean.value());
            }
        }
    }

    /**
     * 优先使用@Resource方式注入，此处为预留接口,也可获取Fetcher Bean
     *
     * @param beanName
     *
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public <T> T bean(String beanName) {
        T bean = (T) container.get(beanName);
        if (bean != null) {
            return bean;
        } else {
            return (T) TaskBeanContainer.getBean(beanName);
        }
    }

    public Taskable<?> task(String beanName) {
        return bean(beanName);
    }

    /**
     * 注册一个Fetcher
     *
     * @param service
     * @param method
     * @param beanName
     */
    private static void registerFetcher(final Object service, final Method method, final String beanName) {
        if (TaskBeanContainer.containsBean(beanName)) {
            throw new TaskBizException("Fetcher bean duplicate for Spring:" + beanName);
        }

        final int paramLen = method.getGenericParameterTypes().length;
        Taskable<?> fetcher = TaskBeanHelper.newFetcher(service, method, beanName, paramLen);

        BeanFactory factory = applicationContext.getAutowireCapableBeanFactory();

        if (factory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory defaultFactory = (DefaultListableBeanFactory) factory;
            defaultFactory.registerSingleton(beanName, fetcher);
            // GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            // beanDefinition.setBeanClass(task.getClass());
            // listFactory.registerBeanDefinition(beanName, beanDefinition);
            LOG.info("DefaultListableBeanFactory Fetcher register: " + beanName);
        } else if (factory instanceof AbstractBeanFactory) {
            AbstractBeanFactory abstFactory = (AbstractBeanFactory) factory;
            abstFactory.registerSingleton(beanName, fetcher);
            LOG.info("AbstractBeanFactory Fetcher register: " + beanName);
        } else {
            container.put(beanName, fetcher);
            LOG.info("LocalContainer Fetcher register: " + beanName);
        }
    }

    /**
     * 显式注册TaskBean
     *
     * @param service
     * @param beanName
     * @param funcName
     * @param paramsType
     */
    public static void registerTaskBean(Object service, String beanName, String funcName, Class<?>... paramsType) {
        Method method = null;
        try {
            method = service.getClass().getMethod(funcName, paramsType);
        } catch (NoSuchMethodException e) {
            LOG.error("register TaskBean fail:", e);
        }
        registerFetcher(service, method, beanName);
    }

    /**
     * spring bean是否存在
     *
     * @param name
     *
     * @return spring bean是否存在
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 获取spring bean
     *
     * @param name
     *
     * @return spring bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 设置spring上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TaskBeanContainer.applicationContext = applicationContext;
        String newValue = String.valueOf(applicationContext.hashCode());
        LOG.info("fetcherBean container id:" + newValue);
        // 不同的Spring Context Refreshing，允许重新初始化，此处不会有并发
        if (!springContainerInstanceFlag.equals(newValue)) {
            hasInit = new CountDownLatch(1);
            initing.set(false);
            initFetcherContainer();
            springContainerInstanceFlag = newValue;
        }
    }

    /**
     * 设置spring构建优先级
     */
    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
