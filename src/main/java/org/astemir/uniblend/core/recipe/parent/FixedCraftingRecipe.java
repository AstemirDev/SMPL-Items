package org.astemir.uniblend.core.recipe.parent;

import org.astemir.uniblend.core.recipe.CraftComponent;
import org.astemir.uniblend.io.json.LoadType;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.gui.GuiCrafting;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FixedCraftingRecipe extends UCraftingRecipe {

    @Property(value = "components",load = LoadType.LIST,type = CraftComponent.class)
    private List<CraftComponent> components;

    @Override
    public boolean test(GuiCrafting gui) {
        for (int i = 0; i < gui.getCraftingSlots().size();i++) {
            if (hasComponentWithIndex(i)) {
                UGuiSlot craftingSlot = gui.getCraftingSlot(i);
                ItemStack itemStack = craftingSlot.getSlotItemContent(gui);
                if (itemStack != null){
                    if (!components.get(i).test(itemStack)) {
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void consumeRequired(GuiCrafting gui) {
        for (CraftComponent component : components) {
            UGuiSlot slot = gui.getCraftingSlot(component.getIndex());
            gui.consumeItem(slot.getIndex(),component.getAmount());
        }
    }

    public CraftComponent getComponentByIndex(int index){
        for (CraftComponent component : components) {
            if (component.getIndex() == index){
                return component;
            }
        }
        return null;
    }

    public boolean hasComponentWithIndex(int index){
        for (CraftComponent component : components) {
            if (component.getIndex() == index){
                return true;
            }
        }
        return false;
    }
}
