package com.baidu.unbiz.multitask.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class相关工具类
 * 
 * @author wangchongjie
 * @since 2015-7-16 下午3:02:20
 */
public class ClassUtils {

    /**
     * 获取某个类锁指定的泛型参数数组
     * 
     * @param c
     * @return type
     */
    public static final Type[] getGenericTypes(Class<?> c) {
        Type superClass = c.getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superClass;
        return type.getActualTypeArguments();
    }

    /**
     * 获取一个类的所有字段
     * 
     * @param entityClass
     * @return Field set
     */
    public static Set<Field> getAllFiled(Class<?> entityClass) {

        // 获取本类的所有字段
        Set<Field> fs = new HashSet<Field>();
        for (Field f : entityClass.getFields()) {
            fs.add(f);
        }
        for (Field f : entityClass.getDeclaredFields()) {
            fs.add(f);
        }

        // 递归获取父类的所有字段
        Class<?> superClass = entityClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Field> superFileds = getAllFiled(superClass);
            fs.addAll(superFileds);
        }

        return fs;
    }

    /**
     * 获取一个类的所有方法
     * 
     * @param entityClass
     * @return Method set
     */
    public static Set<Method> getAllMethod(Class<?> entityClass) {

        // 获取本类的所有的方法
        Set<Method> ms = new HashSet<Method>();
        for (Method m : entityClass.getMethods()) {
            ms.add(m);
        }
        for (Method m : entityClass.getDeclaredMethods()) {
            ms.add(m);
        }

        // 递归获取父类的所有方法
        Class<?> superClass = entityClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Method> superFileds = getAllMethod(superClass);
            ms.addAll(superFileds);
        }

        return ms;
    }

    /**
     * 将from的属性copy到to中
     * 
     * @param from
     * @param to
     * @since 2015-7-15 by wangchongjie
     */
    public static final void copyProperties(Object from, Object to) {

        Set<Field> fromSet = getAllFiled(from.getClass());
        Set<Field> toSet = getAllFiled(to.getClass());

        Map<String, Field> toMap = new HashMap<String, Field>();
        for (Field f : toSet) {
            toMap.put(f.getName(), f);
        }

        for (Field f : fromSet) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String name = f.getName();
            Field toField = toMap.get(name);
            if (toField == null) {
                continue;
            }

            toField.setAccessible(true);
            f.setAccessible(true);
            try {
                toField.set(to, f.get(from));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取一个类的field
     * 
     * @param field
     * @param clazz
     * @return Field
     * @since 2015-7-15 by wangchongjie
     */
    public static Field getFieldFromClass(String field, Class<? extends Object> clazz) {
        try {
            return clazz.getDeclaredField(field);
        } catch (Exception e) {
            try {
                return clazz.getField(field);
            } catch (Exception ex) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * key为clazz+fieldName,value为Field对象
     */
    private static Map<String, Field> fieldCache = new ConcurrentHashMap<String, Field>();

    /**
     * field有效并缓存
     * 
     * @param field
     * @param cacheKey
     * @return boolean
     * @since 2015-7-28 by wangchongjie
     */
    private static boolean fieldIsEffectAndCache(Field field, String cacheKey) {
        if (field != null) {
            fieldCache.put(cacheKey, field);
            return true;
        }
        return false;
    }

    /**
     * 遍历所有field（父子类、public、protected、private）
     * 
     * @param clazz
     * @param field
     * @return 2014-11-21 下午7:37:52 created by wangchongjie
     */
    public static Field getCachedFieldFromClass(String field, Class<?> clazz) {
        String cacheKey = clazz + "|" + field;
        Field result = fieldCache.get(cacheKey);
        if (result != null) {
            return result;
        }
        try {
            result = clazz.getDeclaredField(field);
        } catch (Exception e) {
            // do nothing
        }
        if (fieldIsEffectAndCache(result, cacheKey)) {
            return result;
        }
        try {
            result = clazz.getField(field);
        } catch (Exception ex) {
            // do nothing
        }
        if (fieldIsEffectAndCache(result, cacheKey)) {
            return result;
        }
        // 递归获取父类的所有字段
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            result = getCachedFieldFromClass(field, superClass);
        } else {
            return null;
        }
        if (fieldIsEffectAndCache(result, cacheKey)) {
            return result;
        }
        return null;
    }

}
