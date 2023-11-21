package org.astemir.uniblend.core.gui;

import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.recipe.parent.UCraftingRecipe;
import org.astemir.uniblend.io.json.LoadType;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.SoundInstance;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GuiCrafting extends UGui {
    @Property(value = "recipes",type = UCraftingRecipe.class,load = LoadType.LIST)
    private List<UCraftingRecipe> recipes = new ArrayList<>();
    @Property(value = "crafting-slots",type = UGuiSlot.class,load = LoadType.LIST)
    private List<UGuiSlot> craftingSlots = new LinkedList<>();
    @Property(value = "crafting-sound")
    private SoundInstance soundInstance = new SoundInstance(Sound.BLOCK_ANVIL_USE,1,1);
    @Property(value = "result-slot")
    private UGuiSlot resultSlot;
    @Override
    public void onCreate() {
        lock();
        addSlot(resultSlot);
        for (UGuiSlot craftingSlot : craftingSlots) {
            addSlot(craftingSlot);
        }
    }

    public void onCraft(){
        playSound(soundInstance);
        runFunc("on_craft", this);
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        UCraftingRecipe resultRecipe = getRecipe();
        if (resultRecipe != null) {
            setItem(resultSlot.getIndex(), resultRecipe.getResult(this));
        } else {
            removeItem(resultSlot.getIndex());
        }
    }

    public UCraftingRecipe getRecipe(){
        for (UCraftingRecipe recipe : recipes) {
            if (recipe.test(this)) {
                return recipe;
            }
        }
        return null;
    }

    public void setRecipes(UCraftingRecipe... recipes) {
        this.recipes = Arrays.asList(recipes);
    }

    public ItemStack getCrafItemStack(int slot){
        return getItemStack(getCraftingSlot(slot).getIndex());
    }

    public UGuiSlot getCraftingSlot(int index){
        for (int i = 0; i < craftingSlots.size(); i++) {
            if (i == index){
                return craftingSlots.get(i);
            }
        }
        return null;
    }
    public List<UGuiSlot> getCraftingSlots() {
        return craftingSlots;
    }


    @Override
    public void onClose(Player player) {
        super.onClose(player);
        for (UGuiSlot craftingSlot : getCraftingSlots()) {
            ItemStack itemStack = getItemStack(craftingSlot.getIndex());
            if (itemStack != null) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }

    public static class ResultSlot extends UGuiSlot {
        @Override
        public void onCreate() {
            super.onCreate();
            accessible().setOnClick((gui,player,click)->{
                if (gui instanceof GuiCrafting craftingGui) {
                    if (click.isAction(InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_ONE)) {
                        UCraftingRecipe recipe = craftingGui.getRecipe();
                        if (recipe != null) {
                            recipe.consumeRequired(craftingGui);
                            craftingGui.onCraft();
                        }
                    } else {
                        click.cancel();
                    }
                }
            }).setOnDrag((gui,player,click)->click.cancel());
        }
    }
}
