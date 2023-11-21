package org.astemir.uniblend.core.recipe;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.recipe.parent.FixedCraftingRecipe;
import org.astemir.uniblend.core.recipe.parent.UCraftingRecipe;
import org.astemir.uniblend.core.recipe.parent.UpgradeCraftingRecipe;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;

import java.util.List;

@Registered("recipes")
public class UniblendRecipes extends UniblendRegistry.Default<UCraftingRecipe>{

    public static UniblendRecipes INSTANCE;
    public UniblendRecipes() {
        INSTANCE = this;
    }

    @Override
    public void onSetupLookups() {
        setLookup("fixed", FixedCraftingRecipe.class);
        setLookup("upgrade", UpgradeCraftingRecipe.class);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String setName : map.keySet()) {
                INSTANCE.register(setName, USerialization.deserialize(map.get(setName), UCraftingRecipe.class));
            }
        }
    }
}
