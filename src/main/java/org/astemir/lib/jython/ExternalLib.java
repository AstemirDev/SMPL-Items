package org.astemir.lib.jython;

import org.astemir.uniblend.UniblendCorePlugin;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExternalLib {
    private Map<String,Class<?>> classes;
    private String name;
    private boolean isLoaded = false;

    public ExternalLib(String name, String... classes) {
        this.name = name;
        this.load(classes);
    }

    protected Object newInstance(Constructor constructor,Object... values){
        if (isLoaded) {
            try {
                return constructor.newInstance(values);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException(name+" not loaded.");
    }

    protected Object invokeMethod(Object obj,Method method,Object... values){
        if (isLoaded) {
            if (obj != null) {
                try {
                    return method.invoke(obj, values);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return null;
            }
        }
        throw new RuntimeException(name+" not loaded.");
    }

    protected Object invokeStaticMethod(Method method,Object... values){
        if (isLoaded) {
            try {
                return method.invoke(null, values);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException(name+" not loaded.");
    }

    protected Method getMethod(String name,String methodName,ClassParam... parameters){
        if (isLoaded) {
            try {
                return classes.get(name).getMethod(methodName, convertParams(parameters));
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return null;
    }

    protected Constructor getConstructor(String name,ClassParam... parameters){
        if (isLoaded) {
            try {
                return classes.get(name).getConstructor(convertParams(parameters));
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        throw new RuntimeException(name+" not loaded.");
    }

    private Class<?>[] convertParams(ClassParam... params){
        List<Class<?>> list = new ArrayList<>();
        for (ClassParam param : params) {
            if (param.getClassName() != null){
                list.add(param.getClassName());
            }else
            if (param.getClassNameStr() != null){
                list.add(classes.get(param.getClassNameStr()));
            }
        }
        return list.toArray(new Class[params.length]);
    }

    public void load(String... classes){
        Map<String,Class<?>> classMap = new HashMap<>();
        URL[] urls;
        try {
            File file = new File(UniblendCorePlugin.getPlugin().getDataFolder().getAbsolutePath()+"/lib/"+name+".jar");
            if (file.exists()){
                isLoaded = true;
            }
            urls = new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (isLoaded) {
            ClassLoader loader = new URLClassLoader(urls, UniblendCorePlugin.class.getClassLoader());
            try {
                for (String className : classes) {
                    classMap.put(className, loader.loadClass(className));
                }
                this.classes = classMap;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public Class getClass(String name){
        return classes.get(name);
    }

    public Map<String, Class<?>> getClasses() {
        return classes;
    }
}
