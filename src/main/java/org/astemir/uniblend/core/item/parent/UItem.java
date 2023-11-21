package org.astemir.uniblend.core.item.parent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.core.item.UniblendItem;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.io.json.*;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.core.attribute.UItemAttribute;
import org.astemir.lib.jython.JythonScript;
import org.astemir.uniblend.misc.RandomizedColor;
import org.astemir.uniblend.utils.NBTUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.codehaus.plexus.util.Base64;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class UItem extends PropertyHolder implements UniblendItem, Named {
    public static final DecimalFormat DEFAULT_DOUBLE_FORMAT = new DecimalFormat("###.#",new DecimalFormatSymbols(Locale.ENGLISH));

    public static final UJsonDeserializer<UItem> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("class")) {
                Class<? extends UItem> itemClass = USerialization.getClass(UniblendItems.INSTANCE, jsonObject, "class");
                return PropertyHolder.newInstance(itemClass, jsonObject);
            }
            return PropertyHolder.newInstance(UItem.class, jsonObject);
        }else{
            return UniblendItems.INSTANCE.matchEntry(json.getAsString());
        }
    };

    @Property(value = "lore", type = Component.class,load = LoadType.LIST)
    private List<Component> lore = new ArrayList<>();
    @Property("material")
    private Material material = Material.APPLE;
    @Property("name-color")
    private TextColor nameColor = NamedTextColor.WHITE;
    @Property("name")
    private Component displayName;
    @Property("item-color")
    private TextColor itemColor;
    @Property("nbt")
    private String nbt = null;
    @Property("custom-model-data")
    private int customModelData = -1;
    @Property("amount")
    private int amount = 1;
    @Property("durability")
    private int durability = 0;
    @Property("bonus-only-mainhand")
    private boolean bonusOnlyMainHand = false;
    @Property(load = LoadType.CUSTOM)
    private Map<Enchantment,Integer> enchantments;
    @Property(load = LoadType.CUSTOM)
    private Map<Attribute,AttributeModifier> attributes;
    @Property(load = LoadType.CUSTOM)
    private JythonScript script;
    private String nameKey;
    @Override
    public void onCreate() {
        super.onCreate();
        enchantments = loadPropertyFunc("enchantments", JsonArray.class,(jsonList)->{
            Map<Enchantment, Integer> result = new HashMap<>();
            for (int i = 0; i < jsonList.size(); i++) {
                JsonObject enchantmentMap = jsonList.get(i).getAsJsonObject();
                result.put(USerialization.deserialize(enchantmentMap.get("enchantment"), Enchantment.class),enchantmentMap.get("lvl").getAsInt());
            }
            return result;
        },new HashMap<>());
        attributes = loadPropertyFunc("attributes", JsonArray.class,(jsonList)->{
            Map<Attribute, AttributeModifier> result = new HashMap<>();
            for (int i = 0; i < jsonList.size(); i++) {
                UItemAttribute attribute = USerialization.deserialize(jsonList.get(i), UItemAttribute.class);
                result.put(attribute.getAttribute(),attribute.getModifier());
            }
            return result;
        },new HashMap<>());
        script = loadPropertyFunc("script",String.class,(scriptStr)->{
            JythonScript script = new JythonScript(scriptStr,false);
            setScript(script);
            return script;
        },null);
    }

    public ItemMeta setupMeta(ItemStack itemStack, ItemMeta meta){
        return meta;
    }

    public List<Component> setupLore(ItemStack itemStack){
        return null;
    }

    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = setupMeta(itemStack,itemStack.getItemMeta());
        if (displayName == null) {
            if (nameKey != null) {
                meta.displayName(Component.translatable("item.uniblend." + nameKey).style(Style.style().color(nameColor).decoration(TextDecoration.ITALIC, false)));
            }
        }else{
            meta.displayName(displayName);
        }
        if (meta instanceof LeatherArmorMeta armorMeta){
            if (itemColor != null) {
                armorMeta.setColor(TextUtils.color(itemColor));
            }
        }
        if (customModelData != -1){
            meta.setCustomModelData(customModelData);
        }
        List<Component> builtinLore = setupLore(itemStack);
        if (builtinLore != null){
            meta.lore(builtinLore);
        }
        if (hasProperty("lore")) {
            List<Component> finalLore = new ArrayList<>();
            if (meta.lore() != null) {
                finalLore.addAll(meta.lore());
            }
            finalLore.addAll(lore);
            meta.lore(finalLore);
        }
        itemStack.setItemMeta(meta);
        if (durability != 0){
            itemStack.setDamage(durability);
        }
        if (amount != 1){
            itemStack.setAmount(amount);
        }
        itemStack = ItemUtils.setEnchantments(itemStack,enchantments);
        itemStack = ItemUtils.setAttributes(itemStack,attributes);
        if (nameKey != null) {
            itemStack.setItemMeta(NBTUtils.set(itemStack.getItemMeta(),"uniblend",nameKey));
        }
        if (nbt != null){
            if (Base64.isArrayByteBase64(nbt.getBytes())){
                itemStack = ItemUtils.addNbt(itemStack,new String(Base64.decodeBase64(nbt.getBytes())));
            }else{
                itemStack = ItemUtils.addNbt(itemStack,nbt);
            }
        }
        return itemStack;
    }

    @Override
    public void setScript(JythonScript script) {
        this.script = script;
        this.runFunc("init",null,this);
    }

    public Material getMaterial() {
        return material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public boolean isBonusOnlyMainHand() {
        return bonusOnlyMainHand;
    }
    @Override
    public JythonScript getScript() {
        return script;
    }
    public String getNameKey(){
        return nameKey;
    }
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }
}
