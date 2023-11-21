package org.astemir.uniblend.core.recipe;

import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.misc.ItemComponent;
import org.bukkit.inventory.ItemStack;

public class CraftComponent {

    public static final UJsonDeserializer<CraftComponent> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        return new CraftComponent(
                USerialization.getInt(jsonObject,"index",0),
                USerialization.get(jsonObject,"item", ItemComponent.class),
                USerialization.getInt(jsonObject,"amount",1),
                USerialization.getBoolean(jsonObject,"vanilla",false)
        );
    };

    private int index;
    private ItemComponent item;
    private int amount;
    private boolean vanilla;

    public CraftComponent(int index, ItemComponent item, int amount,boolean vanilla) {
        this.index = index;
        this.item = item;
        this.amount = amount;
        this.vanilla = vanilla;
    }

    public int getIndex() {
        return index;
    }

    public int getAmount() {
        return amount;
    }

    public ItemComponent getItemComponent() {
        return item;
    }

    public boolean test(ItemStack itemStack) {
        return getItemComponent().test(itemStack,vanilla) && itemStack.getAmount() >= amount;
    }
}
