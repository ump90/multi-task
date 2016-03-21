package com.baidu.unbiz.multitask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 标记为该注解的service类会被扫描方法上是否有TaskBean注解，
 * 若有则将相应方法wrap成一个spring的bean供使用
 *
 * @author wangchongjie
 * @since 2015-7-3 下午3:53:47
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface TaskService {
}
