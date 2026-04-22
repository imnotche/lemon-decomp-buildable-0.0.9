// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.lang.reflect.Method;
import java.net.URL;
import java.io.File;
import java.net.URLClassLoader;

public class ReflectionUtil
{
    public static void addToClassPath(final URLClassLoader classLoader, final File file) throws Exception {
        final URL url = file.toURI().toURL();
        addToClassPath(classLoader, url);
    }
    
    public static void addToClassPath(final URLClassLoader classLoader, final URL url) throws Exception {
        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }
    
    public static void iterateSuperClasses(Class<?> clazz, final Consumer<Class<?>> consumer) {
        while (clazz != Object.class) {
            consumer.accept(clazz);
            clazz = clazz.getSuperclass();
        }
    }
    
    public static <T> T getField(final Class<?> clazz, final Object instance, final int index) {
        try {
            final Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            return (T)field.get(instance);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void setField(final Class<?> clazz, final Object instance, final int index, final Object value) {
        try {
            final Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            field.set(instance, value);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Field getField(final Class<?> clazz, final String... mappings) throws NoSuchFieldException {
        final int length = mappings.length;
        int i = 0;
        while (i < length) {
            final String s = mappings[i];
            try {
                return clazz.getDeclaredField(s);
            }
            catch (final NoSuchFieldException ex) {
                ++i;
                continue;
            }
        }
        throw new NoSuchFieldException("No Such field: " + clazz.getName() + "-> " + Arrays.toString(mappings));
    }
    
    public static Method getMethodNoParameters(final Class<?> clazz, final String... mappings) {
        final int length = mappings.length;
        int i = 0;
        while (i < length) {
            final String s = mappings[i];
            try {
                return clazz.getDeclaredMethod(s, new Class[0]);
            }
            catch (final NoSuchMethodException ex) {
                ++i;
                continue;
            }
        }
        throw new RuntimeException("Couldn't find: " + Arrays.toString(mappings));
    }
    
    public static Method getMethod(final Class<?> clazz, final String notch, final String searge, final String mcp, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(searge, parameterTypes);
        }
        catch (final NoSuchMethodException e) {
            try {
                return clazz.getMethod(notch, parameterTypes);
            }
            catch (final NoSuchMethodException ex) {
                try {
                    return clazz.getMethod(mcp, parameterTypes);
                }
                catch (final NoSuchMethodException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
    }
    
    public static String getSimpleName(final String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
