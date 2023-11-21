package org.astemir.lib.jython;

public interface Scriptable {

    default <T> T runFunc(String function, T defaultValue, Object... args){
        JythonScript script = getScript();
        if (script != null){
            if (script.contains(function)) {
                Object[] newArgs = new Object[args.length];
                for (int i = 0; i < newArgs.length; i++) {
                    newArgs[i] = JythonLib.toPyObject(args[i]);
                }
                if (defaultValue != null) {
                    return (T) JythonLib.toJavaObject(script.runFunction(function, newArgs), defaultValue.getClass());
                }else{
                    script.runFunction(function, newArgs);
                }
            }
        }
        return defaultValue;
    }

    JythonScript getScript();
    void setScript(JythonScript script);

}
