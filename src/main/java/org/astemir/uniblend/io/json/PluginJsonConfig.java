package org.astemir.uniblend.io.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.astemir.uniblend.io.PluginFile;
import org.bukkit.plugin.Plugin;
import org.python.antlr.ast.Str;

public class PluginJsonConfig extends PluginFile {

    public PluginJsonConfig(Plugin plugin, String name, boolean preloadDefault) {
        super(plugin,name,preloadDefault);
    }
    public JsonElement get(String path){
        if (path.contains("/")) {
            String[] members = path.split("/");
            if (members.length > 0) {
                JsonElement result = json().get(members[0]);
                for (int i = 1; i < members.length; i++) {
                    if (result.isJsonObject()) {
                        result = result.getAsJsonObject().get(members[i]);
                    }
                }
                return result;
            }
        }
        return json().get(path);
    }

    public <T> T getAs(String path,Class<T> className){
        return USerialization.as(get(path),className);
    }

    public JsonArray getJsonArray(String path){
        return get(path).getAsJsonArray();
    }

    public JsonObject getJsonObject(String path){
        return get(path).getAsJsonObject();
    }

    public JsonObject json(){
        return USerialization.GSON.fromJson(content(),JsonObject.class);
    }
}
