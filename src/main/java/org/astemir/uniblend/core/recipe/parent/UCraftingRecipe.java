package org.astemir.uniblend.core.recipe.parent;


import com.google.gson.JsonObject;
import org.astemir.uniblend.core.recipe.UniblendRecipes;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.io.json.PropertyHolder;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.misc.ItemComponent;
import org.astemir.uniblend.core.gui.GuiCrafting;
import org.bukkit.inventory.ItemStack;


public abstract class UCraftingRecipe extends PropertyHolder implements Named {

    public static final UJsonDeserializer<UCraftingRecipe> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("class")) {
                Class<? extends UCraftingRecipe> recipeClass = USerialization.getClass(UniblendRecipes.INSTANCE, jsonObject, "class");
                return PropertyHolder.newInstance(recipeClass, jsonObject);
            }
            return UCraftingRecipe.newInstance(FixedCraftingRecipe.class, jsonObject);
        }else{
            return UniblendRecipes.INSTANCE.getEntry(json.getAsString());
        }
    };
    @Property("result")
    private ItemComponent result;

    @Property("result-amount")
    private int resultAmount = 1;

    private String nameKey;

    public abstract boolean test(GuiCrafting gui);

    public abstract void consumeRequired(GuiCrafting gui);


    public ItemStack getResult(GuiCrafting crafting) {
        ItemStack itemStack = result.getItemStack().get();
        itemStack.setAmount(resultAmount);
        return itemStack;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public void setNameKey(String key) {
        this.nameKey = key;
    }
}