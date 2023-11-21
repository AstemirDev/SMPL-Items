package org.astemir.uniblend.core;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface UniblendRegistry<T> {

    T add(T instance);
    default void remove(T instance){
        getEntries().remove(instance);
    }

    default void addAll(T... instances){
        for (T instance : instances) {
            add(instance);
        }
    }
    default void clear(){
        getEntries().clear();
    }

    List<T> getEntries();


    abstract class Abstract<T> extends UniblendModule implements UniblendRegistry<T> {
        @Override
        public T add(T instance) {
            getEntries().add(instance);
            return instance;
        }

        public T register(String key,T instance){
            if (instance instanceof Named named){
                named.setNameKey(key);
            }
            return add(instance);
        }

        public T matchEntry(String key){
            if (containsEntry(key)){
                return getEntry(key);
            }
            for (T entry : getEntries()) {
                if (entry instanceof Named named){
                    if (named.getNameKey().contains(key)){
                        return entry;
                    }
                }
            }
            return null;
        }

        public T getEntry(String key){
            for (T entry : getEntries()) {
                if (entry instanceof Named named){
                    if (named.getNameKey().equals(key)){
                        return entry;
                    }
                }
            }
            return null;
        }

        public boolean containsEntry(String key){
            return getEntry(key) != null;
        }

        public boolean hasMatch(String key){ return matchEntry(key) != null;}
    }


    class Default<T> extends UniblendRegistry.Abstract<T> {
        private List<T> entries = new ArrayList<>();
        @Override
        public List<T> getEntries() {
            return entries;
        }
    }

    class Concurrent<T> extends UniblendRegistry.Abstract<T>{
        private CopyOnWriteArrayList<T> entries = new CopyOnWriteArrayList<>();
        @Override
        public List<T> getEntries() {
            return entries;
        }
    }
}