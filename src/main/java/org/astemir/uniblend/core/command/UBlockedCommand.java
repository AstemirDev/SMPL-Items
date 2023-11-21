package org.astemir.uniblend.core.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;

import java.util.ArrayList;
import java.util.List;

public class UBlockedCommand implements Named {

    public static final UJsonDeserializer<UBlockedCommand> DESERIALIZER = (json)->{
        if (json.isJsonObject()){
            JsonObject object = json.getAsJsonObject();
            List<String> aliases = new ArrayList<>();
            if (object.has("aliases")){
                for (JsonElement jsonElement : object.get("aliases").getAsJsonArray()) {
                    aliases.add(jsonElement.getAsString());
                }
            }
            return new UBlockedCommand(aliases, USerialization.get(object,"error-message", Component.class));
        }
        return null;
    };
    private String name;
    private List<String> aliases;
    private Component errorMessage;

    public UBlockedCommand(List<String> aliases, Component errorMessage) {
        this.aliases = aliases;
        this.errorMessage = errorMessage;
    }

    public Component getErrorMessage() {
        return errorMessage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isValid(String command){
        if (name.equals(command)){
            return true;
        }else{
            for (String alias : aliases) {
                if (alias.equals(command)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String getNameKey() {
        return name;
    }

    @Override
    public void setNameKey(String key) {
        this.name = key;
    }
}
