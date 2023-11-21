package org.astemir.lib.jython;



import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.io.PluginFile;


public class JythonScript {
    private String textContent;
    private String path;
    private final JythonLib.Interpreter interpreter;
    public JythonScript(String path, boolean preload) {
        this.path = path;
        this.textContent = new PluginFile(UniblendCorePlugin.getPlugin(), path, preload).content();
        this.interpreter = JythonLib.interpreter();
        this.execute();
    }

    public void execute(){
        interpreter.exec(textContent);
    }

    public boolean contains(String name){
        return interpreter.contains(name);
    }

    public Object runFunction(String name,Object... args){
        JythonLib.Function func = interpreter.getFunction(name,args);
        return func.run();
    }

    public String getPackagePath(){
        int lastSlashIndex = path.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            return path.substring(0, lastSlashIndex);
        }
        return "";
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }

}
