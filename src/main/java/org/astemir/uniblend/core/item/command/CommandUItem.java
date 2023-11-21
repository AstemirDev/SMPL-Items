package org.astemir.uniblend.core.item.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.astemir.uniblend.core.command.UCommand;
import org.astemir.uniblend.core.gui.UGui;
import org.astemir.uniblend.core.gui.UniblendGuis;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CommandUItem extends UCommand {


    public CommandUItem() {
        super("uitem");
        CmdArgument ARG_GIVE = CmdArgument.arg("give");
        CmdArgument ARG_MENU = CmdArgument.arg("menu");
        CmdArgument ARG_NBT = CmdArgument.arg("nbt");
        CmdArgument ARG_ITEM_NAME = CmdArgument.strArg("item_name").autoComplete(()->itemNames());
        variant(new CmdPattern(ARG_GIVE,ARG_ITEM_NAME),(sender, values) -> {
            if (sender.isPlayer()) {
                String itemId = values.get(ARG_ITEM_NAME).getValue();
                UItem item = UniblendItems.INSTANCE.matchEntry(itemId);
                if (item != null) {
                    sender.getPlayerSender().getInventory().addItem(item.toItemStack());
                    sender.sendMessage("Вы выдали себе предмет:",itemId);
                }else{
                    sender.sendMessage("§cТакого предмета не существует.");
                }
            }else{
                sender.sendMessage("§cЭту команду могут использовать только игроки.");
            }
            return true;
        },"выдать предмет себе");
        variant(new CmdPattern(ARG_NBT,ARG_ITEM_NAME),(sender, values) -> {
            String itemId = values.get(ARG_ITEM_NAME).getValue();
            UItem item = UniblendItems.INSTANCE.matchEntry(itemId);
            if (item != null) {
                String tagText = ItemUtils.getTag(CraftItemStack.asNMSCopy(item.toItemStack())).toString();
                Component component = Component.text("§aNBT предмета: ").append(Component.translatable(item.getNameKey()));
                component = component.clickEvent(ClickEvent.copyToClipboard(tagText));
                component = component.hoverEvent(HoverEvent.showText(Component.text(tagText)));
                sender.sendMessage(component);
            }else{
                sender.sendMessage("§cТакого предмета не существует.");
            }
            return true;
        },"показать nbt предмета");
        variant(new CmdPattern(ARG_MENU),(sender,values)->{
            if (sender.isPlayer()){
                UGui gui = UniblendGuis.INSTANCE.matchEntry("item_menu").create();
                gui.show(sender.getPlayerSender());
            }
            return true;
        },"открыть меню предметов");
    }



    public List<String> itemNames(){
        List<String> list = new ArrayList<>();
        for (UItem entry : UniblendItems.INSTANCE.getEntries()) {
            list.add(entry.getNameKey());
        }
        return list;
    }

    private List<String> pages(){
        Inventory[] allItems = allItems(27);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < allItems.length; i++) {
            list.add(i+"");
        }
        return list;
    }

    private Inventory[] allItems(int maxItems) {
        List<UItem> entries = UniblendItems.INSTANCE.getEntries();
        if (entries.isEmpty()) {
            return new Inventory[0];
        }
        int size = (entries.size() + maxItems - 1) / maxItems;
        Inventory[] inventories = new Inventory[size];
        int inventoryId = 0;
        int slotId = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (i % maxItems == 0) {
                inventories[inventoryId] = Bukkit.createInventory(null, 27, "§lСтраница " + inventoryId + "/" + (size - 1));
                slotId = 0;
                inventoryId++;
            }
            inventories[inventoryId - 1].setItem(slotId, entries.get(i).toItemStack());
            slotId++;
        }
        return inventories;
    }
}
