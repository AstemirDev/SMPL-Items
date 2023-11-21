package org.astemir.uniblend.core.item.parent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.event.EventExecutionResult;
import org.astemir.uniblend.io.TextComponentMap;
import org.astemir.uniblend.io.json.LoadType;
import org.astemir.uniblend.io.json.Property;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.event.PlayerClickEvent;
import org.astemir.uniblend.misc.Pair;
import org.astemir.uniblend.utils.NBTUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;


public class UItemSocketGem extends UItem{
    @Property("style")
    private TextComponentMap style;
    @Property("gem-level-cap")
    private int levelCap = 99;
    @Property("gem-chance")
    private int chance = 50;
    @Property(load = LoadType.CUSTOM)
    private List<GemAttribute> gemAttributes;

    @Override
    public void onCreate() {
        super.onCreate();
        gemAttributes = loadPropertyFunc("gem-attributes", JsonArray.class,(jsonArray)->{
            LinkedList<GemAttribute> result = new LinkedList<>();
            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement.isJsonObject()){
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    GemAttributeType type = USerialization.getEnum(jsonObject,"type", GemAttributeType.class);
                    GemAttributeOperation operation = USerialization.getEnum(jsonObject,"operation", GemAttributeOperation.class,GemAttributeOperation.NONE);
                    double value = USerialization.getDouble(jsonObject,"value",1);
                    Component description = USerialization.get(jsonObject,"description", Component.class);
                    result.add(new GemAttribute(type,operation,value,description));
                }
            }
            return result;
        });
    }

    @Override
    public ItemStack toItemStack() {
        ItemStack itemStack = super.toItemStack();
        setGemLevel(itemStack,1);
        itemStack.lore(gemLore(this,itemStack));
        return itemStack;
    }

    public double applyToNumber(GemAttribute attribute, int level, double number){
        GemAttributeOperation operation = attribute.getOperation();
        switch (operation){
            case ADD -> {
                return number+attribute.calculateValue(level);
            }
            case ADD_PERCENT -> {
                return number+((number/100f)*attribute.calculateValue(level));
            }
            case SUB -> {
                return number-attribute.calculateValue(level);
            }
            case SUB_PERCENT -> {
                return number-((number/100f)*attribute.calculateValue(level));
            }
            case MULT -> {
                return number*attribute.calculateValue(level);
            }
            case MULT_PERCENT -> {
                return number*((number/100f)*attribute.calculateValue(level));
            }
            case DIV -> {
                return number/attribute.calculateValue(level);
            }
            case DIV_PERCENT -> {
                return number/((number/100f)*attribute.calculateValue(level));
            }
        }
        return number;
    }

    @Override
    public EventExecutionResult onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof HorseInventory){
            return EventExecutionResult.CANCEL;
        }
        Player player = (Player) e.getWhoClicked();
        if (player.getOpenInventory().getTopInventory() instanceof HorseInventory){
            return EventExecutionResult.CANCEL;
        }
        return super.onInventoryClick(e);
    }

    @Override
    public EventExecutionResult onRightClick(PlayerClickEvent e) {
        if (e.getClickedEntity() instanceof AbstractHorse){
            return EventExecutionResult.CANCEL;
        }
        return super.onRightClick(e);
    }


    public TextComponentMap getStyle() {
        return style;
    }
    public static void setItemGem(ItemStack itemStack,ItemStack gemStack){
        UItemSocketGem gemItem = (UItemSocketGem) UniblendItems.getItem(gemStack);
        PersistentDataContainer resultNbt = NBTUtils.nbtContainer(itemStack.getItemMeta());
        resultNbt = NBTUtils.set(resultNbt,"id",gemItem.getNameKey());
        resultNbt = NBTUtils.set(resultNbt,"level",getGemLevel(gemStack));
        itemStack.setItemMeta(NBTUtils.set(itemStack.getItemMeta(),"gem",resultNbt));
    }

    public static Pair<UItemSocketGem,Integer> getItemGem(ItemStack itemStack){
        if (itemStack != null){
            if (NBTUtils.contains(itemStack.getItemMeta(),"gem")){
                PersistentDataContainer gemContainer = NBTUtils.get(itemStack.getItemMeta(),"gem",PersistentDataContainer.class);
                String id = NBTUtils.get(gemContainer,"id",String.class);
                int level = NBTUtils.get(gemContainer,"level",Integer.class);
                return Pair.of((UItemSocketGem) UniblendItems.INSTANCE.matchEntry(id),level);
            }
        }
        return null;
    }


    public int getChance(int level){
        return chance/level;
    }

    public int getChance(ItemStack itemStack){
        return chance/getGemLevel(itemStack);
    }

    public int getLevelCap() {
        return levelCap;
    }

    public List<GemAttribute> getGemAttributes() {
        return gemAttributes;
    }

    public static void setGemLevel(ItemStack itemStack, int level){
        if (itemStack != null) {
            itemStack.setItemMeta(NBTUtils.set(itemStack.getItemMeta(), "level", level));
        }
    }

    public static int getGemLevel(ItemStack itemStack){
        if (itemStack != null) {
            return NBTUtils.get(itemStack.getItemMeta(),"level",Integer.class);
        }else{
            return 0;
        }
    }

    public static boolean isGem(ItemStack itemStack){
        return UniblendItems.isItem(itemStack, UItemSocketGem.class);
    }

    public static boolean hasGem(ItemStack itemStack){
        if (itemStack != null) {
            return NBTUtils.contains(itemStack.getItemMeta(), "gem");
        }else{
            return false;
        }
    }


    public static Component gemChanceLore(UItemSocketGem gem, ItemStack itemStack){
        return gem.getStyle().get("chance-text").append(Component.text(": "+gem.getChance(itemStack)+"%"));
    }

    public static List<Component> gemAttributesLore(UItemSocketGem gem, ItemStack itemStack){
        LinkedList<Component> lore = new LinkedList<>();
        lore.add(Component.empty().append(gem.getStyle().get("unicode")).append(TextUtils.text(" "+getGemLevel(itemStack)+" ", NamedTextColor.WHITE)).append(gem.getStyle().get("level-text")));
        for (GemAttribute attribute : gem.getGemAttributes()) {
            lore.add(attribute.buildComponent(itemStack));
        }
        return lore;
    }

    public static List<Component> gemLore(UItemSocketGem gem, ItemStack itemStack){
        List<Component> lore = gemAttributesLore(gem,itemStack);
        lore.add(0,gemChanceLore(gem,itemStack));
        return lore;
    }

    public class GemAttribute{
        private GemAttributeType type;
        private GemAttributeOperation operation;
        private Component description;
        private double value;

        public GemAttribute(GemAttributeType type, GemAttributeOperation operation, double value,Component description) {
            this.type = type;
            this.operation = operation;
            this.value = value;
            this.description = description;
        }

        public GemAttributeOperation getOperation() {
            return operation;
        }

        public GemAttributeType getType() {
            return type;
        }

        public double getValue() {
            return value;
        }

        public double calculateValue(ItemStack itemStack){
            return calculateValue(getGemLevel(itemStack));
        }

        public double calculateValue(int level){
            return value*level;
        }

        public Component buildComponent(ItemStack itemStack) {
            TextColor color = type.getColor();
            Component component = Component.empty();
            if (operation != GemAttributeOperation.NONE) {
                component = TextUtils.text(operation.getText(), color).
                        append(TextUtils.text(String.valueOf(calculateValue(itemStack)), color)).
                        append(TextUtils.text(String.valueOf(operation.getType()), color)).
                        append(Component.text(" "));
            }
            return component.append(description);
        }
    }


    public enum GemAttributeOperation{
        NONE("",""),
        ADD("+",""),
        SUB("-",""),
        ADD_PERCENT("+","%"),
        SUB_PERCENT("-","%"),
        MULT("•",""),
        DIV("÷",""),
        MULT_PERCENT("•","%"),
        DIV_PERCENT("÷","%");

        private String text;
        private String type;
        GemAttributeOperation(String text, String type) {
            this.text = text;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }
    }

    public enum GemAttributeType{
        DASH(NamedTextColor.AQUA),
        MELEE_DAMAGE(NamedTextColor.GREEN),
        RANGED_DAMAGE(NamedTextColor.GREEN),
        DODGE_CHANCE(NamedTextColor.GREEN),
        MISS_CHANCE(NamedTextColor.RED),
        MISFORTUNE_CHANCE(NamedTextColor.RED);

        private TextColor color;

        GemAttributeType(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }

    }
}
