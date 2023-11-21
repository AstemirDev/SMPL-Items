package org.astemir.uniblend.utils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                allFields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return allFields;
    }

    public static <T> T getValue(Object instance,Field field){
        try {
            field.setAccessible(true);
            return (T)field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object instance,Field field,Object value){
        try {
            field.setAccessible(true);
            field.set(instance,value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

    public static <T> T newInstance(Class<T> className){
        try {
            return className.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
