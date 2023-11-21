package org.astemir.uniblend.core.setbonus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.astemir.uniblend.io.json.UJsonDeserializer;
import org.astemir.uniblend.core.Named;
import org.astemir.uniblend.core.item.parent.UItem;
import org.astemir.uniblend.core.item.parent.UItemArmor;
import org.astemir.uniblend.core.item.UniblendItems;
import org.astemir.uniblend.io.json.JsonUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class USetBonus implements Named {

    public static final UJsonDeserializer<USetBonus> DESERIALIZER = (json)->{
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray requiredItemsJson = jsonObject.getAsJsonArray("required-items");
            JsonArray potionEffects = jsonObject.getAsJsonArray("potion-effects");
            return new USetBonus(JsonUtils.listString(requiredItemsJson),JsonUtils.list(potionEffects,PotionEffect.class));
        }
        return null;
    };

    private List<String> equipmentIds;
    private List<PotionEffect> bonusEffects;
    private String name;

    public USetBonus(List<String> equipmentIds, List<PotionEffect> bonusEffects) {
        this.equipmentIds = equipmentIds;
        this.bonusEffects = bonusEffects;
    }

    public boolean canAffect(LivingEntity livingEntity) {
        for (String equipmentId : equipmentIds) {
            UItem itemSMPL = UniblendItems.INSTANCE.matchEntry(equipmentId);
            if (itemSMPL == null) {
                return false;
            }

            if (itemSMPL instanceof UItemArmor armorSmpl) {
                if (UniblendItems.getItemInSlot(livingEntity, armorSmpl.getEquipmentSlot()) != itemSMPL) {
                    return false;
                }
            } else {
                UItem[] items = UniblendItems.getItemsInBothHands(livingEntity);
                if (!(itemSMPL == items[0] || itemSMPL == items[1])) {
                    return false;
                }
                if (itemSMPL.isBonusOnlyMainHand() && itemSMPL != items[0]) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canAffect(LivingEntity livingEntity, UItem ignored) {
        for (String equipmentId : equipmentIds) {
            if (!equipmentId.equals(ignored.getNameKey())) {
                UItem itemSMPL = UniblendItems.INSTANCE.matchEntry(equipmentId);
                if (itemSMPL == null) {
                    return false;
                }
                if (itemSMPL instanceof UItemArmor armorSmpl) {
                    if (UniblendItems.getItemInSlot(livingEntity, armorSmpl.getEquipmentSlot()) != itemSMPL) {
                        return false;
                    }
                } else {
                    UItem[] items = UniblendItems.getItemsInBothHands(livingEntity);
                    if (!(itemSMPL == items[0] || itemSMPL == items[1])) {
                        return false;
                    }
                    if (itemSMPL.isBonusOnlyMainHand() && itemSMPL != items[0]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean isSetItem(UItem itemSMPL){
        for (String equipmentId : equipmentIds) {
            if (itemSMPL.getNameKey().equals(equipmentId)){
                return true;
            }
        }
        return false;
    }

    public void cure(LivingEntity livingEntity){
        for (PotionEffect bonusEffect : bonusEffects) {
            livingEntity.removePotionEffect(bonusEffect.getType());
        }
    }

    public void affect(LivingEntity livingEntity){
        if (canAffect(livingEntity)){
            for (PotionEffect bonusEffect : bonusEffects) {
                livingEntity.addPotionEffect(bonusEffect);
            }
        }
    }

    @Override
    public String getNameKey() {
        return name;
    }

    @Override
    public void setNameKey(String key) {
        this.name = key;
    }
}