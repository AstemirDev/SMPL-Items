package org.astemir.lib.jython;



import java.io.InputStream;
import java.lang.reflect.Method;

public class JythonLib extends ExternalLib {

    public static JythonLib INSTANCE;
    private Method wrapJavaObject;
    private Method wrapPyObject;

    public JythonLib() {
        super("jython","org.python.util.PythonInterpreter","org.python.core.PyObject","org.python.core.PyFunction","org.python.core.PyJavaType","org.python.core.PyType");
        INSTANCE = this;
        wrapJavaObject = getMethod("org.python.core.PyJavaType", "wrapJavaObject", ClassParam.as(Object.class));
        wrapPyObject = getMethod("org.python.core.PyObject","__tojava__",ClassParam.as(Class.class));
    }

    public static void initialize(){
        JythonLib jython = new JythonLib();
        if (jython.isLoaded()){
            interpreter().exec("print \"Jython is loaded successfully.\"");
        }
    }

    private Object wrapObject(Object object){
        return invokeStaticMethod(wrapJavaObject,object);
    }

    private Object wrapPyObject(Object pyObject,Class<?> className){
        return invokeMethod(pyObject,wrapPyObject,className);
    }

    private Interpreter createInterpreter(){
        return new JythonLib.Interpreter(this);
    }

    public static Interpreter interpreter(){
        return JythonLib.INSTANCE.createInterpreter();
    }

    public static Object toPyObject(Object object){
        return JythonLib.INSTANCE.wrapObject(object);
    }

    public static Object toJavaObject(Object object,Class<?> className){
        return JythonLib.INSTANCE.wrapPyObject(object,className);
    }

    public class Interpreter extends LibObject {

        private Object handle;

        public Interpreter(ExternalLib lib) {
            super(lib,"org.python.util.PythonInterpreter");
            constructor("default");
            method("exec",ClassParam.as(String.class));
            method("execfile",ClassParam.as(InputStream.class));
            method("getPy","get",ClassParam.as(String.class));
            method("get",ClassParam.as(String.class),ClassParam.as(Class.class));
            method("set",ClassParam.as(String.class),ClassParam.as(Object.class));
            method("getLocals");
            handle = newInstance("default");
        }

        public void set(String name,Object value){
            invokeMethod(handle,"set",name,value);
        }
        public Object get(String name){
            return invokeMethod(handle,"getPy",name);
        }

        public boolean contains(String name){
            return get(name) != null;
        }

        public Function getFunction(String name,Object... objs){
            Object object = get(name);
            if (object != null) {
                return new Function(getLib(), object, objs);
            }
            return null;
        }

        public <T> T get(String name,Class<T> className){
            return (T) invokeMethod(handle,"get",name,className);
        }

        public void exec(String value){
            invokeMethod(handle,"exec",value);
        }
        public void execfile(InputStream inputStream){
            invokeMethod(handle,"execfile",inputStream);
        }
    }

    public static class Function extends LibObject{
        private Object handle;
        private Object[] args;


        public Function(ExternalLib lib, Object handle, Object... args) {
            super(lib,"org.python.core.PyFunction");
            this.handle = handle;
            this.args = args;
            method("run","__call__");
            if (args != null && args.length > 0) {
                ClassParam[] argsClasses = new ClassParam[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsClasses[i] = new ClassParam("org.python.core.PyObject");
                }
                method("runArgs", "__call__", argsClasses);
            }
        }

        public Object run(){
            if (args == null || args.length == 0) {
                return invokeMethod(handle, "run");
            }else{
                return invokeMethod(handle,"runArgs",args);
            }
        }
    }
}
