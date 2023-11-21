package org.astemir.uniblend.io.json;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.gui.builtin.IncrustingTable;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.utils.ArrayUtils;
import org.astemir.uniblend.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.checkerframework.checker.units.qual.K;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

public class PropertyHolder {
    private JsonObject properties;

    public void loadAll(){
        for (Field field : ReflectionUtils.getAllFields(getClass())) {
            Property property = field.getAnnotation(Property.class);
            Class<?> loadType = field.getType();
            if (property != null){
                if (property.type() != Class.class){
                    loadType = property.type();
                }
                if (property.load() != LoadType.CUSTOM) {
                    if (!property.value().isEmpty()) {
                        Object value = null;
                        switch (property.load()) {
                            case DEFAULT -> value = loadProperty(property.value(), loadType);
                            case ENUM -> value = loadPropertyEnum(property.value(), loadType);
                            case ARRAY -> value = loadPropertyArray(property.value(), loadType);
                            case LIST -> value = loadPropertyList(property.value(), loadType);
                            case MAP -> value = loadPropertyMap(property.value(), loadType);
                        }
                        if (value != null){
                            ReflectionUtils.setFieldValue(this, field,value);
                        }
                    }
                }
            }
        }
    }

    public <K> K[] loadPropertyArray(String name, Class<K> elementClass){
        K[] result = (K[]) Array.newInstance(elementClass,0);
        JsonArray array = properties.getAsJsonArray(name);
        if (array != null) {
            for (JsonElement jsonElement : array) {
                result = ArrayUtils.add(result, USerialization.as(jsonElement, elementClass));
            }
        }
        return result;
    }

    public <K> List<K> loadPropertyList(String name, Class<K> elementClass) {
        List<K> result = new ArrayList<>();
        JsonArray array = properties.getAsJsonArray(name);
        if (array != null) {
            for (JsonElement jsonElement : array) {
                result.add(USerialization.as(jsonElement, elementClass));
            }
        }
        return result;
    }

    public <K> Map<String,K> loadPropertyMap(String name, Class<K> elementClass){
        Map<String,K> result = new HashMap<>();
        JsonObject map = properties.getAsJsonObject(name);
        if (map != null) {
            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                result.put(entry.getKey(), USerialization.as(entry.getValue(), elementClass));
            }
        }
        return result;
    }

    public <K> List<K> loadPropertyList(String name, Class<K> elementClass,List<K> defaultList){
        if (properties.has(name)) {
            return loadPropertyList(name,elementClass);
        }else{
            return defaultList;
        }
    }

    public <K> K[] loadPropertyArray(String name, Class<K> elementClass,K[] defaultArray){
        if (properties.has(name)) {
            return loadPropertyArray(name,elementClass);
        }else{
            return defaultArray;
        }
    }

    public <K> Map<String,K> loadPropertyMap(String name, Class<K> elementClass, Map<String,K> defaultMap){
        if (properties.has(name)) {
            return loadPropertyMap(name, elementClass);
        }else{
            return defaultMap;
        }
    }

    public <K> K loadPropertyEnum(String name,Class<K> className){
        JsonElement jsonElement = properties.get(name);
        if (jsonElement != null) {
            return (K) ReflectionUtils.searchEnum((Class<? extends Enum>)className,jsonElement.getAsString());
        }else{
            return null;
        }
    }

    public <K> K loadProperty(String name,Class<K> className){
        JsonElement jsonElement = properties.get(name);
        if (jsonElement != null) {
            return USerialization.as(jsonElement,className);
        }else{
            return null;
        }
    }

    public <K> K loadProperty(String name,Class<K> className,K defaultValue){
        if (properties.has(name)){
            return loadProperty(name,className);
        }else{
            return defaultValue;
        }
    }

    public <K> K loadPropertyEnum(String name,Class<K> className,K defaultValue){
        if (properties.has(name)){
            return loadPropertyEnum(name,className);
        }else{
            return defaultValue;
        }
    }

    public <K,R> R loadPropertyFunc(String name, Class<K> className, Function<K,R> function){
        return function.apply(loadProperty(name,className));
    }

    public <K,R> R loadPropertyFunc(String name, Class<K> className, Function<K,R> function,R defaultValue){
        if (properties.has(name)) {
            return function.apply(loadProperty(name,className));
        }else{
            return defaultValue;
        }
    }

    public boolean hasProperty(String name){
        return properties.has(name);
    }

    public boolean hasAnyProperty(String... names){
        for (String name : names) {
            if (hasProperty(name)){
                return true;
            }
        }
        return false;
    }

    public void setProperties(JsonObject properties) {
        this.properties = properties;
    }

    public <T extends PropertyHolder> T create(){
        return (T) PropertyHolder.newInstance(getClass(),properties);
    }

    public static <T extends PropertyHolder> T newInstance(Class<? extends T> instanceClass, JsonObject jsonObject){
        try {
            T t = instanceClass.newInstance();
            t.setProperties(jsonObject);
            t.loadAll();
            t.onCreate();
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onCreate(){}
}
