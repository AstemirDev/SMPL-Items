package org.astemir.uniblend.core.item;

import com.google.gson.JsonObject;
import org.astemir.uniblend.core.item.parent.builtin.armor.YurvelineArmor;
import org.astemir.uniblend.core.item.parent.builtin.bows.CursedWatcher;
import org.astemir.uniblend.core.item.parent.builtin.melee.*;
import org.astemir.uniblend.core.item.parent.builtin.other.Cigarette;
import org.astemir.uniblend.core.item.parent.builtin.tools.YurvelineHoe;
import org.astemir.uniblend.core.item.parent.builtin.tools.YurvelineTool;
import org.astemir.uniblend.core.item.command.CommandUItem;
import org.astemir.uniblend.core.item.parent.*;
import org.astemir.uniblend.io.json.PluginJsonConfig;
import org.astemir.uniblend.io.json.USerialization;
import org.astemir.uniblend.core.Registered;
import org.astemir.uniblend.core.UniblendRegistry;
import org.astemir.uniblend.core.item.parent.builtin.armor.InfernalWing;
import org.astemir.uniblend.core.item.parent.builtin.bows.Ragnarok;
import org.astemir.uniblend.core.item.parent.builtin.bows.Trailblazer;
import org.astemir.uniblend.core.item.parent.builtin.crossbows.Firestorm;
import org.astemir.uniblend.core.item.parent.builtin.crossbows.HolyWrath;
import org.astemir.uniblend.core.item.parent.builtin.crossbows.MultiplexCrossbow;
import org.astemir.uniblend.core.item.parent.builtin.crossbows.SentrysWrath;
import org.astemir.uniblend.core.item.parent.builtin.other.UnstablePowder;
import org.astemir.uniblend.core.item.parent.builtin.shields.NecroticShield;
import org.astemir.uniblend.core.item.parent.builtin.shields.PrismaticShield;
import org.astemir.uniblend.core.item.parent.builtin.shields.RadiationShield;
import org.astemir.uniblend.core.item.parent.builtin.tools.HeftyPickaxe;
import org.astemir.uniblend.core.item.parent.builtin.tridents.VoltaicTrident;
import org.astemir.uniblend.core.item.event.GemEventListener;
import org.astemir.uniblend.core.item.event.UItemEventListener;
import org.astemir.uniblend.core.entity.utils.EntityUtils;
import org.astemir.uniblend.utils.NBTUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Registered("items")
public class UniblendItems extends UniblendRegistry.Default<UItem>{

    public static UniblendItems INSTANCE;

    public UniblendItems() {
        INSTANCE = this;
    }

    @Override
    public void onRegister() {
        registerEvent(new UItemEventListener());
        registerEvent(new GemEventListener());
        registerCommand(new CommandUItem());
    }

    @Override
    public void onSetupLookups() {
        setLookup("item", UItem.class);
        setLookup("item_armor", UItemArmor.class);
        setLookup("item_bow", UItemBow.class);
        setLookup("item_food", UItemFood.class);
        setLookup("item_sword", UItemSword.class);
        setLookup("item_boss_spawn", UItemBossSpawn.class);
        setLookup("infernal_wing", InfernalWing.class);
        setLookup("ragnarok", Ragnarok.class);
        setLookup("cursed_watcher", CursedWatcher.class);
        setLookup("trailblazer", Trailblazer.class);
        setLookup("firestorm", Firestorm.class);
        setLookup("holy_wrath", HolyWrath.class);
        setLookup("multiplex_crossbow", MultiplexCrossbow.class);
        setLookup("sentrys_wrath", SentrysWrath.class);
        setLookup("blazing_hatchet", BlazingHatchet.class);
        setLookup("chilling_blade", ChillingBlade.class);
        setLookup("daybreaker", Daybreaker.class);
        setLookup("sacrifice_sword", SacrificeSword.class);
        setLookup("bloodmourner", BloodMourner.class);
        setLookup("scarlet_dagger", ScarletDagger.class);
        setLookup("wither_blade", WitherBlade.class);
        setLookup("withersbane", WithersBane.class);
        setLookup("unstable_powder", UnstablePowder.class);
        setLookup("cigarette", Cigarette.class);
        setLookup("necrotic_shield", NecroticShield.class);
        setLookup("prismatic_shield", PrismaticShield.class);
        setLookup("radiation_shield", RadiationShield.class);
        setLookup("hefty_pickaxe", HeftyPickaxe.class);
        setLookup("voltaic_trident", VoltaicTrident.class);
        setLookup("equipment_gem", UItemSocketGem.class);
        setLookup("yurveline_armor", YurvelineArmor.class);
        setLookup("yurveline_sword", YurvelineSword.class);
        setLookup("yurveline_tool", YurvelineTool.class);
        setLookup("yurveline_hoe", YurvelineHoe.class);
    }

