package org.astemir.uniblend.io;

import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PluginFile extends FileHandle {

    public PluginFile(Plugin plugin, String name, boolean preloadDefault) {
        super(load(plugin,name,preloadDefault));
        this.load(StandardCharsets.UTF_8);
    }

    protected static File load(Plugin plugin, String name, boolean preloadDefault){
        String subPath = "";
        String finalName = name;
        if (name.contains("/")) {
            int index = name.lastIndexOf("/");
            subPath = name.substring(0,index);
            finalName = name.substring(index);
        }
        File pluginFolder = new File(plugin.getDataFolder(),subPath);
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        File file = new File(pluginFolder,finalName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                if (preloadDefault) {
                    try {
                        InputStream inputStream = plugin.getResource(name);
                        if (inputStream != null) {
                            String defaultContent = FileUtils.readText(inputStream, Charset.defaultCharset());
                            FileUtils.writeText(file, Charset.defaultCharset(), defaultContent);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
