package org.astemir.uniblend.core.recipe.parent;

import org.astemir.lib.ia.ItemsAdderLib;
import org.astemir.uniblend.core.gui.GuiCrafting;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.core.recipe.CraftComponent;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class UpgradeCraftingRecipe extends UCraftingRecipe {

    @Property("upgradable-component")
    private CraftComponent upgradableComponent;
    @Property("upgrade-component")
    private CraftComponent upgradeComponent;

    @Override
    public boolean test(GuiCrafting gui) {
        return checkItem(gui,upgradableComponent) && checkItem(gui,upgradeComponent);
    }

    @Override
    public ItemStack getResult(GuiCrafting gui) {
        ItemStack upgradable = gui.getCrafItemStack(upgradableComponent.getIndex());
        if (upgradable != null) {
            ItemStack result = super.getResult(gui);
            result = ItemUtils.setEnchantments(result,upgradable.getEnchantments());
            if (upgradable.getItemMeta() instanceof Damageable damageable) {
                if (ItemsAdderLib.isCustom(upgradable)) {
                    result = ItemsAdderLib.copyDurability(upgradable, result);
                }else {
                    result = ItemsAdderLib.setDurability(result, damageable.getDamage());
                }
                result.setDamage(upgradable.getDamage());
            }
            return result;
        }
        return null;
    }

    private static boolean checkItem(GuiCrafting gui,CraftComponent component){
        UGuiSlot craftingSlot = gui.getCraftingSlot(component.getIndex());
        ItemStack itemStack = craftingSlot.getSlotItemContent(gui);
        if (itemStack != null){
            if (!component.test(itemStack)) {
                return false;
            }
        }else{
            return false;
        }
        return true;
    }


    @Override
    public void consumeRequired(GuiCrafting gui) {
        gui.consumeItem(gui.getCraftingSlot(upgradableComponent.getIndex()).getIndex(),upgradableComponent.getAmount());
        gui.consumeItem(gui.getCraftingSlot(upgradeComponent.getIndex()).getIndex(),upgradeComponent.getAmount());
    }

}
