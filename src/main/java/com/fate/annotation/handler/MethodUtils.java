package com.fate.annotation.handler;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by ydc on 2019/12/10.
 */
public final class MethodUtils {

    private MethodUtils() {}

    public static <T extends Annotation> void doMethodAnnotation(Class<?> targetClass, Class<T> annotationClass, MetadataTraverse<T> metadataTraverse) {
        doMethods(targetClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                if (method.isAnnotationPresent(annotationClass)) {
                    try {
                        T t = method.getAnnotation(annotationClass);
                        metadataTraverse.inspect(method, t);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        });
    }

    public static void doMethods(Class<?> targetType, ReflectionUtils.MethodCallback mc) {
        LinkedHashSet<Class> handlerTypes = new LinkedHashSet<>();
        Class specificHandlerType;
        if (!Proxy.isProxyClass(targetType)) {
            specificHandlerType = ClassUtils.getUserClass(targetType);
            handlerTypes.add(specificHandlerType);
        }
        handlerTypes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetType));
        Iterator iterator = handlerTypes.iterator();
        while (iterator.hasNext()) {
            Class currentHandlerType = (Class) iterator.next();
            ReflectionUtils.doWithMethods(currentHandlerType, mc, ReflectionUtils.USER_DECLARED_METHODS);
        }
    }

    public interface MetadataTraverse<T> {
        void inspect(Method method, T t);
    }
}
