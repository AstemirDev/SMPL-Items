package org.astemir.lib.jython;

public class ClassParam {

    private Class<?> className;

    private String classNameStr;

    public ClassParam(Class<?> className) {
        this.className = className;
    }

    public ClassParam(String classNameStr) {
        this.classNameStr = classNameStr;
    }

    public ClassParam(LibObject libObject) {
        this.classNameStr = libObject.getClassName();
    }

    public Class<?> getClassName() {
        return className;
    }

    public String getClassNameStr() {
        return classNameStr;
    }

    public static ClassParam as(String name){
        return new ClassParam(name);
    }

    public static ClassParam as(LibObject libObject){
        return new ClassParam(libObject);
    }

    public static ClassParam as(Class<?> className){
        return new ClassParam(className);
    }
}
