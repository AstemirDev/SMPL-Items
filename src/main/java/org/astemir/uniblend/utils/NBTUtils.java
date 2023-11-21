package org.astemir.uniblend.utils;

import org.astemir.uniblend.UniblendCorePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class NBTUtils {
    public static NamespacedKey key(String name){
        return new NamespacedKey(UniblendCorePlugin.getPlugin(),name);
    }
    public static NamespacedKey key(String namespace,String name){
        return new NamespacedKey(namespace,name);
    }
    public static PersistentDataContainer nbtContainer(PersistentDataHolder holder){
        if (holder != null){
            if (holder.getPersistentDataContainer() != null){
                return holder.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
            }
        }
        return null;
    }

    public static <T,K extends PersistentDataHolder> K set(K holder, NamespacedKey key, T value){
        if (holder != null) {
            set(holder.getPersistentDataContainer(),key,value);
        }
        return holder;
    }

    public static <T> T get(PersistentDataHolder holder,NamespacedKey key,Class<T> className){
        if (holder != null){
            return get(holder.getPersistentDataContainer(),key,className);
        }
        return null;
    }

    public static <T> T getOr(PersistentDataHolder holder,NamespacedKey key,Class<T> className,T defaultValue){
        if (holder != null){
            return getOr(holder.getPersistentDataContainer(),key,className,defaultValue);
        }
        return null;
    }

    public static boolean contains(PersistentDataHolder holder,NamespacedKey key){
        if (holder != null){
            if (holder.getPersistentDataContainer() != null) {
                if (holder.getPersistentDataContainer().has(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T,K extends PersistentDataContainer> K set(K container, NamespacedKey key, T value){
        if (container != null){
            container.set(key,getType(value.getClass()),value);
        }
        return container;
    }

    public static <T> T get(PersistentDataContainer container,NamespacedKey key,Class<T> className){
        if (container != null){
            return (T) container.get(key,getType(className));
        }
        return null;
    }

    public static <T> T getOr(PersistentDataContainer container,NamespacedKey key,Class<T> className,T defaultValue){
        if (container != null){
            if (container.has(key)) {
                return (T) container.get(key, getType(className));
            }else{
                return defaultValue;
            }
        }
        return null;
    }

    public static boolean contains(PersistentDataContainer container,NamespacedKey key){
        if (container != null){
            if (container.has(key)) {
                return true;
            }
        }
        return false;
    }


    public static <T,K extends PersistentDataHolder> K set(K holder, String key, T value){
        if (holder != null) {
            set(holder.getPersistentDataContainer(),key,value);
        }
        return holder;
    }

    public static <T> T get(PersistentDataHolder holder,String key,Class<T> className){
        if (holder != null){
            return get(holder.getPersistentDataContainer(),key,className);
        }
        return null;
    }

    public static <T> T getOr(PersistentDataHolder holder,String key,Class<T> className,T defaultValue){
        if (holder != null){
            return getOr(holder.getPersistentDataContainer(),key,className,defaultValue);
        }
        return null;
    }

    public static boolean contains(PersistentDataHolder holder,String key){
        if (holder != null){
            NamespacedKey namespacedKey = key(key);
            if (holder.getPersistentDataContainer() != null) {
                if (holder.getPersistentDataContainer().has(namespacedKey)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static <T,K extends PersistentDataContainer> K set(K container, String key, T value){
        if (container != null){
            container.set(key(key),getType(value.getClass()),value);
        }
        return container;
    }

    public static <T> T get(PersistentDataContainer container,String key,Class<T> className){
        if (container != null){
            return (T) container.get(key(key),getType(className));
        }
        return null;
    }

    public static <T> T getOr(PersistentDataContainer container,String key,Class<T> className,T defaultValue){
        if (container != null){
            NamespacedKey namespacedKey = key(key);
            if (container.has(namespacedKey)) {
                return (T) container.get(namespacedKey, getType(className));
            }else{
                return defaultValue;
            }
        }
        return null;
    }


    public static boolean contains(PersistentDataContainer container,String key){
        if (container != null){
            NamespacedKey namespacedKey = key(key);
            if (container.has(namespacedKey)) {
                return true;
            }
        }
        return false;
    }

    public static <T> PersistentDataType getType(Class<T> className){
        if (className == int.class || className == Integer.class){
            return PersistentDataType.INTEGER;
        }else
        if (className == long.class || className == Long.class){
            return PersistentDataType.LONG;
        }else
        if (className == double.class || className == Double.class){
            return PersistentDataType.DOUBLE;
        }else
        if (className == float.class || className == Float.class){
            return PersistentDataType.FLOAT;
        }else
        if (className == boolean.class || className == Boolean.class){
            return PersistentDataType.BOOLEAN;
        }else
        if (className == byte.class || className == Byte.class){
            return PersistentDataType.BYTE;
        }else
        if (className == String.class){
            return PersistentDataType.STRING;
        }else
        if (className == int[].class || className == Integer[].class){
            return PersistentDataType.INTEGER_ARRAY;
        }else
        if (className == long[].class || className == Long[].class){
            return PersistentDataType.LONG_ARRAY;
        }else
        if (className == byte[].class || className == Byte[].class){
            return PersistentDataType.BYTE_ARRAY;
        }else
        if (className == PersistentDataContainer.class || className == CraftPersistentDataContainer.class){
            return PersistentDataType.TAG_CONTAINER;
        }else
        if (className == PersistentDataContainer[].class || className == CraftPersistentDataContainer[].class){
            return PersistentDataType.TAG_CONTAINER_ARRAY;
        }
        throw new RuntimeException("Unknown value type: "+className);
    }
}
