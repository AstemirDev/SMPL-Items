package org.astemir.uniblend.core.attribute;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

public class UItemAttribute implements Named {


    public static final UJsonDeserializer<UItemAttribute> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            return new UItemAttribute(USerialization.get(jsonObject, "attribute", Attribute.class), USerialization.as(jsonObject,AttributeModifier.class));
        }else{
            return UniblendAttributes.INSTANCE.getEntry(json.getAsString());
        }
    };

    private Attribute attribute;
    private AttributeModifier modifier;
    private String key;

    public UItemAttribute(Attribute attribute, AttributeModifier modifier) {
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public AttributeModifier getModifier() {
        return modifier;
    }

    @Override
    public String getNameKey() {
        return key;
    }

    @Override
    public void setNameKey(String key) {
        this.key = key;
    }
}