    @Override
    public void onConfigLoad(List<PluginJsonConfig> configs) {
        clear();
        for (PluginJsonConfig config : configs) {
            JsonObject map = config.json();
            for (String key : map.keySet()) {
                register(key, USerialization.deserialize(map.get(key), UItem.class));
            }
        }
    }

    @Override
    public void onUpdatePerPlayer(Player player, long tick) {
        ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand(),player.getInventory().getItemInOffHand(),player.getInventory().getHelmet(),player.getInventory().getChestplate(),player.getInventory().getLeggings(),player.getInventory().getBoots()};
        for (ItemStack itemStack : items) {
            UItem item = UniblendItems.getItem(itemStack);
            if (item != null) {
                item.onTick(player,itemStack,tick);
            }
        }
    }


    public static boolean isItem(ItemStack itemStack){
        return getItem(itemStack) != null;
    }

    public static boolean isItem(ItemStack itemStack, Class<? extends UItem> itemClass){
        if (itemStack != null) {
            UItem compare = getItem(itemStack);
            if (compare != null) {
                if (itemClass.isInstance(compare) || compare.getClass() == itemClass) {
                    return true;
                }
            }
        }
        return false;
    }

    public static UItem getItem(ItemStack item){
        if (item == null){
            return null;
        }
        if (item.hasItemMeta()) {
            for (UItem itemSMPL : INSTANCE.getEntries()) {
                String id = getItemID(item);
                if (id != null){
                    if (itemSMPL.getNameKey().equals(id)){
                        return itemSMPL;
                    }
                }
            }
        }
        return null;
    }

    public static String getItemID(ItemStack itemStack){
        if (itemStack != null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    return NBTUtils.getOr(meta, "uniblend", String.class,
                            NBTUtils.get(meta, NBTUtils.key("smplcore", "smplid"), String.class));
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static boolean isFullSet(LivingEntity entity,Class<? extends UItem> setClass){
        ItemStack helmet = entity.getEquipment().getItem(EquipmentSlot.HEAD);
        ItemStack chestplate = entity.getEquipment().getItem(EquipmentSlot.CHEST);
        ItemStack leggings = entity.getEquipment().getItem(EquipmentSlot.LEGS);
        ItemStack boots = entity.getEquipment().getItem(EquipmentSlot.FEET);
        if (UniblendItems.isItem(helmet,setClass)
                && UniblendItems.isItem(chestplate,setClass)
                && UniblendItems.isItem(leggings,setClass)
                && UniblendItems.isItem(boots,setClass)){
            return true;
        }
        return false;
    }

    public static UItem[] getArmorItems(LivingEntity entity){
        return new UItem[]{getItemInSlot(entity,EquipmentSlot.HEAD),getItemInSlot(entity,EquipmentSlot.CHEST),getItemInSlot(entity,EquipmentSlot.LEGS),getItemInSlot(entity,EquipmentSlot.FEET)};
    }

    public static Map<EquipmentSlot,UItem> getCustomEquipment(LivingEntity entity){
        Map<EquipmentSlot,UItem> equipment = new HashMap<>();
        equipment.put(EquipmentSlot.HEAD,getItemInSlot(entity,EquipmentSlot.HEAD));
        equipment.put(EquipmentSlot.CHEST,getItemInSlot(entity,EquipmentSlot.CHEST));
        equipment.put(EquipmentSlot.LEGS,getItemInSlot(entity,EquipmentSlot.LEGS));
        equipment.put(EquipmentSlot.FEET,getItemInSlot(entity,EquipmentSlot.FEET));
        equipment.put(EquipmentSlot.HAND,getItemInSlot(entity,EquipmentSlot.HAND));
        equipment.put(EquipmentSlot.OFF_HAND,getItemInSlot(entity,EquipmentSlot.OFF_HAND));
        return equipment;
    }

    public static UItem[] getFullItems(LivingEntity entity){
        return new UItem[]{getItemInSlot(entity,EquipmentSlot.HEAD),getItemInSlot(entity,EquipmentSlot.CHEST),getItemInSlot(entity,EquipmentSlot.LEGS),getItemInSlot(entity,EquipmentSlot.FEET),getItemInSlot(entity,EquipmentSlot.HAND),getItemInSlot(entity,EquipmentSlot.OFF_HAND)};
    }

    public static UItem[] getItemsInBothHands(LivingEntity livingEntity){
        return new UItem[]{getItemInSlot(livingEntity,EquipmentSlot.HAND),getItemInSlot(livingEntity,EquipmentSlot.OFF_HAND)};
    }

    public static UItem getItemInSlot(LivingEntity livingEntity, EquipmentSlot slot){
        return getItem(EntityUtils.getItemInSlot(livingEntity,slot));
    }
}
