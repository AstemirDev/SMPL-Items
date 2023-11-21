package org.astemir.lib.jython;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LibObject {

    private String className;
    private Map<String, Constructor> constructors = new HashMap<>();
    private Map<String, Method> methods = new HashMap<>();
    private ExternalLib lib;

    public LibObject(ExternalLib lib, String className) {
        this.lib = lib;
        this.className = className;
    }

    public LibObject constructor(String name,ClassParam... params){
        constructors.put(name,lib.getConstructor(className,params));
        return this;
    }

    public LibObject method(String name,ClassParam... params){
        methods.put(name,lib.getMethod(className,name,params));
        return this;
    }

    public LibObject method(String name,String methodName,ClassParam... params){
        methods.put(name, lib.getMethod(className, methodName, params));
        return this;
    }

    public Object newInstance(String constructor,Object... values){
        return lib.newInstance(constructors.get(constructor),values);
    }

    public Object invokeMethod(Object obj,String methodName,Object... values){
        Method method = methods.get(methodName);
        if (method != null) {
            return lib.invokeMethod(obj, method, values);
        }else{
            return null;
        }
    }

    public Method getMethod(String name){
        return methods.get(name);
    }

    public String getClassName() {
        return className;
    }

    public ExternalLib getLib() {
        return lib;
    }

    public Map<String, Constructor> getConstructors() {
        return constructors;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }
}
