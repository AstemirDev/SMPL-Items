package org.astemir.uniblend.core.gui.builtin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.astemir.uniblend.UniblendCorePlugin;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.parent.UItemSocketGem;
import org.astemir.uniblend.core.hud.FadeUtils;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.misc.SoundInstance;
import org.astemir.uniblend.utils.PlayerUtils;
import org.astemir.uniblend.utils.RandomUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class IncrustingTable extends UGui {
    @Property("gem-slot")
    private UGuiSlot gemSlot;
    @Property("item-slot")
    private UGuiSlot itemSlot;
    @Property(value = "result-slot")
    private UGuiSlot resultSlot;
    @Property(value = "incrust-button")
    private UGuiSlot incrustButton;
    @Property("success-chance-text")
    private Component successChanceText;
    @Override
    public void onCreate() {
        lock();
        slots(incrustButton,gemSlot,itemSlot,resultSlot);
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        Pair<Integer,ItemStack> result = getResult();
        if (result != null) {
            ItemStack resultStack = result.getValue();
            ItemMeta meta = resultStack.getItemMeta();
            if (!meta.hasDisplayName()){
                meta.displayName(TextUtils.translate(resultStack.getType().translationKey(),NamedTextColor.WHITE));
            }
            List<Component> lore = meta.lore();
            if (lore == null){
                lore = new ArrayList<>();
            }
            lore.add(Component.empty());
            lore.add(successChanceText.append(Component.text(": "+result.getKey()+"%")));
            meta.lore(lore);
            resultStack.setItemMeta(meta);
            resultStack.setType(Material.PAPER);
            resultStack.setCustomModelData(4);
            setItem(incrustButton.getIndex(), resultStack);
        }else{
            setItem(incrustButton.getIndex(), null);
        }
    }

    public Pair<Integer,ItemStack> getResult(){
        ItemStack itemStack = getItemStack(itemSlot.getIndex());
        ItemStack gemStack = getItemStack(gemSlot.getIndex());
        if (UItemSocketGem.isGem(itemStack) &&  UItemSocketGem.isGem(gemStack)){
            UItemSocketGem gemA = (UItemSocketGem) UniblendItems.getItem(itemStack);
            UItemSocketGem gemB = (UItemSocketGem) UniblendItems.getItem(gemStack);
            int levelA = UItemSocketGem.getGemLevel(itemStack);
            int levelB = UItemSocketGem.getGemLevel(gemStack);
            if (gemA.getNameKey().equals(gemB.getNameKey()) && levelA == levelB){
                if (levelA+1 <= gemA.getLevelCap()) {
                    ItemStack copy = itemStack.clone();
                    UItemSocketGem.setGemLevel(copy, levelA + 1);
                    ItemMeta meta = copy.getItemMeta();
                    meta.lore(UItemSocketGem.gemLore(gemA, copy));
                    copy.setItemMeta(meta);
                    return Pair.of(gemA.getChance(levelA), copy);
                }else{
                    return null;
                }
            }
        }else
        if (itemStack != null) {
            if (UItemSocketGem.isGem(gemStack) && !UItemSocketGem.hasGem(itemStack)) {
                UItemSocketGem gem = (UItemSocketGem) UniblendItems.getItem(gemStack);
                ItemStack copy = itemStack.clone();
                ItemMeta meta = copy.getItemMeta();
                List<Component> list = meta.lore();
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.addAll(0, UItemSocketGem.gemAttributesLore(gem, gemStack));
                meta.lore(list);
                copy.setItemMeta(meta);
                UItemSocketGem.setItemGem(copy, gemStack);
                return Pair.of(gem.getChance(UItemSocketGem.getGemLevel(gemStack)),copy);
            }
        }
        return null;
    }

    public UGuiSlot getResultSlot() {
        return resultSlot;
    }

    public UGuiSlot getIncrustButton() {
        return incrustButton;
    }

    public UGuiSlot getGemSlot() {
        return gemSlot;
    }

    public UGuiSlot getItemSlot() {
        return itemSlot;
    }

    public static class IncrustButton extends UGuiSlot{

        @Property("click-sound")
        private SoundInstance clickSound = new SoundInstance(Sound.BLOCK_ANVIL_DESTROY,1,2);
        @Property("success-sound")
        private SoundInstance successSound = new SoundInstance(Sound.BLOCK_ANVIL_USE,1,2);
        @Property("fail-sound")
        private SoundInstance failSound = new SoundInstance(Sound.BLOCK_ANVIL_DESTROY,1,2);
        private boolean clickable = true;

        @Override
        public void onCreate() {
            super.onCreate();
            setOnClick((gui,player,click)->{
                clickSound.play(player.getLocation());
                if (clickable) {
                    IncrustingTable incrustingTable = (IncrustingTable)gui;
                    Pair<Integer,ItemStack> result = incrustingTable.getResult();
                    if (result != null) {
                        clickable = false;
                        incrustingTable.setItem(incrustingTable.getItemSlot().getIndex(),null);
                        incrustingTable.setItem(incrustingTable.getGemSlot().getIndex(),null);
                        boolean success = RandomUtils.doWithChance(result.getKey());
                        FadeUtils.show(player, NamedTextColor.BLACK, 400, 100, 0);
                        Bukkit.getScheduler().runTaskLater(UniblendCorePlugin.getPlugin(), () -> {
                            if (!gui.isDisposed()) {
                                clickable = true;
                                if (success) {
                                    successSound.play(player.getLocation());
                                    incrustingTable.setItem(incrustingTable.getResultSlot().getIndex(),result.getValue());
                                    FadeUtils.show(player, NamedTextColor.DARK_GREEN, 0, 0, 400);
                                } else {
                                    failSound.play(player.getLocation());
                                    FadeUtils.show(player, NamedTextColor.DARK_RED, 0, 0, 400);
                                }
                            }else{
                                if (success) {
                                    PlayerUtils.itemGive(player, result.getValue());
                                }
                            }
                        }, 10);
                    }
                }
            });
        }
    }

    public static class IncrustingResultSlot extends UGuiSlot {
        @Override
        public void onCreate() {
            super.onCreate();
            accessible().setOnClick((gui,player,click)->{
                if (!click.isAction(InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_ONE)) {
                    click.cancel();
                }
            }).setOnDrag((gui,player,click)->click.cancel());
        }
    }
}
