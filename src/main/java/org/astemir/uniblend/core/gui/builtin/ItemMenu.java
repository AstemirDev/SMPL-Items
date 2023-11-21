package org.astemir.uniblend.core.gui.builtin;

import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.slot.UGuiSlot;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.io.json.Property;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;


public class ItemMenu extends UGui {
    @Property("next-page")
    private UGuiSlot nextPage;
    @Property("previous-page")
    private UGuiSlot previousPage;
    @Property("max-items")
    private int maxItems = 18;
    private int page = 0;
    private int maxPage;

    @Override
    public void onCreate() {
        List<UItem> list = UniblendItems.INSTANCE.getEntries();
        maxPage = (list.size() - 1) / maxItems;
        setupItems(0);
        lock();
    }

    private void setupItems(int page){
        clearSlots();
        List<UItem> list = UniblendItems.INSTANCE.getEntries();
        int maxIndex = Math.min((page * maxItems) + (maxItems-1), list.size() - 1);
        for (int i = page * maxItems; i <= maxIndex; i++) {
            UGuiSlot slot = new UGuiSlot();
            slot.setItemStack(list.get(i).toItemStack());
            slot.setIndex(9 + (i - page * maxItems));
            addSlot(slot.setOnClick((gui, player, click) -> {
                if (click.getCursorItem() == null || click.getCursorItem().getType() == Material.AIR) {
                    click.setCursorItem(slot.getItemStack());
                } else {
                    click.setCursorItem(null);
                }
            }));
        }
        addSlot(nextPage);
        addSlot(previousPage);
        updateContent(false);
    }

    public int getPage() {
        return page;
    }

    public boolean nextPage(){
        if (getPage() + 1 <= maxPage){
            page+=1;
            return true;
        }
        return false;
    }

    public boolean previousPage(){
        if (getPage() - 1 >= 0) {
            page -= 1;
            return true;
        }
        return false;
    }

    public static class NextPage extends UGuiSlot {
        @Override
        public void onCreate() {
            super.onCreate();
            setOnClick((gui, player, click) -> {
                if (gui instanceof ItemMenu itemMenu) {
                    gui.playSound(Sound.UI_BUTTON_CLICK, 1, 1);
                    if (itemMenu.nextPage()) {
                        itemMenu.setupItems(itemMenu.getPage());
                    }
                }
            });
        }
    }

    public static class PreviousPage extends UGuiSlot {
        @Override
        public void onCreate() {
            super.onCreate();
            setOnClick((gui, player, click) -> {
                if (gui instanceof ItemMenu itemMenu) {
                    gui.playSound(Sound.UI_BUTTON_CLICK, 1, 1);
                    if (itemMenu.previousPage()) {
                        itemMenu.setupItems(itemMenu.getPage());
                    }
                }
            });
        }
    }
}
