package com.baidu.unbiz.multitask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据获取类型的Bean注解， 可将method转换为Spring的bean使用
 *
 * @author wangchongjie
 * @since 2015-8-17 下午3:50:56
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskBean {

  /**
   * task bean的名称
   *
   * @return bean name
   * @since 2015-8-17
   */
  String value();
}
